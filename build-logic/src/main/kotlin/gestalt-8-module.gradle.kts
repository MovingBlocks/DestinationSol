plugins {
    id("gestalt-module")
}

val gestaltVersion = extra["gestaltVersion"] as String

tasks.compileJava {
    // Specify the module's assets as pre-requisites for compilation, so that they force re-compilation when they change.
    inputs.files(sourceSets.main.get().resources.srcDirs)
    // Asset lists are cached by gestalt-di's annotation processors, which run just before compilation begins.
    // Without these manifests, or with outdated manifests, the game won't know what assets are present.
    options.compilerArgs = listOf("-Aresource=${sourceSets.main.get().resources.srcDirs.joinToString(File.pathSeparator)}")
}
tasks.compileTestJava {
    // See above comments.
    inputs.files(sourceSets.test.get().resources.srcDirs)
    options.compilerArgs = listOf("-Aresource=${sourceSets.test.get().resources.srcDirs.joinToString(File.pathSeparator)}")
}

dependencies {
    "annotationProcessor"("org.terasology.gestalt:gestalt-inject-java:$gestaltVersion")
}
