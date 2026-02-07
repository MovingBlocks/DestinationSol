package org.destinationsol.modules;

import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * This interface defines the module configuration for a given facade. Different facades will have different implementations
 * and capabilities, so some properties that may vary between facades are defined here.
 */
public interface FacadeModuleConfig {
    /**
     * Returns a collection of root folders to search for modules in. All modules should be located within these folders.
     * @return the root module paths
     */
    Collection<File> getModulePaths();

    /**
     * Determines if the game uses SecurityManager for gestalt sandboxing.
     * This should usually be true, unless running on a platform that does not support it.
     * @return true, if SecurityManager should be used, otherwise false.
     */
    boolean useSecurityManager();

    /**
     * Returns a method used to construct the class loader for a given module, parent and permissions.
     * @return the class loader supplier method.
     */
    ModuleEnvironment.ClassLoaderSupplier getClassLoaderSupplier();

    /**
     * Constructs the engine module from the base classpath and returns it.
     * @return the constructed engine module.
     */
    Module createEngineModule();

    /**
     * Constructs facade-specific modules from the base classpath and returns them.
     * @return the constructed facade-specific modules
     */
    default Collection<Module> createFacadeModules() {
        return Collections.emptyList();
    }

    default ModuleFactory createModuleFactory() {
        return new ModuleFactory();
    }

    /**
     * Returns a list of classes that should be accessible from within the sandbox.
     * Any classes not part of the built-in list or this one cannot be used in module code.
     * @return a list of API classes.
     */
    Class<?>[] getAPIClasses();
}
