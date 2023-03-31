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

import org.destinationsol.common.Nullable;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.UIWarnButton;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the specified screen is closed. It hints at a button that can be used to do this.
 */
public class CloseScreenStep extends TutorialStep {
    @Inject
    protected NUIManager nuiManager;
    private final UIWarnButton closeButton;
    private final NUIScreenLayer screen;
    private final String message;

    @Inject
    protected CloseScreenStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public CloseScreenStep(@Nullable UIWarnButton closeButton,
                           NUIScreenLayer screen, String message) {
        this.closeButton = closeButton;
        this.screen = screen;
        this.message = message;
    }

    @Override
    public void start() {
        if (closeButton != null) {
            closeButton.setVisible(true);
        }
        setTutorialText(message);
    }

    @Override
    public boolean checkComplete(float timeStep) {
        if (closeButton != null) {
            closeButton.enableWarn();
        }
        return !nuiManager.hasScreen(screen);
    }
}
