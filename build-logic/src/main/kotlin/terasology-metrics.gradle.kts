import com.github.spotbugs.snom.SpotBugsTask

plugins {
    java
    id("project-report")
    checkstyle
    pmd
    id("com.github.spotbugs")
    jacoco
    id("ru.vyarus.animalsniffer")
}

val codeMetrics: Configuration by configurations.creating

dependencies {
    // Config for our code analytics lives in a centralized repo: https://github.com/MovingBlocks/TeraConfig
    codeMetrics(group = "org.terasology.config", name = "codemetrics", version = "2.2.0", ext = "zip")

    "pmd"("net.sourceforge.pmd:pmd-ant:7.26.0")
    "pmd"("net.sourceforge.pmd:pmd-core:7.26.0")
    "pmd"("net.sourceforge.pmd:pmd-java:7.26.0")

    "signature"("com.toasttab.android:gummy-bears-api-24:0.15.0:coreLib2@signature")
}

animalsniffer {
    // java.nio.* APIs can be desugared by D8. java.io.File.toPath() also needs to be excluded.
    ignore = setOf("java.nio.file.*", "java.io.File")
}

jacoco {
    toolVersion = "0.8.15"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Despite doc saying this should be automatic we need to explicitly add it anyway :-(
    reports {
        // We only use the .exec report for display in Jenkins and such. More could be enabled if desired.
        xml.required = false
        csv.required = false
        html.required = false
    }
}

checkstyle {
    isIgnoreFailures = true
    configFile = File(rootDir, "config/metrics/checkstyle/checkstyle.xml")
    toolVersion = "10.2"
    configDirectory.set(configFile.parentFile)
    configProperties["samedir"] = configFile.parentFile
}

pmd {
    isIgnoreFailures = true
    ruleSetFiles = files("$rootDir/config/metrics/pmd/pmd.xml")
    // By default, gradle uses both ruleset file AND the rulesets. Override the ruleSets to use only those from the file
    ruleSets = listOf()
}

spotbugs {
    toolVersion.set("4.8.1")
    ignoreFailures.set(true)
    excludeFilter.set(File(rootDir, "config/metrics/findbugs/findbugs-exclude.xml"))
}
tasks.named<SpotBugsTask>("spotbugsMain") {
    reports.create("xml") {
        enabled = true
        outputLocation.set(file("$buildDir/reports/spotbugs/main/spotbugs.xml"))
    }
}

val extractMetricsConfig = rootProject.tasks.findByName("extractMetricsConfig")
    ?: rootProject.tasks.register("extractMetricsConfig", Copy::class) {
        description = "Extracts our configuration files from the zip we fetched as a dependency"
        from({ codeMetrics.map { zipTree(it) } })
        into("$rootDir/config/metrics")
    }.get()

tasks.named("spotbugsMain") { dependsOn(extractMetricsConfig) }
tasks.named("pmdMain") { dependsOn(extractMetricsConfig) }

tasks.withType<Checkstyle>().configureEach {
    group = "Reporting"
    dependsOn(extractMetricsConfig)
}

tasks.withType<Pmd>().configureEach {
    dependsOn(extractMetricsConfig)
    group = "Reporting"
}

tasks.withType<SpotBugsTask>().configureEach {
    dependsOn(extractMetricsConfig)
    group = "Reporting"
}

tasks.check {
    dependsOn(extractMetricsConfig)
}
