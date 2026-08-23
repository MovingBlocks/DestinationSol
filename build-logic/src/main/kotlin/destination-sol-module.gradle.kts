import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.idea.model.IdeaModel

plugins {
    id("destination-sol-constants")
    id("gestalt-8-module")
    id("destination-sol-common")
}

group = "org.destinationsol.modules"

val engineVersion = extra["engineVersion"] as String

dependencies {
    "api"(rootProject.findProject("engine") ?: "org.destinationsol.engine:engine:$engineVersion")
}

configure<GestaltExtension> {
    modulesPackage.set("org.destinationsol.modules")
    moduleMetadataFileName.set("module.json")
}

publishing {
    publications {
        // maybeCreate: gestalt-module and terasology-publish-common also configure the
        // project-name-keyed publication, in whichever order their plugins get applied.
        maybeCreate(project.name, MavenPublication::class.java).pom {
            url.set("https://github.com/DestinationSol/${project.name}")
            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/DestinationSol/${project.name}/issues")
            }
            scm {
                connection.set("scm:git:https://github.com/DestinationSol/${project.name}.git")
                developerConnection.set("scm:git:ssh://github.com/DestinationSol/${project.name}.git")
                url.set("scm:git:https://github.com/DestinationSol/${project.name}.git")
            }
        }
    }
}

// Generate the module directory structure if missing
tasks.register("createSkeleton") {
    doLast {
        mkdir("assets")
        mkdir("assets/music")
        mkdir("assets/sounds")
        mkdir("assets/textures")
        mkdir("assets/configs")
        mkdir("assets/asteroids")
        mkdir("assets/schemas")
        mkdir("assets/prefabs")
        mkdir("assets/ui")
        mkdir("assets/skins")
        mkdir("overrides")
        mkdir("deltas")
        mkdir("src/main/java")
        mkdir("src/test/java")
    }
}

// idea/eclipse are applied transitively (via destination-sol-common -> destination-sol-ide),
// not by this script's own plugins{} block, so the type-safe idea{}/eclipse{} accessors
// aren't available here - look the extensions up explicitly instead.
configure<IdeaModel> {
    module {
        inheritOutputDirs = false
        outputDir = file("build/classes")
        testOutputDir = file("build/testClasses")
        isDownloadSources = true
    }
}

// For Eclipse just make sure the classpath is right
configure<EclipseModel> {
    classpath {
        defaultOutputDir = file("build/classes")
    }
}
