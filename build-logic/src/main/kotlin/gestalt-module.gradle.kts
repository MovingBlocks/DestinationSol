import com.google.gson.JsonParser
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    id("gestalt-repositories")
    id("terasology-publish-common")
}

val gestaltExtension = extensions.create<GestaltExtension>("gestalt")

gestaltExtension.modulesPackage.set("org.terasology.gestalt.modules")
gestaltExtension.moduleMetadataFileName.set("module.txt")

// Change the output dir of each module
sourceSets {
    main {
        java.destinationDirectory.set(File("$buildDir/classes"))
    }
    test {
        java.destinationDirectory.set(File("$buildDir/testClasses"))
    }
}

tasks.jar {
    from("module.json") {
        into("")
    }
    from("assets") {
        into("assets")
    }
    from("overrides") {
        into("overrides")
    }
    from("deltas") {
        into("deltas")
    }
}

val gestaltModule: Configuration by configurations.creating
configurations.named("api") {
    extendsFrom(gestaltModule)
}

afterEvaluate {
    val moduleMetadataFile = file(gestaltExtension.moduleMetadataFileName.get())
    if (!moduleMetadataFile.exists()) {
        println("${gestaltExtension.moduleMetadataFileName.get()} does not exist!")
        throw GradleException("Failed to find ${gestaltExtension.moduleMetadataFileName.get()} for module ${project.name}")
    }

    val moduleMetadata = moduleMetadataFile.reader().use { JsonParser.parseReader(it) }.asJsonObject
    extra["moduleMetadata"] = moduleMetadata
    extra["modulesRoot"] = if (project.hasProperty("modulesRoot")) property("modulesRoot") else "$rootProject/modules"

    moduleMetadata.get("version")?.asString?.let { version = it }
    moduleMetadata.get("description")?.asString?.let { description = it }

    publishing {
        publications {
            // Both this plugin and destination-sol-module configure the same, project-name-keyed
            // publication; maybeCreate lets either one run first without the other clobbering it.
            maybeCreate(project.name, MavenPublication::class.java).pom {
                name.set(project.name)
                description.set(project.description)
                val author = moduleMetadata.get("author")?.asString
                if (!author.isNullOrEmpty()) {
                    developers {
                        for (developerId in author.split(",")) {
                            developer {
                                id.set(developerId.trim())
                            }
                        }
                    }
                }
            }
        }
    }

    dependencies {
        moduleMetadata.getAsJsonArray("dependencies")?.forEach { element ->
            val dependency = element.asJsonObject
            val optional = dependency.get("optional")?.asBoolean ?: false
            if (!optional) {
                val depId = dependency.get("id").asString
                val minVersion = dependency.get("minVersion")?.asString
                val maxVersion = dependency.get("maxVersion")?.asString
                gestaltModule(group = gestaltExtension.modulesPackage.get(), name = depId) {
                    if (minVersion != null && maxVersion == null) {
                        version {
                            strictly("[$minVersion,)")
                        }
                    }
                    if (minVersion == null && maxVersion != null) {
                        version {
                            strictly("(,$maxVersion[")
                        }
                    }
                    if (minVersion != null && maxVersion != null) {
                        version {
                            strictly("[$minVersion, $maxVersion[")
                        }
                    }
                }
            }
        }
    }
}
