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

import groovy.xml.XmlNodePrinter
import groovy.xml.XmlParser
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import java.io.FileWriter
import java.io.PrintWriter

plugins {
    id("destination-sol-common")
    id("destination-sol-jre")
}

val gdxVersion = extra["gdxVersion"] as String
val gestaltVersion = extra["gestaltVersion"] as String
val gdxControllersVersion = extra["gdxControllersVersion"] as String
val appName = extra["appName"] as String

val mainClassName = "org.destinationsol.desktop.SolDesktop"
extra["mainClassName"] = mainClassName

dependencies {
    implementation(project(":engine"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-desktop:$gdxControllersVersion")

    implementation(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.25")
    implementation(group = "org.terasology.crashreporter", name = "cr-destsol", version = "4.0.0")
    annotationProcessor("org.terasology.gestalt:gestalt-inject-java:$gestaltVersion")
}

tasks.withType<ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer>().configureEach {
    // The desktop facade does not run on Android, so we do not have to fulfil its constraints.
    exclude("**/*")
}

val distsDirectory = the<BasePluginExtension>().distsDirectory

tasks.register<JavaExec>("run") {
    dependsOn(tasks.classes)
    //TODO: Remove extra args when the splash screen works on Macs again - see https://github.com/MovingBlocks/DestinationSol/issues/414
    if (System.getProperty("os.name").lowercase().contains("mac")) {
        jvmArgs = listOf("-splash:../engine/src/main/resources/assets/textures/mainMenu/mainMenuLogo.png", "-XstartOnFirstThread", "-Dlog4j.configuration=log4j-debug.properties")
        args("-noSplash")
    } else {
        jvmArgs = listOf("-splash:../engine/src/main/resources/assets/textures/mainMenu/mainMenuLogo.png", "-Dlog4j.configuration=log4j-debug.properties")
    }
    mainClass.set(mainClassName)
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
    workingDir = rootProject.projectDir
    isIgnoreExitValue = true
}

tasks.jar {
    archiveFileName.set("solDesktop.jar")

    manifest {
        val manifestClasspath = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
        attributes("Main-Class" to mainClassName)
        attributes("Class-Path" to manifestClasspath)
        attributes("SplashScreen-Image" to "mainMenuLogo.png")
    }
}

tasks.register<Copy>("moduleDist") {
    into("${distsDirectory.get().asFile}/app/modules")
    @Suppress("UNCHECKED_CAST")
    val destinationSolModules = rootProject.extra["destinationSolModules"] as () -> List<Project>
    destinationSolModules().forEach { module ->
        dependsOn(":modules:${module.name}:jar")
        from("$rootDir/modules/${module.name}/build/libs") {
            include("*.jar")
        }
    }
}

tasks.register<Copy>("copyLaunchers") {
    description = "Copy launchers into the distribution folder."

    from("$rootDir/launcher")
    include("*.sh", "*.exe")
    into("${distsDirectory.get().asFile}/app")
}

tasks.register<Copy>("libsDist") {
    description = "Copy libs directory into the distribution folder."

    dependsOn(tasks.jar)

    from(tasks.jar)
    from(configurations.runtimeClasspath)
    into("${distsDirectory.get().asFile}/app/libs")
}

tasks.register("distUnbundledJRE") {
    description = "Creates an application package without any bundled JRE."
    group = "Distribution"

    dependsOn("libsDist")
    dependsOn("moduleDist")
    dependsOn("copyLaunchers")
}

tasks.register<Zip>("distZipUnbundledJRE") {
    description = "Creates an application package and zip archive without any bundled JRE."
    group = "Distribution"

    dependsOn("distUnbundledJRE")
    from("${distsDirectory.get().asFile}/app")
    archiveFileName.set("DestinationSol.zip")
}

tasks.register("distBundleJREs") {
    description = "Creates an application package with a bundled JRE."
    group = "Distribution"

    dependsOn("distUnbundledJRE")
    dependsOn("downloadJreAll")
}

tasks.register<Zip>("distZipBundleJREs") {
    description = "Creates an application package and zip archive with a bundled JRE."
    group = "Distribution"

    dependsOn("distBundleJREs")
    from("${distsDirectory.get().asFile}/app")
    archiveFileName.set("DestinationSol.zip")
}

// TODO: LibGDX Generated config for Eclipse. Needs adjustment for assets not being in the Android facade
// eclipse is applied transitively (via destination-sol-common -> destination-sol-ide), not by
// this script's own plugins{} block, so the type-safe eclipse{} accessor isn't available here.
configure<EclipseModel> {
    project {
        name = "$appName-desktop"
        linkedResource(mapOf("name" to "assets", "type" to "2", "location" to "PARENT-1-PROJECT_LOC/android/assets"))
        file {
            whenMerged {
                val destinationSolRunConfig = XmlParser().parseText("""
                    <!--<?xml version="1.0" encoding="UTF-8" standalone="no"?>-->
                    <launchConfiguration type="org.eclipse.jdt.launching.localJavaApplication">
                        <listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_PATHS">
                            <listEntry value="/DestinationSol-desktop/src/main/java/org/destinationsol/desktop/SolDesktop.java"/>
                        </listAttribute>
                        <listAttribute key="org.eclipse.debug.core.MAPPED_RESOURCE_TYPES">
                            <listEntry value="1"/>
                        </listAttribute>
                        <booleanAttribute key="org.eclipse.jdt.launching.ATTR_USE_CLASSPATH_ONLY_JAR" value="false"/>
                        <booleanAttribute key="org.eclipse.jdt.launching.ATTR_USE_START_ON_FIRST_THREAD" value="true"/>
                        <stringAttribute key="org.eclipse.jdt.launching.MAIN_TYPE" value="org.destinationsol.desktop.SolDesktop"/>
                        <stringAttribute key="org.eclipse.jdt.launching.PROJECT_ATTR" value="DestinationSol-desktop"/>
                        <stringAttribute key="org.eclipse.jdt.launching.VM_ARGUMENTS" value="-splash:../engine/src/main/resources/assets/textures/mainMenu/mainMenuLogo.png -Dlog4j.configuration=log4j-debug.properties"/>
                        <stringAttribute key="org.eclipse.jdt.launching.WORKING_DIRECTORY" value="${'$'}{workspace_loc:DestinationSol-engine}"/>
                    </launchConfiguration>
                """)
                val writer = FileWriter(file("DestinationSol.launch"))
                val printer = XmlNodePrinter(PrintWriter(writer))
                printer.isPreserveWhitespace = true
                printer.print(destinationSolRunConfig)
            }
        }
    }
}

tasks.register("afterEclipseImport") {
    description = "Post processing after project generation"
    group = "IDE"

    doLast {
        val classpath = XmlParser().parse(file(".classpath"))
        groovy.util.Node(classpath, "classpathentry", mapOf("kind" to "src", "path" to "assets"))
        val writer = FileWriter(file(".classpath"))
        val printer = XmlNodePrinter(PrintWriter(writer))
        printer.isPreserveWhitespace = true
        printer.print(classpath)
    }
}

// Registering the task above doesn't make :desktop:eclipse run it. finalizedBy, not
// synchronizationTasks: the latter only fires on a Buildship-driven IDE sync, not on a plain
// `gradlew eclipse` - and .classpath (which this patches) doesn't exist until eclipse has run.
tasks.named("eclipse") { finalizedBy(tasks.named("afterEclipseImport")) }

tasks.withType<JavaExec>().configureEach {
    if (System.getProperty("DEBUG", "false") == "true") {
        jvmArgs("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9099", "-Dlog4j.configuration=log4j-debug.properties")
    }
}
