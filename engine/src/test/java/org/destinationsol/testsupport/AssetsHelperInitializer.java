package org.destinationsol.testsupport;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.Assets;
import org.destinationsol.modules.ModuleManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;

/**
 * Use it for tests, which used {@link org.destinationsol.assets.Assets}
 */
@ResourceLock("AssetsHelper")
public interface AssetsHelperInitializer {

    @BeforeAll
    static void initAssets() throws Exception {
        if (Assets.getAssetHelper() == null) {
            new HeadlessApplication(null);
            AssetHelper helper = new AssetHelper();
            ModuleManager moduleManager = new ModuleManager();
            moduleManager.init();
            helper.init(moduleManager.getEnvironment(), new ComponentManager(), false);
            Assets.initialize(helper);
        }
    }

}
