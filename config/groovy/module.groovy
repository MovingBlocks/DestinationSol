/*
 * Copyright 2020 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import groovy.json.JsonSlurper

class module {
    def excludedItems = ["engine", "core", "out", "build", "DestinationSol.github.io"]

    def getGithubDefaultHome(Properties properties) {
        return properties.alternativeGithubHome ?: "DestinationSol"
    }

    File targetDirectory = new File("modules")
    def itemType = "module"

    String[] findDependencies(File targetDir, boolean respectExcludedItems = true) {
        def foundDependencies = readModuleDependencies(new File(targetDir, "module.json"), respectExcludedItems)
        println "Looked for dependencies, found: " + foundDependencies
        return foundDependencies
    }

    /**
     * Reads a given module info file to figure out which if any dependencies it has. Filters out any already retrieved.
     * This method is only for modules.
     * @param targetModuleInfo the target file to check (a module.json file or similar)
     * @return a String[] containing the next level of dependencies, if any
     */
    String[] readModuleDependencies(File targetModuleInfo, boolean respectExcludedItems = true) {
        def qualifiedDependencies = []
        if (!targetModuleInfo.exists()) {
            println "The module info file did not appear to exist - can't calculate dependencies"
            return qualifiedDependencies
        }
        def slurper = new JsonSlurper()
        def moduleConfig = slurper.parseText(targetModuleInfo.text)
        for (dependency in moduleConfig.dependencies) {
            if (respectExcludedItems && excludedItems.contains(dependency.id)) {
                println "Skipping listed dependency $dependency.id as it is in the exclude list (shipped with primary project)"
            } else {
                println "Accepting listed dependency $dependency.id"
                qualifiedDependencies << dependency.id
            }
        }
        return qualifiedDependencies
    }

    def copyInTemplateFiles(File targetDir) {
        // Copy in the template build.gradle for modules
        println "In copyInTemplateFiles for module $targetDir.name - copying in a build.gradle then next checking for module.json"
        File targetBuildGradle = new File(targetDir, 'build.gradle')
        targetBuildGradle.delete()
        targetBuildGradle << new File('templates/build.gradle').text

        // Copy in the template module.json for modules (if one doesn't exist yet)
        File moduleManifest = new File(targetDir, 'module.json')
        if (!moduleManifest.exists()) {
            def moduleText = new File("templates/module.json").text

            moduleManifest << moduleText.replaceAll('MODULENAME', targetDir.name)
            println "WARNING: the module ${targetDir.name} did not have a module.json! One was created, please review and submit to GitHub"
        }

        // Always use the latest build.gradle
        File buildGradle = new File(targetDir, 'build.gradle')
        buildGradle.delete();
        buildGradle << new File('templates/build.gradle').text
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
     * Filters the given items based on this item type's preferences
     * @param possibleItems A map of repos (possible items) and their descriptions (potential filter data)
     * @return A list containing only the items this type cares about
     */
    List filterItemsFromApi(Map possibleItems) {
        List itemList = []

        // Modules just consider the item name and excludes those in a specific list
        itemList = possibleItems.findAll {
            !excludedItems.contains(it.key)
        }.collect { it.key }

        return itemList
    }

    def refreshGradle(File targetDir) {
        // Copy in the template build.gradle for modules
        if (!new File(targetDir, "module.json").exists()) {
            println "$targetDir has no module.json, it must not want a fresh build.gradle"
            return
        }
        println "In refreshGradle for module $targetDir - copying in a fresh build.gradle"
        File targetBuildGradle = new File(targetDir, 'build.gradle')
        targetBuildGradle.delete()
        targetBuildGradle << new File('templates/build.gradle').text
    }
}
