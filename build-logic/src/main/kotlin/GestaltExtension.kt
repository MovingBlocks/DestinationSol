import org.gradle.api.provider.Property

interface GestaltExtension {
    val modulesPackage: Property<String>
    val moduleMetadataFileName: Property<String>
}
