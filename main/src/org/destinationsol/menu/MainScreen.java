/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
    private final boolean isMobile;
    private final GameOptions gameOptions;

    private final TextureAtlas.AtlasRegion titleLogoTex;
    private final TextureAtlas.AtlasRegion titleBgTex;

    private final ArrayList<SolUiControl> controls;
    private final SolUiControl tutCtrl;
    private final SolUiControl optionsCtrl;
    private final SolUiControl exitCtrl;
    private final SolUiControl newGameCtrl;
    private final SolUiControl creditsCtrl;

    public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
        isMobile = mobile;
        controls = new ArrayList<>();
        this.gameOptions = gameOptions;

        tutCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
        tutCtrl.setDisplayName("Tutorial");
        controls.add(tutCtrl);

        newGameCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyShoot());
        newGameCtrl.setDisplayName("New Game");
        controls.add(newGameCtrl);

        optionsCtrl = new SolUiControl(mobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        optionsCtrl.setDisplayName("Options");
        controls.add(optionsCtrl);

        exitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        exitCtrl.setDisplayName("Exit");
        controls.add(exitCtrl);

        creditsCtrl = new SolUiControl(creditsBtnRect(r), true, Input.Keys.C);
        creditsCtrl.setDisplayName("Credits");
        controls.add(creditsCtrl);

        titleLogoTex = textureManager.getTexture("ui/titleLogo");
        titleBgTex = textureManager.getTexture("ui/titleBg");
    }

    public static Rectangle creditsBtnRect(float r) {
        final float CREDITS_BTN_W = .15f;
        final float CREDITS_BTN_H = .07f;

        return new Rectangle(r - CREDITS_BTN_W, 1 - CREDITS_BTN_H, CREDITS_BTN_W, CREDITS_BTN_H);
    }

    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
        if (cmp.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
            tutCtrl.setEnabled(false);
        } else {
            tutCtrl.setEnabled(true);
        }

        if (tutCtrl.isJustOff()) {
            cmp.loadNewGame(true, false);
            return;
        }
        SolInputManager im = cmp.getInputMan();
        MenuScreens screens = cmp.getMenuScreens();
        if (newGameCtrl.isJustOff()) {
            im.setScreen(cmp, screens.newGame);
            return;
        }
        if (optionsCtrl.isJustOff()) {
            im.setScreen(cmp, screens.options);
            return;
        }
        if (exitCtrl.isJustOff()) {
            // Save the settings on exit, but not on mobile as settings don't exist there.
            if (isMobile == false) {
                cmp.getOptions().save();
            }
            Gdx.app.exit();
            return;
        }
        if (creditsCtrl.isJustOff()) {
            im.setScreen(cmp, screens.credits);
        }
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
        return false;
    }

    @Override
    public void onAdd(SolApplication cmp) {
        cmp.getMusicManager().playMenuMusic(gameOptions);
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
        uiDrawer.draw(titleBgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.W);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE)
            uiDrawer.draw(titleLogoTex, sx, sy, sx / 2, sy / 2, uiDrawer.r / 2, 0.1f + sy / 2, 0, SolColor.W);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }

    @Override
    public void blurCustom(SolApplication cmp) {

    }
}
