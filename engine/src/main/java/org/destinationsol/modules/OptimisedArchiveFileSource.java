/*
 * Copyright 2019 MovingBlocks
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

import java.io.IOException;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import org.terasology.module.resources.FileReference;
import org.terasology.module.resources.ModuleFileSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A ModuleFileSource that exposes the content of an archive file (zip/jar).
 * This class is mostly a copy of ArchiveFileSource but uses ZipFile rather then ZipInputStream.
 * @see org.terasology.module.resources.ArchiveFileSource
 */
public class OptimisedArchiveFileSource implements ModuleFileSource {
    private static final String PATH_SEPARATOR = "/";
    private static final Joiner PATH_JOINER = Joiner.on(PATH_SEPARATOR);

    private final Map<String, FileReference> contents = Maps.newLinkedHashMap();
    private final SetMultimap<List<String>, String> subpaths = HashMultimap.create();

    /**
     * Creates an archive file source over the given archive file. All .class files in the archive
     * will be ignored
     *
     * @param file    The archive file to load
     * @param subpath The path within the archive to use as the base of the exposed files
     * @throws IOException If there is any issue reading the archive file
     */
    public OptimisedArchiveFileSource(File file, String... subpath) throws IOException {
        this(file, (x -> !x.endsWith(".class")), subpath);
    }

    /**
     * Creates an archive file source over the given archive file.
     *
     * @param file           The archive file to load
     * @param contentsFilter A predicate to use to filter what files to expose
     * @param subpath        The path within the archive to use as the base of the exposed files
     * @throws IOException If there is any issue reading the archive file
     */
    public OptimisedArchiveFileSource(File file, Predicate<String> contentsFilter, String... subpath) throws IOException {
        String basePath = buildPathString(Arrays.asList(subpath));
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() && entry.getName().startsWith(basePath)) {
                    List<String> pathParts = Arrays.asList(entry.getName().substring(basePath.length()).split(PATH_SEPARATOR));
                    if (!pathParts.get(0).isEmpty()) {
                        subpaths.put(pathParts.subList(0, pathParts.size() - 1), pathParts.get(pathParts.size() - 1));
                    }
                } else if (entry.getName().startsWith(basePath)) {
                    OptimisedArchiveFileReference archiveFile = new OptimisedArchiveFileReference(file, entry.getName(), basePath);
                    if (contentsFilter.test(archiveFile.getName())) {
                        contents.put(entry.getName().substring(basePath.length()), archiveFile);
                    }
                }
            }
        }
    }

    private String buildPathString(List<String> subpath) {
        StringBuilder basePathBuilder = new StringBuilder();
        PATH_JOINER.appendTo(basePathBuilder, subpath);
        if (basePathBuilder.length() > 0) {
            basePathBuilder.append(PATH_SEPARATOR);
        }
        return basePathBuilder.toString();
    }

    @Override
    public Optional<FileReference> getFile(List<String> filepath) {
        return Optional.ofNullable(contents.get(PATH_JOINER.join(filepath)));
    }

    @Override
    public Collection<FileReference> getFiles() {
        return Collections.unmodifiableCollection(contents.values());
    }

    @Override
    public Collection<FileReference> getFilesInPath(boolean recursive, List<String> path) {
        String basePath = buildPathString(path);
        return contents.entrySet().stream()
                .filter(x -> x.getKey().startsWith(basePath) && (recursive || !x.getKey().substring(basePath.length()).contains(PATH_SEPARATOR)))
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Override
    public Set<String> getSubpaths(List<String> fromPath) {
        return subpaths.get(fromPath);
    }

    @Override
    public Iterator<FileReference> iterator() {
        return contents.values().iterator();
    }

    private static class OptimisedArchiveFileReference implements FileReference {
        private File zipFile;
        private String internalFile;
        private String basePath;

        OptimisedArchiveFileReference(File zipFile, String internalFile, String basePath) {
            this.zipFile = zipFile;
            this.internalFile = internalFile;
            this.basePath = basePath;
        }

        @Override
        public String getName() {
            int lastPathSeparator = internalFile.lastIndexOf(PATH_SEPARATOR);
            if (lastPathSeparator >= 0) {
                return internalFile.substring(lastPathSeparator + 1);
            }
            return internalFile;
        }

        @Override
        public List<String> getPath() {
            List<String> parts = Arrays.asList(internalFile.substring(basePath.length()).split(PATH_SEPARATOR));
            return parts.subList(0, parts.size() - 1);
        }

        @Override
        public InputStream open() throws IOException {
            ZipFile zip = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals(internalFile)) {
                    return zip.getInputStream(entry);
                }
            }
            throw new FileNotFoundException("Could not find file " + internalFile + " in " + zipFile.getPath());
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public int hashCode() {
            return Objects.hash(zipFile, internalFile, basePath);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (o instanceof OptimisedArchiveFileReference) {
                OptimisedArchiveFileReference other = (OptimisedArchiveFileReference) o;
                return Objects.equals(zipFile, other.zipFile) && Objects.equals(internalFile, other.internalFile) && Objects.equals(basePath, other.basePath);
            }

            return false;
        }
    }
}
