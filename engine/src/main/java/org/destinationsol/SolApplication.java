/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.sound.OggMusicManager;
import org.destinationsol.game.sound.OggSoundManager;
import org.destinationsol.menu.MenuScreens;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.valuetype.TypeHandler;
import org.terasology.valuetype.TypeLibrary;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SolApplication implements ApplicationListener {
    private static Logger logger = LoggerFactory.getLogger(SolApplication.class);

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

    private float timeAccumulator = 0;
    private boolean isMobile;

    public SolApplication() {
        // Initiate Box2D to make sure natives are loaded early enough
        Box2D.init();
    }

    @Override
    public void create() {
        isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
        if (isMobile) {
            DebugOptions.read(null);
        }
        options = new GameOptions(isMobile(), null);

        moduleManager = new ModuleManager();

        TypeLibrary typeLibrary = new TypeLibrary();
        typeLibrary.addHandler(new TypeHandler<>(Integer.class, Integer::new));
        typeLibrary.addHandler(new TypeHandler<>(Vector3.class, Vector3::new));

        musicManager = new OggMusicManager();
        soundManager = new OggSoundManager();
        inputManager = new SolInputManager(soundManager);

        musicManager.playMenuMusic(options);

        logger.info("\n\n ------------------------------------------------------------ \n");
        moduleManager.printAvailableModules();


        commonDrawer = new CommonDrawer();
        uiDrawer = new UiDrawer(commonDrawer);
        layouts = new SolLayouts(uiDrawer.r);
        menuScreens = new MenuScreens(layouts, isMobile(), uiDrawer.r, options);

        inputManager.setScreen(this, menuScreens.main);
    }

    @Override
    public void resize(int i, int i1) { }

    public void render() {
        timeAccumulator += Gdx.graphics.getDeltaTime();

        while (timeAccumulator > Const.REAL_TIME_STEP) {
            safeUpdate();
            timeAccumulator -= Const.REAL_TIME_STEP;
        }

        draw();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

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
            uiDrawer.draw(uiDrawer.whiteTex, uiDrawer.r, .5f, 0, 0, 0, .25f, 0, SolColor.UI_BG);
            uiDrawer.drawString(fatalErrorMsg, uiDrawer.r / 2, .5f, FontSize.MENU, true, SolColor.WHITE);
            uiDrawer.drawString(fatalErrorTrace, .2f * uiDrawer.r, .6f, FontSize.DEBUG, false, SolColor.WHITE);
        }
        DebugCollector.draw(uiDrawer);
        if (solGame == null) {
            uiDrawer.drawString("v" + Const.VERSION, 0.01f, .974f, FontSize.DEBUG, UiDrawer.TextAlignment.LEFT, false, SolColor.WHITE);
        }
        commonDrawer.end();
    }

    public void loadNewGame(boolean tut, String shipName) {
        if (solGame != null) {
            throw new AssertionError("Starting a new game with unfinished current one");
        }

        inputManager.setScreen(this, menuScreens.loading);
        menuScreens.loading.setMode(tut, shipName);
        musicManager.playGameMusic(options);
    }

    public void startNewGame(boolean tut, String shipName) {
        solGame = new SolGame(this, shipName, tut, commonDrawer);
        inputManager.setScreen(this, solGame.getScreens().mainScreen);
        musicManager.playGameMusic(options);
    }

    public SolInputManager getInputMan() {
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
}
