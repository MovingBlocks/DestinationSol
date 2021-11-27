package org.destinationsol.modules;

import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.io.File;

/**
 * This interface defines the module configuration for a given facade. Different facades will have different implementations
 * and capabilities, so some properties that may vary between facades are defined here.
 */
public interface FacadeModuleConfig {
    /**
     * Returns the root folder to search for modules in. All modules should be located within this root folder.
     * @return the root module path
     */
    File getModulesPath();

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
     * Returns a list of classes that should be accessible from within the sandbox.
     * Any classes not part of the built-in list or this one cannot be used in module code.
     * @return a list of API classes.
     */
    Class<?>[] getAPIClasses();
}
