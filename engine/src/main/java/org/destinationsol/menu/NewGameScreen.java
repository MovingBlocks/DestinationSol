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

package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SaveManager;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl backControl;
    private final SolUiControl continueControl;
    private final SolUiControl newControl;

    NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        continueControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
        continueControl.setDisplayName("Continue");
        controls.add(continueControl);

        newControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        newControl.setDisplayName("New game");
        controls.add(newControl);

        backControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Cancel");
        controls.add(backControl);

        bgTex = Assets.getAtlasRegion("engine:mainMenuBg", Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        continueControl.setEnabled(SaveManager.hasPrevShip());
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        MenuScreens screens = solApplication.getMenuScreens();
        SolInputManager im = solApplication.getInputMan();
        if (backControl.isJustOff()) {
            im.setScreen(solApplication, screens.main);
            return;
        }
        if (continueControl.isJustOff()) {
            solApplication.loadNewGame(false, null);
            return;
        }
        if (newControl.isJustOff()) {
            im.setScreen(solApplication, screens.newShip);
        }
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
        return true;
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }
}
