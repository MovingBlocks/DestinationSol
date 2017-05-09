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
    public static final float CREDITS_BTN_W = .15f;
    public static final float CREDITS_BTN_H = .07f;

    private final ArrayList<SolUiControl> myControls;
    private final SolUiControl myTutCtrl;
    private final SolUiControl myOptionsCtrl;
    private final SolUiControl myExitCtrl;
    private final SolUiControl myNewGameCtrl;
    private final SolUiControl myCreditsCtrl;
    private final TextureAtlas.AtlasRegion myTitleLogo;
    private final TextureAtlas.AtlasRegion myTitleBg;
    private final boolean isMobile;
    GameOptions gameOptions;

    public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
        isMobile = mobile;
        myControls = new ArrayList<SolUiControl>();
        this.gameOptions = gameOptions;

        myTutCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
        myTutCtrl.setDisplayName("Tutorial");
        myControls.add(myTutCtrl);

        myNewGameCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyShoot());
        myNewGameCtrl.setDisplayName("New Game");
        myControls.add(myNewGameCtrl);

        myOptionsCtrl = new SolUiControl(mobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        myOptionsCtrl.setDisplayName("Options");
        myControls.add(myOptionsCtrl);

        myExitCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        myExitCtrl.setDisplayName("Exit");
        myControls.add(myExitCtrl);

        myCreditsCtrl = new SolUiControl(creditsBtnRect(r), true, Input.Keys.C);
        myCreditsCtrl.setDisplayName("Credits");
        myControls.add(myCreditsCtrl);

        myTitleLogo = textureManager.getTex("ui/titleLogo", null);
        myTitleBg = textureManager.getTex("ui/titleBg", null);
    }

    public static Rectangle creditsBtnRect(float r) {
        return new Rectangle(r - CREDITS_BTN_W, 1 - CREDITS_BTN_H, CREDITS_BTN_W, CREDITS_BTN_H);
    }

    public List<SolUiControl> getControls() {
        return myControls;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
        if (cmp.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
            myTutCtrl.setEnabled(false);
        } else {
            myTutCtrl.setEnabled(true);
        }

        if (myTutCtrl.isJustOff()) {
            cmp.loadNewGame(true, false);
            return;
        }
        SolInputManager im = cmp.getInputMan();
        MenuScreens screens = cmp.getMenuScreens();
        if (myNewGameCtrl.isJustOff()) {
            im.setScreen(cmp, screens.newGame);
            return;
        }
        if (myOptionsCtrl.isJustOff()) {
            im.setScreen(cmp, screens.options);
            return;
        }
        if (myExitCtrl.isJustOff()) {
            // Save the settings on exit, but not on mobile as settings don't exist there.
            if (isMobile == false) {
                cmp.getOptions().save();
            }
            Gdx.app.exit();
            return;
        }
        if (myCreditsCtrl.isJustOff()) {
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
        uiDrawer.draw(myTitleBg, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.W);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE)
            uiDrawer.draw(myTitleLogo, sx, sy, sx / 2, sy / 2, uiDrawer.r / 2, 0.1f + sy / 2, 0, SolColor.W);
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
