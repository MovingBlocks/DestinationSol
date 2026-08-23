import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlNodePrinter
import groovy.xml.XmlParser
import org.gradle.api.XmlProvider

plugins {
    eclipse
    idea
    id("org.jetbrains.gradle.plugin.idea-ext")
}

@Suppress("UNCHECKED_CAST")
fun Node.child(name: String): List<Node> = (get(name) as? NodeList)?.filterIsInstance<Node>() ?: emptyList()

fun Node.attr(name: String): Any? = attribute(name)

val ideaPatchAnnotationProcessors = Action<XmlProvider> {
    val profile = asNode().child("component")
        .first { it.attr("name") == "CompilerConfiguration" }
        .child("annotationProcessing").first()
        .child("profile")
    for (profileNode in profile) {
        val moduleName = profileNode.child("module").first().attr("name") as String

        val sourceOutputDir: String
        val sourceTestOutputDir: String

        if (moduleName.startsWith("DestinationSol.modules.")) {
            // Modules output to a unified build directory.
            sourceOutputDir = "../generated/sources/annotationProcessor/java/main"
            sourceTestOutputDir = "../generated/sources/annotationProcessor/java/main"
        } else {
            // Normal libraries use separated build directories instead.
            sourceOutputDir = "../../../generated/sources/annotationProcessor/java/main"
            sourceTestOutputDir = "../../../generated/sources/annotationProcessor/java/test"
        }

        if (profileNode.child("sourceOutputDir").isEmpty()) {
            profileNode.appendNode("sourceOutputDir", mapOf("name" to sourceOutputDir))
        } else {
            profileNode.child("sourceOutputDir").first().attributes()["name"] = sourceOutputDir
        }
        if (profileNode.child("sourceTestOutputDir").isEmpty()) {
            profileNode.appendNode("sourceTestOutputDir", mapOf("name" to sourceTestOutputDir))
        } else {
            profileNode.child("sourceTestOutputDir").first().attributes()["name"] = sourceTestOutputDir
        }
        if (profileNode.child("outputRelativeToContentRoot").isEmpty()) {
            profileNode.appendNode("outputRelativeToContentRoot", mapOf("value" to false))
        } else {
            profileNode.child("outputRelativeToContentRoot").first().attributes()["value"] = false
        }
    }
}

val ideaPatchEntryPoints = Action<XmlProvider> {
    val component = asNode().child("component")
        .firstOrNull { it.attr("name") == "EntryPointsManager" } ?: return@Action

    component.child("list").forEach { component.remove(it) }
    val entryPointsList = component.appendNode("list", mapOf("size" to 5))
    entryPointsList.appendNode("item", mapOf("index" to 0, "class" to "java.lang.String", "itemvalue" to "org.destinationsol.game.attributes.RegisterUpdateSystem"))
    entryPointsList.appendNode("item", mapOf("index" to 1, "class" to "java.lang.String", "itemvalue" to "org.destinationsol.game.console.annotations.Command"))
    entryPointsList.appendNode("item", mapOf("index" to 2, "class" to "java.lang.String", "itemvalue" to "org.terasology.gestalt.assets.module.annotations.RegisterAssetFileFormat"))
    entryPointsList.appendNode("item", mapOf("index" to 3, "class" to "java.lang.String", "itemvalue" to "org.terasology.gestalt.assets.module.annotations.RegisterAssetType"))
    entryPointsList.appendNode("item", mapOf("index" to 4, "class" to "java.lang.String", "itemvalue" to "org.terasology.context.annotation.Service"))

    component.child("writeAnnotations").forEach { component.remove(it) }
    val writeAnnotations = component.appendNode("writeAnnotations", mapOf<String, Any>())
    writeAnnotations.appendNode("writeAnnotation", mapOf("name" to "javax.inject.Inject"))
    writeAnnotations.appendNode("writeAnnotation", mapOf("name" to "org.destinationsol.common.In"))
}

val ideaPatchCheckstyle = Action<XmlProvider> {
    val checkstyleConfigs = asNode().child("component")
        .first { it.attr("name") == "CheckStyle-IDEA" }
        .child("option").first { it.attr("name") == "locations" }
        .child("list").first()
    var terasologyConfig = checkstyleConfigs.child("ConfigurationLocation")
        .firstOrNull { it.attr("id") == "terasology-style" }
    if (terasologyConfig != null) {
        println(terasologyConfig)
        checkstyleConfigs.remove(terasologyConfig)
    }
    terasologyConfig = checkstyleConfigs.appendNode(
        "ConfigurationLocation",
        mapOf("id" to "terasology-style", "type" to "LOCAL_FILE", "scope" to "All", "description" to "Terasology Style")
    )
    terasologyConfig.setValue("\$PROJECT_DIR\$/config/metrics/checkstyle/checkstyle.xml")
    val terasologyConfigProperties = terasologyConfig.appendNode("option", mapOf("name" to "properties")).appendNode("map", mapOf<String, Any>())
    terasologyConfigProperties.appendNode("entry", mapOf("key" to "sameDir", "value" to "\$PROJECT_DIR\$/config/metrics/checkstyle"))
}

extra["ideaPatchAnnotationProcessors"] = ideaPatchAnnotationProcessors
extra["ideaPatchEntryPoints"] = ideaPatchEntryPoints
extra["ideaPatchCheckstyle"] = ideaPatchCheckstyle
