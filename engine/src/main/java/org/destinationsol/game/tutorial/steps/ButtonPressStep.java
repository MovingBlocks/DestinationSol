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

package org.destinationsol.game.tutorial.steps;

import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

public class ButtonPressStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final UIWarnButton button;
    private final String message;
    private boolean buttonPressed;

    public ButtonPressStep(TutorialScreen tutorialScreen, UIWarnButton button, String message) {
        this.tutorialScreen = tutorialScreen;
        this.button = button;
        this.message = message;
    }

    @Override
    public void start() {
        tutorialScreen.setTutorialText(message);
        button.subscribe(button -> {
            buttonPressed = true;
        });
    }

    @Override
    public boolean checkComplete(float timeStep) {
        button.enableWarn();
        return buttonPressed;
    }
}
