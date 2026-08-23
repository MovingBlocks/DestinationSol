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

// Dependencies needed for what our Gradle scripts themselves use. It cannot be included via an external Gradle file :-(
buildscript {
    repositories {
        mavenCentral()

        google()
        maven {
            url = uri("https://artifactory.terasology.io/artifactory/virtual-repo-live")
        }
        // Needed for Jsemver, which is a gestalt dependency
        maven { url = uri("https://heisluft.de/maven/") }
    }

    dependencies {
        classpath("dom4j:dom4j:1.6.1")
    }
}

plugins {
    `java-library`
    id("destination-sol-common")
    id("terasology-publish-common")
}

group = "org.destinationsol.engine"

val engineVersion = extra["engineVersion"] as String
val gdxVersion = extra["gdxVersion"] as String
val gdxControllersVersion = extra["gdxControllersVersion"] as String
val gestaltVersion = extra["gestaltVersion"] as String
val nuiVersion = extra["nuiVersion"] as String
val appName = extra["appName"] as String

version = engineVersion

dependencies {
    api(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
    api(group = "com.google.code.gson", name = "gson", version = "2.6.2")
    api(group = "com.google.guava", name = "guava", version = "30.1-jre")

    api("com.badlogicgames.gdx:gdx:$gdxVersion")
    api("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
    api("com.badlogicgames.gdx-controllers:gdx-controllers-core:$gdxControllersVersion")

    api("org.terasology.gestalt:gestalt-asset-core:$gestaltVersion")
    api("org.terasology.gestalt:gestalt-entity-system:$gestaltVersion")
    api("org.terasology.gestalt:gestalt-module:$gestaltVersion")
    api("org.terasology.gestalt:gestalt-util:$gestaltVersion")

    api("org.terasology.gestalt:gestalt-di:$gestaltVersion")
    api("org.terasology.gestalt:gestalt-inject:$gestaltVersion")
    annotationProcessor("org.terasology.gestalt:gestalt-inject-java:$gestaltVersion")

    implementation("net.jcip:jcip-annotations:1.0")

    api("org.terasology.nui:nui:$nuiVersion")
    api("org.terasology.nui:nui-libgdx:$nuiVersion")
    api("org.terasology.nui:nui-gestalt:$nuiVersion")
    api("org.terasology.nui:nui-reflect:$nuiVersion")

    implementation(group = "com.google.protobuf", name = "protobuf-java", version = "3.4.0")

    implementation("com.github.zafarkhaja:java-semver:0.10.0") // gestalt lost this...
    api("com.github.everit-org.json-schema:org.everit.json.schema:1.11.1")
    implementation("com.github.marschall:zipfilesystem-standalone:1.0.1")
    implementation("dom4j:dom4j:1.6.1")

    //TODO inserted by someone who has no idea how the gradle thing works. Inspect whether necessary (for tests). Copied rom desktop build.gradle. May break things with android (maybe)
    testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
    testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    testImplementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
    testImplementation("com.badlogicgames.gdx-controllers:gdx-controllers-desktop:$gdxControllersVersion")

    // Test lib dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.1.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.2")

    testImplementation("org.jboss.shrinkwrap:shrinkwrap-depchain-java7:1.1.3")
    testImplementation("org.assertj:assertj-core:3.27.7")
}

// Adds Resources as parameter for AnnotationProcessor (gather ResourceIndex,
// also add resource as input for compilejava, for re-gathering ResourceIndex, when resource was changed.
tasks.compileJava {
    inputs.files(sourceSets.main.get().resources.srcDirs)
    options.compilerArgs = listOf("-Aresource=${sourceSets.main.get().resources.srcDirs.joinToString(File.pathSeparator)}")
}
tasks.compileTestJava {
    inputs.files(sourceSets.test.get().resources.srcDirs)
    options.compilerArgs = listOf("-Aresource=${sourceSets.test.get().resources.srcDirs.joinToString(File.pathSeparator)}")
}

tasks.jar {
    archiveFileName.set("sol.jar")

    doFirst {
        copy {
            from("src/SolAppListener.gwt.xml")
            into("build/classes/main")
        }
    }
}

// eclipse is applied transitively (via destination-sol-common -> destination-sol-ide), not by
// this script's own plugins{} block, so the type-safe eclipse{} accessor isn't available here.
configure<org.gradle.plugins.ide.eclipse.model.EclipseModel> {
    project {
        name = "$appName-engine"
    }
}

// Extra details provided for unit tests
tasks.test {
    // ignoreFailures: Specifies whether the build should break when the verifications performed by this task fail.
    ignoreFailures = true
    useJUnitPlatform()
    // showStandardStreams: makes the standard streams (err and out) visible at console when running tests
    testLogging.showStandardStreams = true
    workingDir = rootProject.projectDir
}
