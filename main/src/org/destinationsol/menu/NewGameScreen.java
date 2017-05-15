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

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SaveManager;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements SolUiScreen {
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl backControl;
    private final SolUiControl previousControl;
    private final SolUiControl newControl;

    NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        previousControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
        previousControl.setDisplayName("Previous Ship");
        controls.add(previousControl);

        newControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        newControl.setDisplayName("New Ship");
        controls.add(newControl);

        backControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Cancel");
        controls.add(backControl);

    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        previousControl.setEnabled(SaveManager.hasPrevShip());
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        MenuScreens screens = solApplication.getMenuScreens();
        SolInputManager im = solApplication.getInputMan();
        if (backControl.isJustOff()) {
            im.setScreen(solApplication, screens.main);
            return;
        }
        if (previousControl.isJustOff()) {
            solApplication.loadNewGame(false, true);
            return;
        }
        if (newControl.isJustOff()) {
            if (!previousControl.isEnabled()) {
                solApplication.loadNewGame(false, false);
            } else {
                im.setScreen(solApplication, screens.newShip);
            }
        }
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
        return true;
    }
}
