plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:5.1.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:2.0.1")
    implementation("de.undercouch:gradle-download-task:5.7.0")
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.4.1")
}
