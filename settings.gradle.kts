includeBuild("build-logic")

include("desktop", "engine", "modules")

val steamGradle = File(rootDir, "steam/build.gradle")
if (steamGradle.exists()) {
    include("steam")
}

val gwtGradle = File(rootDir, "gwt/build.gradle")
if (gwtGradle.exists()) {
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
val androidGradle = File(rootDir, "android/build.gradle.kts")
if (androidGradle.exists()) {
    include("android")
}
