import org.gradle.api.publish.maven.MavenPublication
import org.gradle.authentication.http.BasicAuthentication

plugins {
    `maven-publish`
}

publishing {
    publications {
        // maybeCreate: gestalt-module and destination-sol-module also configure the
        // project-name-keyed publication, in whichever order their plugins get applied.
        maybeCreate(project.name, MavenPublication::class.java).apply {
            from(components["java"])
            pom {
                name.set(project.name)
            }
        }
    }

    repositories {
        maven {
            name = "TerasologyOrg"
            if (rootProject.hasProperty("publishRepo")) {
                // This first option is good for local testing, you can set a full explicit target repo in gradle.properties
                val publishRepo = rootProject.property("publishRepo") as String
                url = uri("https://artifactory.terasology.io/artifactory/$publishRepo")

                logger.info("Changing PUBLISH repoKey set via Gradle property to {}", publishRepo)
            } else {
                // Support override from the environment to use a different target publish org
                var deducedPublishRepo = System.getenv()["PUBLISH_ORG"]
                if (deducedPublishRepo.isNullOrEmpty()) {
                    // If not then default
                    deducedPublishRepo = "libs"
                }

                // Base final publish repo on whether we're building a snapshot or a release
                deducedPublishRepo += if (project.version.toString().endsWith("SNAPSHOT")) {
                    "-snapshot-local"
                } else {
                    "-release-local"
                }

                logger.info("The final deduced publish repo is {}", deducedPublishRepo)
                url = uri("https://artifactory.terasology.io/artifactory/$deducedPublishRepo")
            }

            if (rootProject.hasProperty("mavenUser") && rootProject.hasProperty("mavenPass")) {
                credentials {
                    username = rootProject.property("mavenUser") as String
                    password = rootProject.property("mavenPass") as String
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}
