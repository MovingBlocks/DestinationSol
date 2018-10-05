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
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SaveManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.menu.MenuScreens;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.ResizeSubscriber;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class SolApplication implements ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(SolApplication.class);

    @SuppressWarnings("FieldCanBeLocal")
    private ModuleManager moduleManager;

    private OggMusicManager musicManager;
    private OggSoundManager soundManager;
    private SolInputManager inputManager;

    private UiDrawer uiDrawer;

    private MenuScreens menuScreens;
    private SolLayouts layouts;
    private GameOptions options;
    private CommonDrawer commonDrawer;
    private String fatalErrorMsg;
    private String fatalErrorTrace;
    private SolGame solGame;
    private Context context;

    private WorldConfig worldConfig;
    // TODO: Make this non-static.
    public static DisplayDimensions displayDimensions;

    private float timeAccumulator = 0;
    private boolean isMobile;

    // TODO: Make this non-static.
    private static Set<ResizeSubscriber> resizeSubscribers;

    public SolApplication() {
        // Initiate Box2D to make sure natives are loaded early enough
        Box2D.init();
    }

    @Override
    public void create() {
        resizeSubscribers = new HashSet<>();

        context = new ContextImpl();
        context.put(SolApplication.class, this);
        worldConfig = new WorldConfig();
        isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
        if (isMobile) {
            DebugOptions.read(null);
        }
        options = new GameOptions(isMobile(), null);

        moduleManager = new ModuleManager();

        logger.info("\n\n ------------------------------------------------------------ \n");
        moduleManager.printAvailableModules();

        musicManager = new OggMusicManager();
        soundManager = new OggSoundManager(context);
        inputManager = new SolInputManager(soundManager);

        musicManager.playMusic(OggMusicManager.MENU_MUSIC_SET, options);

        displayDimensions = new DisplayDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        commonDrawer = new CommonDrawer();
        uiDrawer = new UiDrawer(commonDrawer);
        layouts = new SolLayouts();
        menuScreens = new MenuScreens(layouts, isMobile(), options);

        inputManager.setScreen(this, menuScreens.main);
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

        draw();
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
        }
        uiDrawer.updateMtx();
        inputManager.draw(uiDrawer, this);
        if (solGame != null) {
            solGame.drawDebugUi(uiDrawer);
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

    public void loadGame(boolean tut, String shipName, boolean isNewGame) {
        if (solGame != null) {
            throw new AssertionError("Starting a new game with unfinished current one");
        }

        inputManager.setScreen(this, menuScreens.loading);
        menuScreens.loading.setMode(tut, shipName, isNewGame);
        musicManager.playMusic(OggMusicManager.GAME_MUSIC_SET, options);
    }

    public void play(boolean tut, String shipName, boolean isNewGame) {
        if (isNewGame) {
            beforeNewGame();
        } else {
            beforeLoadGame();
        }

        solGame = new SolGame(shipName, tut, isNewGame, commonDrawer, context, worldConfig);
        inputManager.setScreen(this, solGame.getScreens().mainGameScreen);
        musicManager.playMusic(OggMusicManager.GAME_MUSIC_SET, options);
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
            solGame.onGameEnd();
        }

        inputManager.dispose();
    }

    public SolGame getGame() {
        return solGame;
    }

    public SolLayouts getLayouts() {
        return layouts;
    }

    public void finishGame() {
        solGame.onGameEnd();
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


     /** This method is called when the "New Game" button gets pressed. It sets the seed for random generation, and the number of systems */
    private void beforeNewGame() {
        // Reset the seed so this galaxy isn't the same as the last
        worldConfig.setSeed(System.currentTimeMillis());
        SolRandom.setSeed(worldConfig.getSeed());

        worldConfig.setNumberOfSystems(getMenuScreens().newShip.getNumberOfSystems());
    }

     /** This method is called when the "Continue" button gets pressed. It loads the world file to get the seed used for the world generation, and the number of systems */
    private void beforeLoadGame() {
        WorldConfig config = SaveManager.loadWorld();
        if (config != null) {
            worldConfig = config;
            SolRandom.setSeed(worldConfig.getSeed());
        }
    }

    // TODO: Make this non-static.
    public static void addResizeSubscriber(ResizeSubscriber resizeSubscriber) {
        resizeSubscribers.add(resizeSubscriber);
    }
}
