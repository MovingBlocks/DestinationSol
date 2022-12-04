/*
 * Copyright 2022 The Terasology Foundation
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

package org.destinationsol.ui.nui.screens;

import org.destinationsol.game.SolGame;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.nui.widgets.UILabel;

import javax.inject.Inject;

/**
 * This screen displays the message box shown during the tutorial to instruct the user.
 * It is unusual in that it should always be rendered on-top of all other UI screens.
 * See {@link #moveToTop()} and {@link org.destinationsol.ui.TutorialManager#update(SolGame, float)} for how this is done.
 */
public class TutorialScreen extends NUIScreenLayer {
    private UILabel tutorialText;
    private boolean isReplaceRemove;

    @Inject
    public TutorialScreen() {
    }

    @Override
    public void initialise() {
        tutorialText = find("tutorialText", UILabel.class);
    }

    public String getTutorialText() {
        return tutorialText.getText();
    }

    public void setTutorialText(String text) {
        tutorialText.setText(text);
    }

    @Override
    public boolean isBlockingInput() {
        return false;
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    public void moveToTop() {
        isReplaceRemove = true;
        nuiManager.removeScreen(this);
        isReplaceRemove = false;
        nuiManager.pushScreen(this);
    }

    @Override
    public void onRemoved() {
        if (isReplaceRemove) {
            return;
        }

        // This screen is always on-top, so when other screens call popScreen,
        // we should remove the screen underneath us, since this was likely the intended behaviour.
        if (nuiManager.getScreens().size() > 1 &&
                !(nuiManager.getTopScreen() instanceof MainGameScreen) &&
                !(nuiManager.getTopScreen() instanceof UIShipControlsScreen)) {
            nuiManager.popScreen();
        }
    }
}
