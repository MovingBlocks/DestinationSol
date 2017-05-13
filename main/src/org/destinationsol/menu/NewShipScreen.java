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

import com.badlogic.gdx.Input;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements SolUiScreen {
    private final List<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl cancelControl;
    private final SolUiControl okControl;

    NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        okControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
        okControl.setDisplayName("OK");
        controls.add(okControl);

        cancelControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.Ptr[] pointers, boolean clickedOutside) {
        if (cancelControl.isJustOff()) {
            solApplication.getInputMan().setScreen(solApplication, solApplication.getMenuScreens().newGame);
            return;
        }
        if (okControl.isJustOff()) {
            solApplication.loadNewGame(false, false);
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.W);
    }
}
