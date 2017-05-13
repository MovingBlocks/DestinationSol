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
import org.terasology.assets.Asset;
import org.terasology.module.ClasspathModule;
import org.terasology.module.Module;
import org.terasology.module.ModuleEnvironment;
import org.terasology.module.ModuleMetadata;
import org.terasology.module.ModuleMetadataJsonAdapter;
import org.terasology.module.sandbox.StandardPermissionProviderFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.Set;

public class ModuleManager {
    private StandardPermissionProviderFactory permissionProviderFactory = new StandardPermissionProviderFactory();
    private ModuleEnvironment environment;

    public ModuleManager() {
        ModuleMetadataJsonAdapter metadataReader = new ModuleMetadataJsonAdapter();

        Module coreModule;
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/module.info"))) {
            ModuleMetadata metadata = metadataReader.read(reader);
            coreModule = ClasspathModule.create(metadata, true, getClass(), Module.class, Asset.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read engine metadata", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert engine library location to path", e);
        }

        loadEnvironment(Sets.newHashSet(coreModule));
    }

    public void loadEnvironment(Set<Module> modules) {
        environment = new ModuleEnvironment(modules, permissionProviderFactory);
    }

    public ModuleEnvironment getEnvironment() {
        return environment;
    }
}
