// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

// This magically allows subdirs to become included builds
// https://docs.gradle.org/6.4.1/userguide/composite_builds.html
file(".").listFiles { f -> f.isDirectory }?.forEach { possibleIncludedBuildDirectory ->
    val buildFile = File(possibleIncludedBuildDirectory, "build.gradle")
    val buildFileKts = File(possibleIncludedBuildDirectory, "build.gradle.kts")
    val settingsFile = File(possibleIncludedBuildDirectory, "settings.gradle")
    val settingsFileKts = File(possibleIncludedBuildDirectory, "settings.gradle.kts")

    if ((buildFile.exists() || buildFileKts.exists()) && (settingsFile.exists() || settingsFileKts.exists())) {
        logger.info("{} will be included in the composite build.",
            rootDir.toPath().relativize(possibleIncludedBuildDirectory.toPath()))
        includeBuild(possibleIncludedBuildDirectory)
    } else {
        logger.warn("{} REJECTED as an included build. build.gradle: {}, settings.gradle: {}",
            rootDir.toPath().relativize(possibleIncludedBuildDirectory.toPath()),
            if (buildFile.exists()) "present" else "MISSING",
            if (settingsFile.exists()) "present" else "MISSING"
        )
    }
}
