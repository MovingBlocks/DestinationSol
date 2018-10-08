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
package org.destinationsol.di;

import com.google.common.collect.Sets;
import dagger.Module;
import dagger.Provides;
import org.destinationsol.ModuleManager;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.DebugOptions;
import org.terasology.module.ModuleEnvironment;
import org.terasology.module.ModuleFactory;
import org.terasology.module.ModulePathScanner;
import org.terasology.module.ModuleRegistry;
import org.terasology.module.TableModuleRegistry;
import org.terasology.module.sandbox.StandardPermissionProviderFactory;

import javax.inject.Singleton;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Module
public class ModuleManagerModule {


    public ModuleManagerModule() {
    }


    @Provides
    @Singleton
    static  ModuleRegistry provideTableModuleRegistry() {
        return new TableModuleRegistry();
    }

    @Provides
    @Singleton
    static  ModuleManager provideModuleManager(ModuleEnvironment moduleEnvironment, ModuleRegistry moduleRegistry) {
        return new ModuleManager(moduleEnvironment, moduleRegistry);
    }

    @Provides
    @Singleton
    public ModuleEnvironment provideModuleEnviroment(ModuleRegistry registry) {

        try {
            URI engineClasspath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            org.terasology.module.Module engineModule = new ModuleFactory().createModule(Paths.get(engineClasspath));

            Path modulesRoot;
            if (DebugOptions.DEV_ROOT_PATH != null) {
                modulesRoot = Paths.get(".").resolve("modules");
            } else {
                modulesRoot = Paths.get(".").resolve("..").resolve("modules");
            }
            new ModulePathScanner().scan(registry, modulesRoot);

            Set<org.terasology.module.Module> requiredModules = Sets.newHashSet();
            requiredModules.add(engineModule);
            requiredModules.addAll(registry);
            return new ModuleEnvironment(requiredModules, new StandardPermissionProviderFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
