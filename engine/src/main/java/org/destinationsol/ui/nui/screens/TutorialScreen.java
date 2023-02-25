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
import org.destinationsol.ui.nui.widgets.InteractHint;
import org.terasology.input.Input;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.widgets.UIBox;
import org.terasology.nui.widgets.UILabel;

import javax.inject.Inject;
import java.util.EnumMap;

/**
 * This screen displays the message box shown during the tutorial to instruct the user.
 * It is unusual in that it should always be rendered on-top of all other UI screens.
 * See {@link #moveToTop()} and {@link org.destinationsol.ui.TutorialManager#update(SolGame, float)} for how this is done.
 */
public class TutorialScreen extends NUIScreenLayer {
    private static final class TutorialBox {
        public final UIBox box;
        public final UILabel text;
        public final InteractHint interactHint;

        public TutorialBox(UIBox box, UILabel text, InteractHint interactHint) {
            this.box = box;
            this.text = text;
            this.interactHint = interactHint;
        }
    }

    private final EnumMap<HorizontalAlign, TutorialBox> tutorialBoxes;
    private boolean isReplaceRemove;

    @Inject
    public TutorialScreen() {
        tutorialBoxes = new EnumMap<>(HorizontalAlign.class);
    }

    @Override
    public void initialise() {
        for (HorizontalAlign horizontalAlign : HorizontalAlign.values()) {
            tutorialBoxes.put(horizontalAlign, new TutorialBox(
                    find("tutorialBox" + horizontalAlign.toString(), UIBox.class),
                    find("tutorialText" + horizontalAlign.toString(), UILabel.class),
                    find("interactHint" + horizontalAlign.toString(), InteractHint.class)
            ));
        }
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

    public Input getInteractHintInput() {
        return getInteractHintInput(HorizontalAlign.CENTER);
    }

    public Input getInteractHintInput(HorizontalAlign horizontalAlign) {
        return getInteractHint(horizontalAlign).getInput();
    }

    public void setInteractHintInput(Input input) {
        setInteractHintInput(HorizontalAlign.CENTER, input);
    }

    public void setInteractHintInput(HorizontalAlign horizontalAlign, Input input) {
        InteractHint interactHint = getInteractHint(horizontalAlign);
        if (input != null) {
            getInteractHint(horizontalAlign).setInput(input);
            interactHint.setVisible(true);
        } else {
            interactHint.setVisible(false);
        }
    }

    public void clearAllTutorialBoxes() {
        for (TutorialBox tutorialBox : tutorialBoxes.values()) {
            if (tutorialBox.box != null) {
                tutorialBox.box.setVisible(false);
            }
            if (tutorialBox.text != null) {
                tutorialBox.text.setText("");
            }
            if (tutorialBox.interactHint != null) {
                tutorialBox.interactHint.setVisible(false);
            }
        }
    }

    @Override
    public boolean isBlockingInput() {
        return false;
    }

    protected UILabel getTutorialTextLabel(HorizontalAlign horizontalAlign) {
        return tutorialBoxes.get(horizontalAlign).text;
    }

    protected UIBox getTutorialBox(HorizontalAlign horizontalAlign) {
        return tutorialBoxes.get(horizontalAlign).box;
    }

    protected InteractHint getInteractHint(HorizontalAlign horizontalAlign) {
        return tutorialBoxes.get(horizontalAlign).interactHint;
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
