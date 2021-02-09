package org.destinationsol.testsupport;

import com.badlogic.gdx.Application;
import org.destinationsol.modules.ModuleManager;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;

/**
 * Partial Initialized game state using for easier access from tests.
 * <p>
 * This is internal class. initilized and used by {@link AssetsHelperInitializer only}
 */
class TestGameStateObject {
    private Application application;
    private ModuleManager moduleManager;
    private ComponentManager componentManager;

    public Application getApplication() {
        return application;
    }

    public TestGameStateObject setApplication(Application application) {
        this.application = application;
        return this;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public TestGameStateObject setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        return this;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public TestGameStateObject setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
        return this;
    }
}
