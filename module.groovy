// We use GrGit for interacting with Git. This gets a hold of it as a dependency like Gradle would
// TODO: Consider if we should do something to fix/suppress the SLF4J warning that gets logged on first usage?
@GrabResolver(name = 'jcenter', root = 'http://jcenter.bintray.com/')
@Grab(group='org.ajoberstar', module='grgit', version='1.9.3')
import org.ajoberstar.grgit.Grgit

import groovy.json.JsonSlurper

// Grab override properties from the gradle.properties file (shared with various Gradle commands)
Properties properties = new Properties()
new File("gradle.properties").withInputStream {
    properties.load(it)
}
println "Properties: " + properties

// Groovy Elvis operator woo! Defaults to "DestinationSol" if an override isn't set
githubHome = properties.alternativeGithubHome ?: "digitalripperynr"

//println "githubHome is: $githubHome"

// Module dependencies we don't want to retrieve as they live in the main DestSol repo
excludedDependencies = ["core"]

// For keeping a list of modules retrieved so far
modulesRetrieved = []

/**
 * Primary entry point for retrieving modules, kicks off recursively if needed.
 * @param modules the modules we want to retrieve
 */
def retrieve(String[] modules) {
    for (String module : modules) {
        println "Starting get for module $module."
        println "Modules retrieved so far: $modulesRetrieved"
        retrieveModule(module)
        //println "Modules retrieved after recent addition(s): modulesRetrieved"
    }
}

/**
 * Reads a given module info file to figure out which if any dependencies it has. Filters out any already retrieved.
 * @param targetModuleInfo the target file to check (a module.txt file or similar)
 * @return a String[] containing the next level of dependencies, if any
 */
String[] readModuleDependencies(File targetModuleInfo) {
    def qualifiedDependencies = []
    if (!targetModuleInfo.exists()) {
        println "The module info file did not appear to exist - can't calculate dependencies"
        return qualifiedDependencies
    }

    def slurper = new JsonSlurper()
    def moduleConfig = slurper.parseText(targetModuleInfo.text)
    for (dependency in moduleConfig.dependencies) {
        if (excludedDependencies.contains(dependency)) {
            println "Skipping listed dependency $dependency as it is in the exclude list (shipped with primary project)"
        } else {
            println "Accepting listed dependency $dependency"
            qualifiedDependencies << dependency
        }
    }
    return qualifiedDependencies
}

/**
 * Retrieves a single module via Git Clone. Considers whether it exists locally first or if it has already been retrieved this execution.
 * @param module the target module to retrieve
 */
def retrieveModule(String module) {
    File targetDir = new File("modules/$module")
    println "Request to retrieve module $module would store it at $targetDir - exists? " + targetDir.exists()

    if (targetDir.exists()) {
        println "That module already had an existing directory locally. If something is wrong with it please delete and try again"
        modulesRetrieved << module
    } else if (modulesRetrieved.contains(module)) {
        println "We already retrieved $module - skipping"
    } else {
        println "Retrieving module $module - if it doesn't appear to exist (typo for instance) you'll get an auth prompt (in case it is private)"
        //noinspection GroovyAssignabilityCheck - GrGit has its own .clone but a warning gets issued for Object.clone
        Grgit.clone dir: targetDir, uri: "https://github.com/$githubHome/${module}.git"
        modulesRetrieved << module

        File moduleManifest = new File(targetDir, 'module.json')
        if (!moduleManifest.exists()) {
            def moduleText = new File("templates/module.json").text
            moduleManifest << moduleText.replaceAll('MODULENAME', module)
            println "WARNING: Module $module did not have a module.json! One was created, please review and submit to GitHub"
        }
        def foundDependencies = readModuleDependencies(new File(targetDir, "module.json"))
           if (foundDependencies.length == 0) {
               println "Module $module did not appear to have any dependencies we need to worry about"
           } else {
               println "Module $module has the following module dependencies we care about: $foundDependencies"
               String[] uniqueDependencies = foundDependencies - modulesRetrieved
               println "After removing dupes already retrieved we have the remaining dependencies left: $uniqueDependencies"
               if (uniqueDependencies.length > 0) {
                   retrieve(uniqueDependencies)
            }
        }
    }
}

/**
 * Creates a new module with the given name and adds the necessary .gitignore,
 * build.gradle and module.txt files.
 * @param name the name of the module to be created
 */
def createModule(String name) {
    // Check if the module already exists. If not, create the module directory
    File targetDir = new File("modules/$name")
    if (targetDir.exists()) {
        println "Target directory already exists. Aborting."
        return
    }
    println "Creating target directory"
    targetDir.mkdir()

    createDirectoryStructure(targetDir)

    // Add gitignore
    println "Creating .gitignore"
    File gitignore = new File(targetDir, ".gitignore")
    def gitignoreText = new File("templates/.gitignore").text
    gitignore << gitignoreText

    // Add module.json
    println "Creating module.json"
    File moduleManifest = new File(targetDir, "module.json")
    def moduleText = new File("templates/module.json").text
    moduleManifest << moduleText.replaceAll('MODULENAME', name)

    // Initialize git
    Grgit.init dir: targetDir, bare: false
}

