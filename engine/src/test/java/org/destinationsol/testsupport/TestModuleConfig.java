package org.destinationsol.testsupport;

import org.destinationsol.modules.FacadeModuleConfig;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.sandbox.JavaModuleClassLoader;

import java.io.File;
import java.nio.file.Paths;

/**
 * Defines the settings used for module-based tests.
 *
 * This is currently just a copy of SolDesktop::DesktopModuleConfig but could be customised further for test-specific
 * module paths.
 */
public class TestModuleConfig implements FacadeModuleConfig {
    @Override
    public File getModulesPath() {
        return Paths.get(".").resolve("modules").toFile();
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