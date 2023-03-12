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

import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.InteractHint;
import org.terasology.input.Input;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.widgets.UIBox;
import org.terasology.nui.widgets.UILabel;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.function.Consumer;

/**
 * This screen displays the message box shown during the tutorial to instruct the user.
 * It is unusual in that it should always be rendered on-top of all other UI screens.
 * See {@link #moveToTop()} and {@link org.destinationsol.game.tutorial.TutorialManager#update(SolGame, float)} for how this is done.
 */
public class TutorialScreen extends NUIScreenLayer {
    private static final class TutorialBox {
        public final UIBox box;
        public final UILabel text;
        public final InteractHint interactHint;
        public Consumer<Input> inputEventListener;

        public TutorialBox(UIBox box, UILabel text, InteractHint interactHint) {
            this.box = box;
            this.text = text;
            this.interactHint = interactHint;
        }
    }

    private final EnumMap<HorizontalAlign, TutorialBox> tutorialBoxes;
    private final InteractionListener mouseInputListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            for (TutorialBox tutorialBox : tutorialBoxes.values()) {
                if (tutorialBox.interactHint != null && tutorialBox.interactHint.isVisible() &&
                        tutorialBox.interactHint.getInput().equals(event.getMouseButton())) {
                    if (tutorialBox.inputEventListener != null) {
                        tutorialBox.inputEventListener.accept(event.getMouseButton());
                        return true;
                    }
                }
            }
            return super.onMouseClick(event);
        }
    };
    private final SolApplication solApplication;

    private boolean isReplaceRemove;

    @Inject
    public TutorialScreen(SolApplication solApplication) {
        tutorialBoxes = new EnumMap<>(HorizontalAlign.class);
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        // TODO: The right tutorial box doesn't exist yet. It will return null values.
        for (HorizontalAlign horizontalAlign : HorizontalAlign.values()) {
            TutorialBox tutorialBox = new TutorialBox(
                    find("tutorialBox" + horizontalAlign.toString(), UIBox.class),
                    find("tutorialText" + horizontalAlign.toString(), UILabel.class),
                    find("interactHint" + horizontalAlign.toString(), InteractHint.class)
            );
            if (tutorialBox.interactHint != null) {
                tutorialBox.interactHint.useMobileIcons(solApplication.isMobile());
            }
            tutorialBoxes.put(horizontalAlign, tutorialBox);
        }
    }

    /**
     * Returns the text displayed in the centre tutorial box.
     * @return the text displayed in the centre tutorial box
     */
    public String getTutorialText() {
        return getTutorialText(HorizontalAlign.CENTER);
    }

    /**
     * Returns the text displayed in the specified tutorial box.
     * @param horizontalAlign the tutorial box to select
     * @return the text displayed in the specified tutorial box.
     */
    public String getTutorialText(HorizontalAlign horizontalAlign) {
        return getTutorialTextLabel(horizontalAlign).getText();
    }

    /**
     * Specifies the text to be displayed in the centre tutorial box.
     * @param text the text to be displayed in the centre tutorial box
     */
    public void setTutorialText(String text) {
        setTutorialText(text, HorizontalAlign.CENTER);
    }

    /**
     * Specifies the text to be displayed in the specified tutorial box.
     * @param text the text to be displayed
     * @param horizontalAlign the tutorial box to select
     */
    public void setTutorialText(String text, HorizontalAlign horizontalAlign) {
        getTutorialTextLabel(horizontalAlign).setText(text);
        getTutorialBox(horizontalAlign).setVisible(!text.isEmpty());
    }

    /**
     * Returns the input hinted at by the centre tutorial box. This can be null.
     * @return the input hinted at by the centre tutorial box
     */
    public Input getInteractHintInput() {
        return getInteractHintInput(HorizontalAlign.CENTER);
    }

    /**
     * Returns the input hinted at by the specified tutorial box. This can be null.
     * @param horizontalAlign the tutorial box to select
     * @return the input hinted at by the specified tutorial box
     */
    public Input getInteractHintInput(HorizontalAlign horizontalAlign) {
        return getInteractHint(horizontalAlign).getInput();
    }

    /**
     * Specifies the input hinted at by the centre tutorial box.
     * @param input the input hinted at by the centre tutorial box
     */
    public void setInteractHintInput(Input input) {
        setInteractHintInput(HorizontalAlign.CENTER, input);
    }

    /**
     * Specifies the input hinted at by the specified tutorial box.
     * @param horizontalAlign the tutorial box to select
     * @param input the input to hint at
     */
    public void setInteractHintInput(HorizontalAlign horizontalAlign, Input input) {
        InteractHint interactHint = getInteractHint(horizontalAlign);
        if (input != null) {
            getInteractHint(horizontalAlign).setInput(input);
            interactHint.setVisible(true);
        } else {
            interactHint.setVisible(false);
        }
    }

    /**
     * Specifies a callback involved when the hinted-at input is activated.
     * @param interactEvent the interaction callback
     */
    public void setInteractEvent(Consumer<Input> interactEvent) {
        setInteractEvent(HorizontalAlign.CENTER, interactEvent);
    }

    /**
     * Specifies a callback involved when the hinted-at input is activated.
     * @param horizontalAlign the tutorial box to select
     * @param interactEvent the interaction callback
     */
    public void setInteractEvent(HorizontalAlign horizontalAlign, Consumer<Input> interactEvent) {
        tutorialBoxes.get(horizontalAlign).inputEventListener = interactEvent;
    }

    /**
     * This clears the contents of all the tutorial boxes and hides them.
     */
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
            tutorialBox.inputEventListener = null;
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

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        for (TutorialBox tutorialBox : tutorialBoxes.values()) {
            if (tutorialBox.interactHint != null && tutorialBox.interactHint.isVisible() &&
                    tutorialBox.interactHint.getInput().equals(event.getKey())) {
                if (!event.isDown() && tutorialBox.inputEventListener != null) {
                    tutorialBox.inputEventListener.accept(event.getKey());
                }
                return true;
            }
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        InteractHint leftInteractHint = getInteractHint(HorizontalAlign.LEFT);
        InteractHint centreInteractHint = getInteractHint(HorizontalAlign.CENTER);
        InteractHint rightInteractHint = getInteractHint(HorizontalAlign.RIGHT);
        if ((leftInteractHint != null && leftInteractHint.isVisible()) ||
                (centreInteractHint != null && centreInteractHint.isVisible()) ||
                (rightInteractHint != null && rightInteractHint.isVisible())) {
            canvas.addInteractionRegion(mouseInputListener, canvas.getRegion());
        }
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
