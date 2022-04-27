package org.destinationsol;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.music.OggMusicManager;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.console.Console;
import org.destinationsol.game.console.ConsoleImpl;
import org.destinationsol.game.screens.RightPaneLayout;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.menu.background.MenuBackgroundManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.nui.NUIManager;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.ServiceRegistry;
import org.terasology.gestalt.di.scanners.StandardScanner;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.nui.reflection.WidgetLibrary;
import org.terasology.reflection.copy.CopyStrategyLibrary;
import org.terasology.reflection.reflect.ReflectFactory;
import org.terasology.reflection.reflect.ReflectionReflectFactory;

public class CoreService extends ServiceRegistry {
    public CoreService(SolApplication application) {
        this.with(SolApplication.class).lifetime(Lifetime.Singleton).use(() -> application);
        this.with(CommonDrawer.class).lifetime(Lifetime.Singleton);
        this.with(GameOptions.class).lifetime(Lifetime.Singleton).use(() -> {
            boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
            if (application.isMobile()) {
                DebugOptions.read(null);
            }
            return new GameOptions(isMobile, null);
        });
        this.with(ModuleManager.class).lifetime(Lifetime.Singleton);

        this.with(ComponentManager.class).lifetime(Lifetime.Singleton).use(ComponentManager::new);
        this.with(AssetHelper.class).lifetime(Lifetime.Singleton);

        this.with(DisplayDimensions.class).lifetime(Lifetime.Singleton).use(() -> new DisplayDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        this.with(UiDrawer.class).lifetime(Lifetime.Singleton);
        this.with(SolLayouts.class).lifetime(Lifetime.Singleton);
        this.with(RightPaneLayout.class).lifetime(Lifetime.Singleton);
        this.with(MenuLayout.class).lifetime(Lifetime.Singleton);

        this.with(NUIManager.class).lifetime(Lifetime.Singleton);
        this.with(MenuBackgroundManager.class).lifetime(Lifetime.Singleton);

        this.with(ReflectFactory.class).lifetime(Lifetime.Singleton).use(ReflectionReflectFactory.class);
        this.with(CopyStrategyLibrary.class).lifetime(Lifetime.Singleton);

        this.with(WidgetLibrary.class).lifetime(Lifetime.Singleton);
        this.with(SolInputManager.class).lifetime(Lifetime.Singleton);
        this.registerScanner(new StandardScanner("org.destinationsol.assets"));
        this.registerScanner(new StandardScanner("org.destinationsol.ui.nui"));

        // TODO seems this is should be GameService before game
        this.with(Console.class).lifetime(Lifetime.Singleton).use(ConsoleImpl.class);
    }

}