/**
 * Creates the directory structure of a destsol module
 * @param root The root of the newly created module
 */
def createDirectoryStructure(File root) {

    // Set root to the assets folder for simplicity sake
    root = new File(root, "assets")
    root.mkdir()

    // Just create the base directories for them. They can organize them how they want after that
    String[] dirs = ["configs", "emitters", "items", "ships", "sounds", "textures"]

    for (String dir : dirs) {
        new File(root, dir).mkdir()
    }

}

/**
 * Update a given module.
 * @param name the name of the module to update
 */
def updateModule(String name) {
    println "Attempting to update module $name"
    File targetDir = new File("modules/$name")
    if (!targetDir.exists()) {
        println "Module \"$name\" not found"
        return
    }

    def moduleGit = Grgit.open(dir: targetDir)
    def clean = moduleGit.status().clean
    println "Is \"$name\" clean? $clean"
    if (!clean) {
        println "Module has uncommitted changes. Aborting."
        return
    }

    println "Updating module $name"
    moduleGit.pull remote: "origin"
}

/**
 * Accepts input from the user, showing a descriptive prompt.
 * @param prompt the prompt to show the user
 */
def getUserString (String prompt) {
    println ('\n*** ' + prompt + '\n')

    def reader = new BufferedReader(new InputStreamReader(System.in)) // Note: Do not close reader, it will close System.in (Big no-no)

    return reader.readLine()
}

/**
 * Simply prints usage information.
 */
def printUsage() {
    println ""
    println "Utility script for interacting with modules. Available sub commands:"
    println "- 'get' - retrieves one or more modules in source form (separate with spaces)"
    println "- 'create' - creates a new module"
    println "- 'update' - updates a module (git pulls latest from current origin, if workspace is clean"
    println "- 'update-all' - updates all local modules"
    println ""
    println "Example: 'groovyw module create MySpaceShips' - would create that module"
    println "*NOTE*: Module names are case sensitive"
    println ""
    println "If you omit further arguments beyond the sub command you'll be prompted for details"
    println "After changing modules available in your workspace rerun 'gradlew idea' and/or refresh your IDE"
    println ""
    println "For advanced usage see project documentation. For instance you can provide an alternative GitHub home"
    println "A gradle.properties file (one exists under '/templates' in an engine workspace) can provide such overrides"
    println ""
}

// Main bit of logic handling the entry points to this script - defers actual work to dedicated methods
// println "Args: $args"
if (args.length == 0) {
    printUsage()
} else {
    switch (args[0]) {
        case 'usage':
            printUsage()
            break
        //noinspection GroovyFallthrough
        case "get":
            println "Preparing to get one or more modules"
            if (args.length == 1) {
                // User hasn't supplied any module names, so ask
                def moduleString = getUserString('Enter Module Name(s - separate multiple with spaces, CapiTaliZation MatterS): ')
                println "User wants: $moduleString"
                // Split it on whitespace
                String[] moduleList = moduleString.split("\\s+")
                println "Now in an array: $moduleList"
                retrieve moduleList
            } else {
                // User has supplied one or more module names, so pass them forward (skipping the "get" arg)
                def adjustedArgs = args.drop(1)
                println "Adjusted args: $adjustedArgs"
                retrieve adjustedArgs
            }
            println "All done retrieving requested modules: $modulesRetrieved"
            break
        case "create":
            println "We're doing a create"
            String name = ""

            // Get new module's name
            if (args.length > 2) {
              println "Received more than one argument. Aborting."
              break
            } else if (args.length == 2) {
              name = args[1]
            } else {
              name = getUserString("Enter module name: ")
            }
            println "User wants to create a module named: $name"

            createModule(name)

            println "Created module named $name"
            break
        case "update":
            println "We're updating modules"
            String[] moduleList = []
            if (args.length == 1) {
                // User hasn't supplied any module names, so ask
                def moduleString = getUserString('Enter Module Name(s - separate multiple with spaces, CapiTaliZation MatterS): ')
                // Split it on whitespace
                moduleList = moduleString.split("\\s+")
            } else {
                // User has supplied one or more module names, so pass them forward (skipping the "get" arg)
                moduleList = args.drop(1)
            }
            println "List of modules to update: $moduleList"
            for (String module: moduleList) {
                updateModule(module)
            }
            break
        case "update-all":
            println "We're updating all modules"
            println "List of modules:"
            new File("modules").eachDir() { dir ->
                String moduleName = dir.getPath().substring(8)
                updateModule(moduleName)
            }
            break
        default:
            println "UNRECOGNIZED COMMAND - please try again or use 'groovyw module usage' for help"
    }
}
