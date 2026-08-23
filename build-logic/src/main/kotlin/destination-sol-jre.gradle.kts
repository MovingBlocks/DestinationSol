/*
 * Copyright 2022 The Terasology Foundation
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

import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.plugins.BasePluginExtension

plugins {
    id("de.undercouch.download")
}

// The `base` plugin (bringing BasePluginExtension) is applied transitively by whatever
// project consumes this script (e.g. via destination-sol-java), not by this script itself,
// so the type-safe `base { }` accessor isn't available here - look it up explicitly instead.
val distsDirectory = the<BasePluginExtension>().distsDirectory

// Uses Bellsoft Liberica JRE. Must stay on a Java 17+ build - the engine compiles with
// options.release = 17 (see destination-sol-java.gradle.kts), and a Java 11 runtime can't
// load Java 17 class files (UnsupportedClassVersionError).
val jreVersion = "17.0.12+10"
val jreUrlBase = "https://download.bell-sw.com/java/$jreVersion/bellsoft-jre$jreVersion"
val jreUrlFilenames = mapOf(
    "lwjreLinux64" to "linux-amd64.tar.gz",
    // 32-bit Windows dropped from JDK 12+ builds; windows-amd64 is the closest equivalent to
    // the old windows-i586 key.
    "lwjre" to "windows-amd64.zip",
    "lwjreOSX" to "macos-amd64.zip",
    "lwjreOSXArm" to "macos-aarch64.zip"
)

val downloadJreAll by tasks.registering {
    group = "Download"
    description = "Downloads JRE for all platforms"
}

jreUrlFilenames.forEach { (os, file) ->
    val downloadTask = tasks.register("downloadJre$os") {
        group = "Download"
        description = "Downloads JRE for $os"

        val packedJre = File("$rootDir/jre/$jreVersion/$file")
        val unpackedJre = distsDirectory.dir("app/$os").get().asFile

        doFirst {
            DownloadAction(project).apply {
                src("$jreUrlBase-$file")
                dest(packedJre)
                overwrite(false)
            }.execute()
        }

        doLast {
            // Unpack the JRE
            if (!unpackedJre.exists()) {
                unpackedJre.mkdirs()
                copy {
                    from(if (file.endsWith("zip")) zipTree(packedJre) else tarTree(packedJre)) {
                        eachFile {
                            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
                        }
                        includeEmptyDirs = false
                    }
                    into(unpackedJre)
                }
            }
        }
    }

    downloadJreAll.configure { dependsOn(downloadTask) }
}
