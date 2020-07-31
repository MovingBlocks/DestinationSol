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
package org.destinationsol;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.music.OggMusicManager;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.rendering.components.Renderable;
import org.destinationsol.rendering.events.RenderEvent;
import org.destinationsol.entitysystem.ComponentSystemManager;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.SerialisationManager;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SaveManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.console.adapter.ParameterAdapterManager;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.location.components.Position;
import org.destinationsol.menu.MenuScreens;
import org.destinationsol.menu.background.MenuBackgroundManager;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.ResizeSubscriber;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.util.FramerateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.module.sandbox.API;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@API
public class SolApplication implements ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(SolApplication.class);

    private final float targetFPS;

    @SuppressWarnings("FieldCanBeLocal")
    private final ModuleManager moduleManager;
    private EntitySystemManager entitySystemManager;

    private OggMusicManager musicManager;
    private OggSoundManager soundManager;
    private SolInputManager inputManager;
    private MenuBackgroundManager menuBackgroundManager;

    private UiDrawer uiDrawer;

    private FactionDisplay factionDisplay;
    private MenuScreens menuScreens;
    private SolLayouts layouts;
    private GameOptions options;
    private CommonDrawer commonDrawer;
    private String fatalErrorMsg;
    private String fatalErrorTrace;
    private SolGame solGame;
    private ParameterAdapterManager parameterAdapterManager;
    private Context context;
    // TODO: Make this non-static.
    public static DisplayDimensions displayDimensions;

    private float timeAccumulator = 0;
    private boolean isMobile;

    private ComponentManager componentManager;

    // TODO: Make this non-static.
    private static Set<ResizeSubscriber> resizeSubscribers;

    public SolApplication(ModuleManager moduleManager, float targetFPS) {
        // Initiate Box2D to make sure natives are loaded early enough
        Box2D.init();
        this.moduleManager = moduleManager;
        this.targetFPS = targetFPS;
        resizeSubscribers = new HashSet<>();
    }

    @Override
    public void create() {
        context = new ContextImpl();
        context.put(SolApplication.class, this);
        context.put(ModuleManager.class, moduleManager);
        isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
        if (isMobile) {
            DebugOptions.read(null);
        }
        options = new GameOptions(isMobile(), null);

        componentManager = new ComponentManager();
        AssetHelper helper = new AssetHelper();
        helper.init(moduleManager.getEnvironment(), componentManager, isMobile);
        Assets.initialize(helper);

        context.put(ComponentSystemManager.class, new ComponentSystemManager(moduleManager.getEnvironment(), context));
        logger.info("\n\n ------------------------------------------------------------ \n");
        moduleManager.printAvailableModules();

        musicManager = new OggMusicManager(options);
        soundManager = new OggSoundManager(context);
        inputManager = new SolInputManager(soundManager, context);

        musicManager.playMusic(OggMusicManager.MENU_MUSIC_SET, options);

        displayDimensions = new DisplayDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        commonDrawer = new CommonDrawer();
        uiDrawer = new UiDrawer(commonDrawer);
        layouts = new SolLayouts();

        menuBackgroundManager = new MenuBackgroundManager(displayDimensions);
        menuScreens = new MenuScreens(layouts, isMobile(), options);

        inputManager.setScreen(this, menuScreens.main);
        parameterAdapterManager = ParameterAdapterManager.createCore(this);
    }

    @Override
    public void resize(int newWidth, int newHeight) {
        displayDimensions.set(newWidth, newHeight);

        for (ResizeSubscriber resizeSubscriber : resizeSubscribers) {
            resizeSubscriber.resize();
        }
    }

    public void render() {
        timeAccumulator += Gdx.graphics.getDeltaTime();

        while (timeAccumulator > Const.REAL_TIME_STEP) {
            safeUpdate();
            timeAccumulator -= Const.REAL_TIME_STEP;
        }

        FramerateLimiter.synchronizeFPS(Math.round(targetFPS));

        try {
            draw();
        } catch (Throwable t) {
            logger.error("Fatal Error:", t);
            fatalErrorMsg = "A fatal error occurred:\n" + t.getMessage();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            fatalErrorTrace = sw.toString();

            if (!isMobile) {
                throw t;
            }
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    private void safeUpdate() {
        if (fatalErrorMsg != null) {
            return;
        }

        try {
            update();
        } catch (Throwable t) {
            logger.error("Fatal Error:", t);
            fatalErrorMsg = "A fatal error occurred:\n" + t.getMessage();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            fatalErrorTrace = sw.toString();

            if (!isMobile) {
                throw t;
            }
        }
    }

    private void update() {
        DebugCollector.update();

        if (DebugOptions.SHOW_FPS) {
            DebugCollector.debug("Fps", Gdx.graphics.getFramesPerSecond());
        }

        inputManager.update(this);

        if (solGame != null) {
            solGame.update();
        }

        SolMath.checkVectorsTaken(null);
    }

    private void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        commonDrawer.begin();

        if (solGame != null) {

            solGame.draw();

            //This event causes each entity with a `Graphics` component to be rendered onscreen
            entitySystemManager.sendEvent(new RenderEvent(), new Renderable(), new Position());
        }
        uiDrawer.updateMtx();
        inputManager.draw(uiDrawer, this);

        if (solGame != null) {
            solGame.drawDebugUi(uiDrawer);
            factionDisplay.drawFactionNames(solGame, uiDrawer, inputManager, solGame.getObjectManager());
        }
        if (fatalErrorMsg != null) {
            uiDrawer.draw(uiDrawer.whiteTexture, displayDimensions.getRatio(), .5f, 0, 0, 0, .25f, 0, SolColor.UI_BG);
            uiDrawer.drawString(fatalErrorMsg, displayDimensions.getRatio(), .5f, FontSize.MENU, true, SolColor.WHITE);
            uiDrawer.drawString(fatalErrorTrace, .2f * displayDimensions.getRatio(), .6f, FontSize.DEBUG, false, SolColor.WHITE);
        }
        DebugCollector.draw(uiDrawer);
        if (solGame == null) {
            uiDrawer.drawString("v" + Const.VERSION, 0.01f, .974f, FontSize.DEBUG, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        }
        commonDrawer.end();
    }

    public void play(boolean tut, String shipName, boolean isNewGame, WorldConfig worldConfig) {

        context.get(ComponentSystemManager.class).preBegin();
        solGame = new SolGame(shipName, tut, isNewGame, commonDrawer, context, worldConfig);
        context.put(SolGame.class, solGame);

        context.put(LootBuilder.class, solGame.getLootBuilder());
        context.put(ItemManager.class, solGame.getItemMan());
        context.put(ObjectManager.class, solGame.getObjectManager());

        entitySystemManager = new EntitySystemManager(moduleManager.getEnvironment(), componentManager, context);


        // Big, fat, ugly HACK to get a working classloader
        // Serialisation and thus a classloader is not needed when there are no components
        Iterator<Class<? extends Component>> componentClasses =
                moduleManager.getEnvironment().getSubtypesOf(Component.class).iterator();
        SerialisationManager serialisationManager = new SerialisationManager(
                SaveManager.getResourcePath("entity_store.dat"), entitySystemManager.getEntityManager(),
                componentClasses.hasNext() ? componentClasses.next().getClassLoader() : null);
        context.put(SerialisationManager.class, serialisationManager);

        if (!isNewGame) {
            try {
                context.get(SerialisationManager.class).deserialise();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        factionDisplay = new FactionDisplay(solGame.getCam());
        inputManager.setScreen(this, solGame.getScreens().mainGameScreen);
    }

    public SolInputManager getInputManager() {
        return inputManager;
    }

    public MenuScreens getMenuScreens() {
        return menuScreens;
    }

    public void dispose() {
        commonDrawer.dispose();

        if (solGame != null) {
            solGame.onGameEnd(context);
        }

        inputManager.dispose();
    }

    public SolGame getGame() {
        return solGame;
    }

    public ParameterAdapterManager getParameterAdapterManager() {
        return parameterAdapterManager;
    }

    public SolLayouts getLayouts() {
        return layouts;
    }

    public void finishGame() {
        solGame.onGameEnd(context);
        solGame = null;
        inputManager.setScreen(this, menuScreens.main);
    }

    public boolean isMobile() {
        return DebugOptions.EMULATE_MOBILE || isMobile;
    }

    public GameOptions getOptions() {
        return options;
    }

    public OggMusicManager getMusicManager() {
        return musicManager;
    }

    public OggSoundManager getSoundManager() {
        return soundManager;
    }

    public MenuBackgroundManager getMenuBackgroundManager() {
        return menuBackgroundManager;
    }

    // TODO: Make this non-static.
    public static void addResizeSubscriber(ResizeSubscriber resizeSubscriber) {
        resizeSubscribers.add(resizeSubscriber);
    }
}
