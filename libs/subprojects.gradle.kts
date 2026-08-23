// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

// This magically allows subdirs to become included builds
// https://docs.gradle.org/6.4.1/userguide/composite_builds.html
// This script is apply(from = ...)'d from settings.gradle.kts, so file(".") here would resolve
// against settingsDir (the repo root), not against libs/ where this script itself lives - scan
// libs/ explicitly instead.
File(rootDir, "libs").listFiles { f -> f.isDirectory }?.forEach { possibleIncludedBuildDirectory ->
    val buildFile = File(possibleIncludedBuildDirectory, "build.gradle")
    val buildFileKts = File(possibleIncludedBuildDirectory, "build.gradle.kts")
    val settingsFile = File(possibleIncludedBuildDirectory, "settings.gradle")
    val settingsFileKts = File(possibleIncludedBuildDirectory, "settings.gradle.kts")

    if ((buildFile.exists() || buildFileKts.exists()) && (settingsFile.exists() || settingsFileKts.exists())) {
        logger.info("{} will be included in the composite build.",
            rootDir.toPath().relativize(possibleIncludedBuildDirectory.toPath()))
        includeBuild(possibleIncludedBuildDirectory)
    } else {
        logger.warn("{} REJECTED as an included build. build.gradle(.kts): {}, settings.gradle(.kts): {}",
            rootDir.toPath().relativize(possibleIncludedBuildDirectory.toPath()),
            if (buildFile.exists() || buildFileKts.exists()) "present" else "MISSING",
            if (settingsFile.exists() || settingsFileKts.exists()) "present" else "MISSING"
        )
    }
}
