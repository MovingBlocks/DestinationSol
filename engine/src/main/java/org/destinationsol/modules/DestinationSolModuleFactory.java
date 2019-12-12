/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.modules;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModuleMetadata;
import org.terasology.gestalt.module.exceptions.InvalidModulePathException;
import org.terasology.gestalt.module.resources.CompositeFileSource;
import org.terasology.gestalt.module.resources.DirectoryFileSource;
import org.terasology.gestalt.module.resources.ModuleFileSource;
import org.terasology.gestalt.util.Varargs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DestinationSolModuleFactory extends ModuleFactory {
    private String reflectionsCachePath = "reflections.cache";

    /**
     * Creates a module for assets that are on the classpath - that is, they are already loaded and available in the root java context.
     *
     * @param paths    The paths for jars and code paths to include in the module
     * @param metadata Metadata describing the module.
     * @return A new module for the specified locations of the classpath.
     */
    public Module createClasspathModule(Collection<Path> paths, ModuleMetadata metadata) {
        Reflections reflectionsCache = null;
        ImmutableList.Builder<File> builder = ImmutableList.builder();
        List<ModuleFileSource> fileSources = new ArrayList<ModuleFileSource>();
        for (Path path : paths) {
            try {
                File pathFile = new File(path.toUri());
                builder.add(pathFile);
                Reflections reflection;
                if (Files.isDirectory(path)) {
                    reflection = readReflectionsCacheFromPath(path.resolve("java/main"), reflectionsCache);
                    fileSources.add(new DirectoryFileSource(pathFile));
                } else {
                    reflection = readReflectionsCacheFromArchive(path, reflectionsCache);
                    fileSources.add(new OptimisedArchiveFileSource(pathFile));
                }

                if (reflection == null) {
                    reflection = new Reflections(new ConfigurationBuilder()
                            .addClassLoader(ClassLoader.getSystemClassLoader())
                            .addUrls(path.toUri().toURL())
                            .addScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
                }

                if (reflectionsCache == null) {
                    reflectionsCache = reflection;
                } else {
                    reflectionsCache.merge(reflection);
                }
            } catch (Exception e) {
                throw new InvalidModulePathException("Path cannot be converted to URL: " + path, e);
            }
        }
        //return new Module(paths, builder.build(), true, metadata, reflectionsCache);
        return new Module(metadata, new CompositeFileSource(fileSources), builder.build(), reflectionsCache, x -> true);
    }

    /**
     * Creates a classpath module from a set of representative classes. The code source (e.g. Jar or directory) for each class is included in the Classpath module.
     * <p>
     * There is an option to include directories on the classpath. This should only be done for one classpath module - this is for use when running from source
     * in environments that keep resources and classes in separate locations (e.g. gradle by default). Any directory on the classpath (as opposed to jars) will be
     * included in this module
     * </p>
     *
     * @param metadata           Metadata describing the module to create
     * @param includeDirectories Should directories on the classpath be included in this module?
     * @param primaryClass       The first representative class to include in the module
     * @param additionalClasses  Any additional representative classes to include.
     * @return A new ClasspathModule
     * @throws URISyntaxException If a source location cannot be converted to a proper URI (typically because the path to the source includes an invalid character).
     */
    public Module createClasspathModule(ModuleMetadata metadata, boolean includeDirectories, Class<?> primaryClass, Class<?>... additionalClasses) throws URISyntaxException {
        Set<Path> paths = Sets.newLinkedHashSet();

        for (Class<?> type : Varargs.combineToSet(primaryClass, additionalClasses)) {
            paths.add(Paths.get(type.getProtectionDomain().getCodeSource().getLocation().toURI()));
        }
        if (includeDirectories) {
            addClasspathDirectories(paths);
        }
        return createClasspathModule(paths, metadata);
    }

    /**
     * Loads an archive (zip) module. This module may contain compiled code (e.g. could be a jar).
     *
     * @param metadata The metadata describing the module
     * @param archive  The archive file
     * @return The loaded module
     * @throws IOException If there is an issue loading the module
     */
    @Override
    public Module createArchiveModule(ModuleMetadata metadata, File archive) throws IOException {
        Module newModule = super.createArchiveModule(metadata, archive);

        try {
            return new Module(newModule.getMetadata(), new OptimisedArchiveFileSource(archive), Collections.singletonList(archive), newModule.getModuleManifest(), x -> false);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to convert file path to url for " + archive, e);
        }
    }

    private void addClasspathDirectories(Set<Path> paths) {
        for (String classpath : System.getProperty("java.class.path").split(File.pathSeparator)) {
            Path path = Paths.get(classpath);
            if (Files.isDirectory(path)) {
                paths.add(path);
            }
        }
    }

    private Reflections readReflectionsCacheFromPath(Path path, Reflections reflectionsCache) {
        Path reflectionsCacheFile = path.resolve(reflectionsCachePath);
        if (Files.isRegularFile(reflectionsCacheFile)) {
            try (InputStream stream = new BufferedInputStream(Files.newInputStream(path.resolve(reflectionsCachePath)))) {
                if (reflectionsCache == null) {
                    reflectionsCache = new ConfigurationBuilder().getSerializer().read(stream);
                } else {
                    reflectionsCache.collect(stream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reflectionsCache;
    }

    private Reflections readReflectionsCacheFromArchive(Path path, Reflections reflectionsCache) {
        try (ZipFile zipFile = new ZipFile(path.toFile())) {
            ZipEntry modInfoEntry = zipFile.getEntry(reflectionsCachePath.toString());
            if (modInfoEntry != null) {
                try (InputStream stream = zipFile.getInputStream(modInfoEntry)) {
                    if (reflectionsCache == null) {
                        reflectionsCache = new ConfigurationBuilder().getSerializer().read(stream);
                    } else {
                        reflectionsCache.collect(stream);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reflectionsCache;
    }
}
