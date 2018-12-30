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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.terasology.module.Module;
import org.terasology.module.ModuleFactory;
import org.terasology.module.ModuleMetadata;
import org.terasology.module.exceptions.InvalidModulePathException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DestinationSolModuleFactory extends ModuleFactory {
    /**
     * Creates a module for a directory.
     *
     * @param path     The path of the directory to make a module for
     * @param codePath The path of code to add to the classpath of the module
     * @param libsPath The path of any jars to add to the classpath of the module
     * @param metadata The metadata describing the module
     * @return A new module for the given directory
     */
    @Override
    public Module createPathModule(Path path, Path codePath, Path libsPath, ModuleMetadata metadata) {
        Module originalModule = super.createPathModule(path, codePath, libsPath, metadata);

        if (originalModule.isCodeModule() && !reflectionCacheExists(path)) {
            Reflections reflections = null;
            try {
                Predicate<String> classFilter = filePath -> filePath != null && filePath.endsWith(".class");
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                        .addUrls(path.toUri().toURL(), codePath.toUri().toURL())
                        .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
                configurationBuilder.setInputsFilter(classFilter);
                reflections = new Reflections(configurationBuilder);
            } catch (Exception ignore) {
            }

            return new Module(originalModule.getLocations(), originalModule.getClasspaths(), metadata, reflections);
        } else {
            return originalModule;
        }
    }

    /**
     * Creates an archive module for a jar or zip file module.
     *
     * @param path     The path of the module - must be a file.
     * @param metadata The metadata describing the module.
     * @return A new module for the archive.
     */
    @Override
    public Module createArchiveModule(Path path, ModuleMetadata metadata) {
        Module originalModule = super.createArchiveModule(path, metadata);

        if (originalModule.isCodeModule() && !reflectionCacheExists(path)) {
            Reflections reflections = null;
            try {
                Predicate<String> classFilter = filePath -> filePath != null && filePath.endsWith(".class");
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                        .addUrls(path.toUri().toURL())
                        .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
                configurationBuilder.setInputsFilter(classFilter);
                reflections = new Reflections(configurationBuilder);
            } catch (Exception ignore) {
            }

            return new Module(originalModule.getLocations(), originalModule.getClasspaths(), metadata, reflections);
        } else {
            return originalModule;
        }
    }

    /**
     * Creates a module for assets that are on the classpath - that is, they are already loaded and available in the root java context.
     *
     * @param paths    The paths for jars and code paths to include in the module
     * @param metadata Metadata describing the module.
     * @return A new module for the specified locations of the classpath.
     */
    @Override
    public Module createClasspathModule(Collection<Path> paths, ModuleMetadata metadata) {
        Module originalModule = super.createClasspathModule(paths, metadata);

        if (originalModule.isCodeModule() && !reflectionCacheExists(paths.toArray(new Path[paths.size()]))) {
            Reflections reflections = null;
            try {
                Predicate<String> classFilter = filePath -> filePath != null && filePath.endsWith(".class");
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                        .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner());
                configurationBuilder.setInputsFilter(classFilter);
                for (Path path : paths) {
                    configurationBuilder.addUrls(path.toUri().toURL());
                }
                reflections = new Reflections(configurationBuilder);
            } catch (Exception ignore) {
            }

            return new Module(originalModule.getLocations(), originalModule.getClasspaths(), metadata, reflections);
        } else {
            return originalModule;
        }
    }

    private boolean reflectionCacheExists(Path... paths) {
        for (Path path : paths) {
            Path reflectionsCacheFile = path.resolve(getReflectionsCachePath());
            if (Files.exists(reflectionsCacheFile)) {
                return true;
            }
        }

        return false;
    }
}
