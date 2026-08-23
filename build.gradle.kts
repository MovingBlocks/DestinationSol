// Git plugin details at https://github.com/ajoberstar/gradle-git
import org.ajoberstar.grgit.Grgit
import org.gradle.api.XmlProvider
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.copyright
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    id("destination-sol-ide")
    id("destination-sol-repositories")
    id("terasology-metrics")
    id("org.ajoberstar.grgit") version "5.0.0" apply false
}

repositories {
    // Good ole Maven central
    mavenCentral()

    // Repos for LibGDX
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }

    // Terasology Artifactory for any shared libs
    maven {
        url = uri("https://artifactory.terasology.io/artifactory/virtual-repo-live")
    }

    maven { url = uri("https://maven.google.com") }
}

// Helper that returns a list of all local Destination Sol module projects
fun destinationSolModules() = subprojects.filter { it.parent?.name == "modules" }

extra["destinationSolModules"] = ::destinationSolModules

destinationSolModules().forEach { destSolModule ->
    destSolModule.configurations.configureEach {
        resolutionStrategy.dependencySubstitution.all {
            val requestedSelector = requested
            if (requestedSelector is ModuleComponentSelector && requestedSelector.group == "org.destinationsol.modules") {
                destinationSolModules().forEach { otherModule ->
                    if (otherModule.name == requestedSelector.module) {
                        useTarget(otherModule)
                    }
                }
            }
        }
    }
}

tasks.named("eclipse") {
    doLast {
        delete(".project")
    }
    dependsOn("extractMetricsConfig")
}

@Suppress("UNCHECKED_CAST")
val ideaPatchAnnotationProcessors = extra["ideaPatchAnnotationProcessors"] as Action<XmlProvider>
@Suppress("UNCHECKED_CAST")
val ideaPatchEntryPoints = extra["ideaPatchEntryPoints"] as Action<XmlProvider>
@Suppress("UNCHECKED_CAST")
val ideaPatchCheckstyle = extra["ideaPatchCheckstyle"] as Action<XmlProvider>

idea {
    project {
        // Set JDK
        jdkName = "1.8"
        wildcards.remove("!?*.groovy")

        settings {
            compiler {
                enableAutomake = true
            }

            runConfigurations {
                create<Application>("Desktop") {
                    mainClass = "org.destinationsol.desktop.SolDesktop"
                    moduleName = "DestinationSol.desktop.main"
                    workingDirectory = rootDir.toString()
                    jvmArgs = "-splash:engine/src/main/resources/assets/textures/mainMenu/mainMenuLogo.png -Xms256m -Xmx1024m -Dlog4j.configuration=log4j-debug.properties"
                    programParameters = "-noSplash -noCrashReport"
                }
            }

            copyright {
                useDefault = "DestinationSolCopyright"
                profiles {
                    create("DestinationSolCopyright") {
                        notice = "Copyright 2023 The Terasology Foundation\n\nLicensed under the Apache License, Version 2.0 (the \"License\");\nyou may not use this file except in compliance with the License.\nYou may obtain a copy of the License at\n\n     https://www.apache.org/licenses/LICENSE-2.0\n\nUnless required by applicable law or agreed to in writing, software\ndistributed under the License is distributed on an \"AS IS\" BASIS,\nWITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\nSee the License for the specific language governing permissions and\nlimitations under the License."
                        keyword = "Copyright"
                        allowReplaceRegexp = ""
                    }
                }
            }

            taskTriggers {
                afterSync(tasks.named("extractMetricsConfig"))
            }

            generateImlFiles = true
            withIDEAFileXml("compiler.xml", ideaPatchAnnotationProcessors)
            withIDEAFileXml("misc.xml", ideaPatchEntryPoints)
            withIDEAFileXml("checkstyle-idea.xml", ideaPatchCheckstyle)
        }

        // NOTE: the previous ipr.withXml{}/workspace.iws.withXml{} blocks here called
        // ideaActivateCheckstyle/Copyright/Annotations/Git/Gradle, ideaMakeAutomatically and
        // ideaRunConfig - none of which have existed since config/gradle/ide.gradle was deleted
        // in 68193863 (2022). Groovy only ever failed on that at runtime, when `gradle idea`/
        // `ipr`/`iws` actually ran, which nothing does - so it's been silently dead ever since.
        // Dropped rather than carried forward as newly-hard-failing Kotlin.
    }
}

tasks.named("cleanIdea") {
    doLast {
        rootDir.resolve("DestinationSol.iws").delete()
        rootDir.resolve("config/metrics").deleteRecursively()
        println("Cleaned root - don't forget to re-extract code metrics config! 'gradlew extractMetricsConfig' will do so, or 'gradlew idea' (or eclipse)")
    }
}

tasks.register("fetchAndroid") {
    description = "Git clones the Android facade source from GitHub"

    // Repo name is the dynamic part of the task name
    val repo = "DestSolAndroid"

    // Default GitHub account to use. Supply with -PgithubAccount="TargetAccountName" or via gradle.properties
    val githubHome = findProperty("githubAccount") as String? ?: "MovingBlocks"

    val destination = file("android")

    // Don't clone this repo if we already have a directory by that name (also determines Gradle UP-TO-DATE)
    enabled = !destination.exists()
    //println("fetchAndroid requested for $repo from Github under $githubHome - exists already? " + !enabled)

    doLast {
        Grgit.clone(mapOf(
            // Do the actual clone if we don't have the directory already
            "uri" to "https://github.com/$githubHome/$repo.git",
            //println("Fetching $repo from $uri")
            "dir" to destination,
            "bare" to false
        ))
    }
}

tasks.register("fetchSteam") {
    description = "Git clones the Steam facade source from GitHub"

    // Repo name is the dynamic part of the task name
    val repo = "DestSolSteam"

    // Default GitHub account to use. Supply with -PgithubAccount="TargetAccountName" or via gradle.properties
    val githubHome = findProperty("githubAccount") as String? ?: "MovingBlocks"

    val destination = file("steam")

    // Don't clone this repo if we already have a directory by that name (also determines Gradle UP-TO-DATE)
    enabled = !destination.exists()

    doLast {
        Grgit.clone(mapOf(
            // Do the actual clone if we don't have the directory already
            "uri" to "https://github.com/$githubHome/$repo.git",
            //println("Fetching $repo from $uri")
            "dir" to destination,
            "bare" to false
        ))
    }
}
