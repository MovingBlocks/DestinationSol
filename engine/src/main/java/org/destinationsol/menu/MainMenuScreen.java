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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiActionButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextBox;
import org.destinationsol.ui.responsiveUi.UiTextButton;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM;
import static org.destinationsol.ui.UiDrawer.UI_POSITION_BOTTOM_RIGHT;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_HEIGHT;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_PADDING;
import static org.destinationsol.ui.responsiveUi.UiTextButton.DEFAULT_BUTTON_WIDTH;

public class MainMenuScreen extends SolUiBaseScreen {
    private final TextureAtlas.AtlasRegion logoTexture;
    private final TextureAtlas.AtlasRegion backgroundTexture;

    private DisplayDimensions displayDimensions;
    private GameOptions gameOptions;

    MainMenuScreen(boolean isMobile, GameOptions gameOptions) {
        this.gameOptions = gameOptions;

        displayDimensions = SolApplication.displayDimensions;

        UiVerticalListLayout buttonList = new UiVerticalListLayout();

        buttonList.addElement(new UiActionButton().addElement(new UiTextBox().setText("Tutorial").setFontSize(FontSize.MENU))
                .setKeyCode(Input.Keys.T)
                .setSoundEnabled(true)
                .setAction(uiElement -> SolApplication.getInstance().play(true, "Imperial Small", true)));

        buttonList.addElement(new UiActionButton().addElement(new UiTextBox().setText("Play Game").setFontSize(FontSize.MENU))
                .setKeyCode(gameOptions.getKeyShoot())
                .setSoundEnabled(true)
                .setAction(uiElement -> SolApplication.changeScreen(SolApplication.getMenuScreens().newGameScreen)));

        // TODO: Temporarily showing on mobile as well. Fix!
        // TODO: Actually, think about why we don't want it on mobile as well first.
        // optionsControl = new SolUiControl(isMobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        buttonList.addElement(new UiActionButton().addElement(new UiTextBox().setText("Options").setFontSize(FontSize.MENU))
                .setKeyCode(Input.Keys.O)
                .setSoundEnabled(true)
                .setAction(uiElement -> SolApplication.changeScreen(SolApplication.getMenuScreens().optionsScreen)));

        buttonList.addElement(new UiActionButton().addElement(new UiTextBox().setText("Exit").setFontSize(FontSize.MENU))
                .setKeyCode(gameOptions.getKeyEscape())
                .setSoundEnabled(true)
                .setAction(uiElement -> {
                    // Save the settings on exit, but not on mobile as settings don't exist there.
                    if (!isMobile) {
                        SolApplication.getInstance().getOptions().save();
                    }
                    Gdx.app.exit();
                }));

        UiTextButton creditsButton = new UiTextButton().setDisplayName("Credits")
                .setTriggerKey(Input.Keys.C)
                .enableSound()
                .setOnReleaseAction(uiElement -> SolApplication.changeScreen(SolApplication.getMenuScreens().creditsScreen));

        rootUiElement = new UiRelativeLayout().addElement(buttonList, UI_POSITION_BOTTOM, 0, -buttonList.getHeight() / 2 - DEFAULT_BUTTON_PADDING)
                .addElement(creditsButton, UI_POSITION_BOTTOM_RIGHT, -DEFAULT_BUTTON_WIDTH / 2 - DEFAULT_BUTTON_PADDING, -DEFAULT_BUTTON_HEIGHT / 2 - DEFAULT_BUTTON_PADDING);

        backgroundTexture = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
        logoTexture = Assets.getAtlasRegion("engine:mainMenuLogo", Texture.TextureFilter.Linear);
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        // TODO: Disabled for now, since I can't figure out why controller players shouldn't be able to play the tutorial.
        // tutorialControl.setEnabled(solApplication.getOptions().controlType != GameOptions.ControlType.CONTROLLER);
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
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE) {
            uiDrawer.draw(logoTexture, sx, sy, sx / 2, sy / 2, displayDimensions.getRatio() / 2, 0.1f + sy / 2, 0, SolColor.WHITE);
        }
    }
}
