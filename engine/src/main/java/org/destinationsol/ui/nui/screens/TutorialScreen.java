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
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.widgets.UIBox;
import org.terasology.nui.widgets.UILabel;

import javax.inject.Inject;

/**
 * This screen displays the message box shown during the tutorial to instruct the user.
 * It is unusual in that it should always be rendered on-top of all other UI screens.
 * See {@link #moveToTop()} and {@link org.destinationsol.ui.TutorialManager#update(SolGame, float)} for how this is done.
 */
public class TutorialScreen extends NUIScreenLayer {
    private UIBox tutorialBoxLeft;
    private UILabel tutorialTextLeft;
    private UIBox tutorialBoxCentre;
    private UILabel tutorialTextCentre;
    private boolean isReplaceRemove;

    @Inject
    public TutorialScreen() {
    }

    @Override
    public void initialise() {
        tutorialBoxLeft = find("tutorialBoxLeft", UIBox.class);
        tutorialTextLeft = find("tutorialTextLeft", UILabel.class);
        tutorialBoxCentre = find("tutorialBoxCentre", UIBox.class);
        tutorialTextCentre = find("tutorialTextCentre", UILabel.class);
    }

    public String getTutorialText() {
        return getTutorialText(HorizontalAlign.CENTER);
    }

    public String getTutorialText(HorizontalAlign horizontalAlign) {
        return getTutorialTextLabel(horizontalAlign).getText();
    }

    public void setTutorialText(String text) {
        setTutorialText(text, HorizontalAlign.CENTER);
    }

    public void setTutorialText(String text, HorizontalAlign horizontalAlign) {
        getTutorialTextLabel(horizontalAlign).setText(text);
        getTutorialBox(horizontalAlign).setVisible(!text.isEmpty());
    }

    public void clearAllTutorialBoxes() {
        tutorialBoxLeft.setVisible(false);
        tutorialBoxCentre.setVisible(false);
    }

    @Override
    public boolean isBlockingInput() {
        return false;
    }

    protected UILabel getTutorialTextLabel(HorizontalAlign horizontalAlign) {
        switch (horizontalAlign) {
            case LEFT:
                return tutorialTextLeft;
            case CENTER:
                return tutorialTextCentre;
            default:
                return tutorialTextCentre;
        }
    }

    protected UIBox getTutorialBox(HorizontalAlign horizontalAlign) {
        switch (horizontalAlign) {
            case LEFT:
                return tutorialBoxLeft;
            case CENTER:
                return tutorialBoxCentre;
            default:
                return tutorialBoxLeft;
        }
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
