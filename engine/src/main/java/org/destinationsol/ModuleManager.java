/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol;

import com.google.common.collect.Sets;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.game.Console;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.console.ConsoleInputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.module.*;
import org.terasology.module.sandbox.*;
import org.terasology.naming.Name;
import org.terasology.naming.Version;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Policy;
import java.util.Set;

public class ModuleManager {
    private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);

    private ModuleEnvironment environment;
    private ModuleRegistry registry;
    private Module engineModule;

    public ModuleManager() {
        try {
            URI engineClasspath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            ModuleMetadata engineMetadata = new ModuleMetadata();
            engineMetadata.setId(new Name("engine"));
            engineMetadata.setVersion(Version.DEFAULT);
            engineModule = new ModuleFactory().createClasspathModule(engineMetadata, true, getClass());

            registry = new TableModuleRegistry();
            Path modulesRoot;
            if (DebugOptions.DEV_ROOT_PATH != null) {
                modulesRoot = Paths.get(".").resolve("modules");
            } else {
                modulesRoot = Paths.get(".").resolve("..").resolve("modules");
            }
            new ModulePathScanner().scan(registry, modulesRoot);

            Set<Module> requiredModules = Sets.newHashSet();
            requiredModules.add(engineModule);
            requiredModules.addAll(registry);

            loadEnvironment(requiredModules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEnvironment(Set<Module> modules) {
        StandardPermissionProviderFactory permissionFactory = new StandardPermissionProviderFactory();
        permissionFactory.getBasePermissionSet().addAPIPackage(engineModule.toString());
        permissionFactory.getBasePermissionSet().addAPIPackage("java.lang");
        APIScanner scanner = new APIScanner(permissionFactory);
        scanner.scan(registry);
        scanner.scan(engineModule);
        Policy.setPolicy(new ModuleSecurityPolicy());
        System.setSecurityManager(new ModuleSecurityManager());
        environment = new ModuleEnvironment(modules, permissionFactory);
        Assets.initialize(environment);

        for (Class commandHandler : environment.getSubtypesOf(ConsoleInputHandler.class)) {
            String commandName = commandHandler.getSimpleName().replace("Command", "");
            try {
                Console.getInstance().getDefaultInputHandler().registerCommand(commandName, (ConsoleInputHandler) commandHandler.newInstance());
            } catch (Exception e) {
                logger.error("Error creating instance of class " + commandHandler.getTypeName());
            }
        }
    }

    public ModuleEnvironment getEnvironment() {
        return environment;
    }

    public void printAvailableModules() {
        AssetHelper assetHelper = Assets.getAssetHelper();
        Set<ResourceUrn> jsonList = assetHelper.list(Json.class);
        Set<ResourceUrn> emitterList = assetHelper.list(Emitter.class);
        Set<ResourceUrn> soundList = assetHelper.list(OggSound.class);
        Set<ResourceUrn> musicList = assetHelper.list(OggMusic.class);
        Set<ResourceUrn> textureList = assetHelper.list(DSTexture.class);

        for (Module module : registry) {
            String moduleName = module.getId().toString();

            logger.info("Module Discovered: {}", module.toString());

            int armors = 0;
            int abilityCharges = 0;
            int clips = 0;
            int engines = 0;
            int shields = 0;
            int jsonOthers = 0;
            int emitters = 0;
            int sounds = 0;
            int music = 0;
            int textures = 0;

            for (ResourceUrn assetUrn : jsonList) {
                String assetName = assetUrn.toString();

                if (assetName.startsWith(moduleName + ":")) {
                    if (assetName.endsWith("Armor")) {
                        armors++;
                    } else if (assetName.endsWith("AbilityCharge")) {
                        abilityCharges++;
                    } else if (assetName.endsWith("Clip")) {
                        clips++;
                    } else if (assetName.endsWith("Engine")) {
                        engines++;
                    } else if (assetName.endsWith("Shield")) {
                        shields++;
                    } else {
                        jsonOthers++;
                    }
                }
            }

            for (ResourceUrn assetUrn : emitterList) {
                String assetName = assetUrn.toString();

                if (assetName.startsWith(moduleName + ":")) {
                    emitters++;
                }
            }

            for (ResourceUrn assetUrn : soundList) {
                String assetName = assetUrn.toString();

                if (assetName.startsWith(moduleName + ":")) {
                    sounds++;
                }
            }

            for (ResourceUrn assetUrn : musicList) {
                String assetName = assetUrn.toString();

                if (assetName.startsWith(moduleName + ":")) {
                    music++;
                }
            }

            for (ResourceUrn assetUrn : textureList) {
                String assetName = assetUrn.toString();

                if (assetName.startsWith(moduleName + ":")) {
                    textures++;
                }
            }

            logger.info("\t-Items:");
            logger.info("\t\t-Armors: {}", armors);
            logger.info("\t\t-AbilityCharges: {}", abilityCharges);
            logger.info("\t\t-Clips: {}", clips);
            logger.info("\t\t-Engines: {}", engines);
            logger.info("\t\t-Shields: {}", shields);
            logger.info("\t\t-Others: {}", jsonOthers);

            logger.info("\t-Emitters: {}", emitters);

            logger.info("\t-Sounds: {}", sounds);

            logger.info("\t-Music: {}", music);

            logger.info("\t-Textures: {}", textures);

            logger.info("");
        }
    }
}
