includeBuild("build-logic")

include("desktop", "engine", "modules")

if (File(rootDir, "steam/build.gradle").exists() || File(rootDir, "steam/build.gradle.kts").exists()) {
    include("steam")
}

if (File(rootDir, "gwt/build.gradle").exists() || File(rootDir, "gwt/build.gradle.kts").exists()) {
    include("gwt")
}

rootProject.name = "DestinationSol"

// Handy little snippet found online that'll "fake" having nested settings.gradle files under /modules, /libs, etc
rootDir.listFiles { file -> file.isDirectory }?.forEach { possibleSubprojectDir ->

    // First scan through all subdirs that has a subprojects.gradle.kts in it and apply that script (recursive search!)
    possibleSubprojectDir.listFiles { file -> file.isFile && file.name == "subprojects.gradle.kts" }
        ?.forEach { subprojectsSpecificationScript ->
            //println("Magic is happening, applying from $subprojectsSpecificationScript")
            apply(from = subprojectsSpecificationScript)
        }
}

// This is put last to ensure that Android can detect the modules
// Checks both names: Jenkins checks out DestSolAndroid's own develop branch here, which may or
// may not have been converted to Kotlin DSL independently of this repo.
if (File(rootDir, "android/build.gradle").exists() || File(rootDir, "android/build.gradle.kts").exists()) {
    include("android")
}
