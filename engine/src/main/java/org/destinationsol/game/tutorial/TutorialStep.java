/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial;

import org.terasology.input.Input;
import org.terasology.nui.HorizontalAlign;

import java.util.function.Consumer;

/**
 * A tutorial step is an action (or connected sequence of actions) that demonstrates to the player a portion of the game's
 * functionality. Tutorial steps are queried for completion every frame. They should provide some explanatory text to
 * show to the player, as well as optionally an input hint. The step is allowed to decide the tutorial box it will
 * populate.
 */
public abstract class TutorialStep {
    private String tutorialHeading;
    private String tutorialText;
    private HorizontalAlign tutorialBoxPosition = HorizontalAlign.CENTER;
    private Input requiredInput;
    private Consumer<Input> inputHandler;

    /**
     * This is called only once, when the step is started. It usually occurs after the previous step has been completed.
     */
    public abstract void start();

    /**
     * This should return true if the step has been completed.
     * @param timeStep the time passed since the last query
     * @return true, if the step has been completed, otherwise false
     */
    public abstract boolean checkComplete(float timeStep);

    /**
     * Returns the heading text shown to the player. This can be null, to preserve the existing heading.
     * @return the heading text shown to the player This can be null to preserve the existing heading.
     */
    public String getTutorialHeading() {
        return tutorialHeading;
    }

    /**
     * Returns the explanatory text to be the shown to the player.
     * @return the explanatory text to be the shown to the player
     */
    public String getTutorialText() {
        return tutorialText;
    }

    /**
     * Returns the position of the text box to show the explanatory text in.
     * @return the position of the text box to show the explanatory text in.
     */
    public HorizontalAlign getTutorialBoxPosition() {
        return tutorialBoxPosition;
    }

    /**
     * Returns the desired input that should be hinted at by the tutorial box.
     * @return the desired input that should be hinted at by the tutorial box.
     */
    public Input getRequiredInput() {
        return requiredInput;
    }

    /**
     * Returns the callback to be invoked if the hinted input is performed.
     * @return the callback to be invoked if the hinted input is performed.
     */
    public Consumer<Input> getInputHandler() {
        return inputHandler;
    }

    /**
     * Specifies the heading to display above the tutorial box. This can be null to preserve the existing heading.
     * @param tutorialHeading the heading to display above the tutorial box, or null to preserve the existing heading.
     */
    protected void setTutorialHeading(String tutorialHeading) {
        this.tutorialHeading = tutorialHeading;
    }

    /**
     * Specifies the explanatory text to display in the tutorial box.
     * @param tutorialText the explanatory text to display
     */
    protected void setTutorialText(String tutorialText) {
        this.tutorialText = tutorialText;
    }

    /**
     * Specifies the position the tutorial box should use.
     * @param tutorialBoxPosition the position of the tutorial box
     */
    public void setTutorialBoxPosition(HorizontalAlign tutorialBoxPosition) {
        this.tutorialBoxPosition = tutorialBoxPosition;
    }

    /**
     * Specifies the desired input to be hinted by the tutorial box.
     * @param requiredInput the desired input to be hinted
     */
    protected void setRequiredInput(Input requiredInput) {
        this.requiredInput = requiredInput;
    }

    /**
     * Specifies the callback to the invoked in the desired input is performed.
     * @param inputHandler the callback to be invoked
     */
    protected void setInputHandler(Consumer<Input> inputHandler) {
        this.inputHandler = inputHandler;
    }
}
