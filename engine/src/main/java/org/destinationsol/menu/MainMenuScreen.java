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

package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

public class MainMenuScreen extends SolUiBaseScreen {
    private final boolean isMobile;
    private final GameOptions gameOptions;

    private final TextureAtlas.AtlasRegion logoTexture;
    private final TextureAtlas.AtlasRegion backgroundTexture;
    private DisplayDimensions displayDimensions;

    private List<FloatingObject> floatingObjects = new ArrayList<>();
    private List<FloatingObject> retainedFloatingObjects = new ArrayList<>();

    private final SolUiControl tutorialControl;
    private final SolUiControl optionsControl;
    private final SolUiControl exitControl;
    private final SolUiControl newGameControl;
    private final SolUiControl creditsControl;

    private final int buttonWidth = 300;
    private final int buttonHeight = 75;
    private final int buttonPadding = 10;

    MainMenuScreen(boolean isMobile, GameOptions gameOptions) {
        this.isMobile = isMobile;
        this.gameOptions = gameOptions;

        displayDimensions = SolApplication.displayDimensions;

        tutorialControl = new SolUiControl(buttonWidth, buttonHeight, UiDrawer.positions.get("bottom"), 0, calculateButtonOffsetFromBottom(3), true, Input.Keys.T);
        tutorialControl.setDisplayName("Tutorial");
        controls.add(tutorialControl);

        newGameControl = new SolUiControl(buttonWidth, buttonHeight, UiDrawer.positions.get("bottom"), 0, calculateButtonOffsetFromBottom(2), true, gameOptions.getKeyShoot());
        newGameControl.setDisplayName("Play Game");
        controls.add(newGameControl);

        // TODO: Temporarily showing on mobile as well. Fix!
        // optionsControl = new SolUiControl(isMobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        optionsControl = new SolUiControl(buttonWidth, buttonHeight, UiDrawer.positions.get("bottom"), 0, calculateButtonOffsetFromBottom(1), true, Input.Keys.O);
        optionsControl.setDisplayName("Options");
        controls.add(optionsControl);

        exitControl = new SolUiControl(buttonWidth, buttonHeight, UiDrawer.positions.get("bottom"), 0, calculateButtonOffsetFromBottom(0), true, gameOptions.getKeyEscape());
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);

        creditsControl = new SolUiControl(MenuLayout.bottomRightFloatingButton(displayDimensions), true, Input.Keys.C);
        creditsControl.setDisplayName("Credits");
        controls.add(creditsControl);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
        logoTexture = Assets.getAtlasRegion("engine:mainMenuLogo", Texture.TextureFilter.Linear);

        for (int i = 0; i < 10; i++) {
            floatingObjects.add(new FloatingObject());
        }
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        tutorialControl.setEnabled(solApplication.getOptions().controlType != GameOptions.ControlType.CONTROLLER);

        if (tutorialControl.isJustOff()) {
            solApplication.play(true, "Imperial Small", true);
            return;
        }

        SolInputManager inputManager = solApplication.getInputManager();
        MenuScreens screens = solApplication.getMenuScreens();

        if (newGameControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.newGame);
            return;
        }

        if (optionsControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.options);
            return;
        }

        if (exitControl.isJustOff()) {
            // Save the settings on exit, but not on mobile as settings don't exist there.
            if (!isMobile) {
                solApplication.getOptions().save();
            }
            Gdx.app.exit();
            return;
        }

        if (creditsControl.isJustOff()) {
            inputManager.setScreen(solApplication, screens.credits);
        }

        retainedFloatingObjects.clear();

        float radius = (float)Math.sqrt(0.25 + Math.pow(displayDimensions.getRatio()/2, 2));
        for (FloatingObject floatingObject : floatingObjects) {
            floatingObject.update();

            float distance = (float)Math.sqrt(Math.pow(floatingObject.position.x - displayDimensions.getRatio()/2, 2) + Math.pow(floatingObject.position.y - 0.5f, 2));
            if (distance < radius) {
                retainedFloatingObjects.add(floatingObject);
            } else {
                retainedFloatingObjects.add(new FloatingObject());
            }
        }

        floatingObjects.clear();
        floatingObjects.addAll(retainedFloatingObjects);
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        solApplication.getMusicManager().playMusic(OggMusicManager.MENU_MUSIC_SET, gameOptions);
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(backgroundTexture, displayDimensions.getRatio(), 1, displayDimensions.getRatio() / 2, 0.5f, displayDimensions.getRatio() / 2, 0.5f, 0, SolColor.WHITE);
    }

    @Override
    public void drawImages(UiDrawer uiDrawer, SolApplication solApplication) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE) {
            uiDrawer.draw(logoTexture, sx, sy, sx / 2, sy / 2, displayDimensions.getRatio() / 2, 0.1f + sy / 2, 0, SolColor.WHITE);
        }

        for (FloatingObject floatingObject : floatingObjects) {
            floatingObject.draw(uiDrawer);
        }
    }

    /**
     * @param buttonIndex the index of the button, starting from 0 for the bottom-most button
     * @return the number of pixels to go up from the bottom of the screen for the {@code buttonIndex}th button
     */
    private int calculateButtonOffsetFromBottom(int buttonIndex) {
        return -(buttonPadding + buttonHeight / 2) - (buttonIndex * (buttonPadding + buttonHeight));
    }

    class FloatingObject {
        private TextureAtlas.AtlasRegion texture;

        private float scale;
        private Color tint;

        private Vector2 position;
        private Vector2 velocity;

        private float angle;
        private float angularVelocity;

        private float radiusX;
        private float radiusY;

        private FloatingObject() {
            texture = Assets.getAtlasRegion(SolRandom.test(0.3f) ? "engine:desertBoss" : "engine:desertSmall");

            boolean small = SolRandom.test(.7f);
            scale = (small ? .1f : .2f) * SolRandom.randomFloat(.4f, 1);
            tint = new Color();
            SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .2f, 1, .7f, tint);

            radiusX = (float)(texture.originalHeight) / displayDimensions.getWidth() * scale / 2;
            radiusY = (float)(texture.originalHeight) / displayDimensions.getHeight() * scale / 2;

            velocity = new Vector2((float)Math.pow(SolRandom.randomFloat(0.3f), 2), (float)Math.pow(SolRandom.randomFloat(0.1f), 2));
            float r = displayDimensions.getRatio();
            if (SolRandom.test(0.5f)) {
                // Spawn to the left or right of screen
                boolean toLeft = SolRandom.test(0.1f);
                position = new Vector2(r/2 + (toLeft ? -1 : 1) * (r/2 + radiusX) - radiusX, 0.5f + SolRandom.randomFloat(0.5f + radiusY) - radiusY);
            } else {
                // Spawn at the top or bottom of screen
                boolean atTop = SolRandom.test(0.5f);
                position = new Vector2(r/2 + SolRandom.randomFloat(r/2 + radiusX) - radiusX, 0.5f + (atTop ? -1 : 1) * (0.5f + radiusY) - radiusY);
            }

            angle = SolRandom.randomFloat((float)Math.PI);
            angularVelocity = SolRandom.randomFloat(.1f);
        }

        private void update() {
            position.add(velocity);
            angle += angularVelocity;
        }

        private void draw(UiDrawer drawer) {
            drawer.draw(texture, scale, scale, scale / 2, scale / 2, position.x, position.y, angle, tint);
        }
    }
}