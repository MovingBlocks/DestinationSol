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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.terasology.input.MouseInput;

public class MapDragStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final MapDrawer mapDrawer;
    private final String message;
    private Vector2 originalMapPosition;

    public MapDragStep(TutorialScreen tutorialScreen, MapDrawer mapDrawer, String message) {
        this.tutorialScreen = tutorialScreen;
        this.mapDrawer = mapDrawer;
        this.message = message;
    }

    @Override
    public void start() {
        tutorialScreen.setTutorialText(message);
        tutorialScreen.setInteractHintInput(MouseInput.MOUSE_LEFT);
        originalMapPosition = mapDrawer.getMapDrawPositionAdditive().cpy();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        return !mapDrawer.getMapDrawPositionAdditive().equals(originalMapPosition);
    }
}
