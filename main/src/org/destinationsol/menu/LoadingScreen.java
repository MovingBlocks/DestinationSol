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

import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class LoadingScreen implements SolUiScreen {
    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private boolean loadTutorial;
    private boolean usePreviousShip;

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.Ptr[] pointers, boolean clickedOutside) {
        solApplication.startNewGame(loadTutorial, usePreviousShip);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("Loading...", uiDrawer.r / 2, .5f, FontSize.MENU, true, SolColor.W);
    }

    public void setMode(boolean loadTutorial, boolean usePreviousShip) {
        this.loadTutorial = loadTutorial;
        this.usePreviousShip = usePreviousShip;
    }
}
