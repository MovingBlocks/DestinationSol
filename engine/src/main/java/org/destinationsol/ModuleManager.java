/*
 * Copyright 2016 MovingBlocks
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
import org.destinationsol.game.DebugOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.Asset;
import org.terasology.assets.ResourceUrn;
import org.terasology.module.ClasspathModule;
import org.terasology.module.Module;
import org.terasology.module.ModuleEnvironment;
import org.terasology.module.ModuleLoader;
import org.terasology.module.ModuleMetadata;
import org.terasology.module.ModuleMetadataJsonAdapter;
import org.terasology.module.ModulePathScanner;
import org.terasology.module.ModuleRegistry;
import org.terasology.module.TableModuleRegistry;
import org.terasology.module.sandbox.StandardPermissionProviderFactory;
import org.terasology.naming.Name;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Set;

public class ModuleManager {
    private static Logger logger = LoggerFactory.getLogger(ModuleManager.class);

    private StandardPermissionProviderFactory permissionProviderFactory = new StandardPermissionProviderFactory();
    private ModuleEnvironment environment;
    private ModuleRegistry registry;

    public ModuleManager() {
        ModuleMetadataJsonAdapter metadataReader = new ModuleMetadataJsonAdapter();

        Module engineModule;
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/module.info"))) {
            ModuleMetadata metadata = metadataReader.read(reader);
            engineModule = ClasspathModule.create(metadata, true, getClass(), Module.class, Asset.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read engine metadata", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert engine library location to path", e);
        }

        registry = new TableModuleRegistry();
        registry.add(engineModule);
        ModulePathScanner scanner = new ModulePathScanner(new ModuleLoader(metadataReader));

        //TODO: TEMPORARY HACK!

        if (DebugOptions.DEV_ROOT_PATH != null) {
            scanner.scan(registry, Paths.get(".").resolve("modules"));
        } else {
            scanner.scan(registry, Paths.get(".").resolve("..").resolve("modules"));
        }

        // Do we need this line? Copied from Teraosolgy.
        /*
        DependencyInfo engineDep = new DependencyInfo();
        engineDep.setId(engineModule.getId());
        engineDep.setMinVersion(engineModule.getVersion());
        engineDep.setMaxVersion(engineModule.getVersion().getNextPatchVersion());

        registry.stream().filter(mod -> mod != engineModule).forEach(mod -> mod.getMetadata().getDependencies().add(engineDep));
        */

        Set<Module> requiredModules = Sets.newHashSet();
        requiredModules.add(engineModule);
        requiredModules.add(registry.getLatestModuleVersion(new Name("core")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("federal")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("organic")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("vulcan")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("tribe")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("deepspace")));
        requiredModules.add(registry.getLatestModuleVersion(new Name("formic")));

        loadEnvironment(requiredModules);
    }

    public void loadEnvironment(Set<Module> modules) {
        environment = new ModuleEnvironment(modules, permissionProviderFactory);
        Assets.initialize(environment);
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
