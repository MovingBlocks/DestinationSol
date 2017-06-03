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
import org.destinationsol.assets.Assets;
import org.terasology.assets.Asset;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class ModuleManager {
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
        scanner.scan(registry, Paths.get(".").resolve("modules"));

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

        loadEnvironment(requiredModules);
    }

    public void loadEnvironment(Set<Module> modules) {
        environment = new ModuleEnvironment(modules, permissionProviderFactory);
        Assets.initialize(environment);
    }

    public ModuleEnvironment getEnvironment() {
        return environment;
    }
}
