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

public abstract class TutorialStep {
    private String tutorialText;
    private HorizontalAlign tutorialBoxPosition = HorizontalAlign.CENTER;
    private Input requiredInput;
    private Consumer<Input> inputHandler;

    public abstract void start();
    public abstract boolean checkComplete(float timeStep);

    public String getTutorialText() {
        return tutorialText;
    }

    public HorizontalAlign getTutorialBoxPosition() {
        return tutorialBoxPosition;
    }

    public Input getRequiredInput() {
        return requiredInput;
    }

    public Consumer<Input> getInputHandler() {
        return inputHandler;
    }

    protected void setTutorialText(String tutorialText) {
        this.tutorialText = tutorialText;
    }

    public void setTutorialBoxPosition(HorizontalAlign tutorialBoxPosition) {
        this.tutorialBoxPosition = tutorialBoxPosition;
    }

    protected void setRequiredInput(Input requiredInput) {
        this.requiredInput = requiredInput;
    }

    protected void setInputHandler(Consumer<Input> inputHandler) {
        this.inputHandler = inputHandler;
    }
}
