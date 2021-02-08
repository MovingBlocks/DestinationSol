package org.destinationsol.testsupport;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.Assets;
import org.destinationsol.modules.ModuleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;

/**
 * Use it for tests, which used {@link org.destinationsol.assets.Assets}
 */
@ResourceLock("AssetsHelper")
public interface AssetsHelperInitializer {
    ThreadLocal<TestGameStateObject> state = new ThreadLocal<>();

    @BeforeEach
    default void initAssets() throws Exception {
        TestGameStateObject stateObject = new TestGameStateObject();
        state.set(stateObject);
        stateObject.setApplication(new HeadlessApplication(new ApplicationAdapter() {
        }));

        ComponentManager componentManager = new ComponentManager();
        stateObject.setComponentManager(componentManager);

        ModuleManager moduleManager = new ModuleManager();
        moduleManager.init();
        stateObject.setModuleManager(moduleManager);

        AssetHelper helper = new AssetHelper();
        helper.init(moduleManager.getEnvironment(), componentManager, false);
        Assets.initialize(helper);
    }

    default ModuleManager getModuleManager() {
        return state.get().getModuleManager();
    }

    default Application getApplication() {
        return state.get().getApplication();
    }

    default ComponentManager getComponentManager() {
        return state.get().getComponentManager();
    }

    @AfterEach
    default void cleanupAssets() {
        state.remove();
    }

}
