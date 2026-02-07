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
package org.destinationsol.desktop;

import org.destinationsol.modules.FacadeModuleConfig;
import org.destinationsol.SolApplication;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModulePathScanner;
import org.terasology.gestalt.module.sandbox.JavaModuleClassLoader;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is the desktop (PC) entry point for the whole DestinationSol application. It handles the creation and
 * launching of LwjglApplication from {@link SolApplication}.
 */
public final class SolDesktop {
    /**
     * This class is basically only a holder for the Java's {@code main(String[])} method, thus needs not to be
     * instantiated.
     */
    private SolDesktop() {
    }

    public static void main(String[] argv) {
        DesktopLauncher.launchGame(argv, DesktopModuleConfig.class, ModulePathScanner.class);
    }

    public static class DesktopModuleConfig implements FacadeModuleConfig {
        @Inject
        public DesktopModuleConfig() {
        }

        @Override
        public Collection<File> getModulePaths() {
            return Collections.singletonList(Paths.get(".").resolve("modules").toFile());
        }

        @Override
        public boolean useSecurityManager() {
            return true;
        }

        @Override
        public ModuleEnvironment.ClassLoaderSupplier getClassLoaderSupplier() {
            return JavaModuleClassLoader::create;
        }

        @Override
        public Module createEngineModule() {
            return new ModuleFactory().createPackageModule("org.destinationsol");
        }

        @Override
        public Class<?>[] getAPIClasses() {
            return new Class<?>[0];
        }
    }
}
