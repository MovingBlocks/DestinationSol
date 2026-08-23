// This magically allows subdirs in this subproject to themselves become sub-subprojects in a proper tree structure
File(rootDir, "modules").listFiles { file -> file.isDirectory }?.forEach { possibleSubprojectDir ->
    if (!possibleSubprojectDir.name.startsWith(".")) {
        val subprojectName = "modules:" + possibleSubprojectDir.name
        //println("Gradle is reviewing module $subprojectName for inclusion as a sub-project")
        val buildFile = File(possibleSubprojectDir, "build.gradle")
        val buildFileKts = File(possibleSubprojectDir, "build.gradle.kts")
        if (buildFile.exists() || buildFileKts.exists()) {
            println("Module $subprojectName has a build file so counting it complete and including it")
        } else {
            println("***** WARNING: Found a module without a build.gradle.kts, Adding a build.gradle.kts to $subprojectName. *****")
            copy {
                from("${rootProject.projectDir}/templates")
                into(possibleSubprojectDir)
                include("build.gradle.kts")
            }
        }

        include(subprojectName)
        val subprojectPath = ":$subprojectName"
        project(subprojectPath).projectDir = possibleSubprojectDir
    } else {
        println("Ignoring path for module consideration: $possibleSubprojectDir")
    }
}
