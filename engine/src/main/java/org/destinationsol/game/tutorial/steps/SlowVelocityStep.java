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

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class SlowVelocityStep extends TutorialStep {
    private static final float MAX_ACCELERATION = 0.8f;
    private final TutorialScreen tutorialScreen;
    private final SolGame game;
    private final float threshold;
    private final String message;
    private float lastSpeed;

    public SlowVelocityStep(TutorialScreen tutorialScreen, SolGame game, float threshold, String message) {
        this.tutorialScreen = tutorialScreen;
        this.game = game;
        this.threshold = threshold;
        this.message = message;
    }

    @Override
    public void start() {
        tutorialScreen.setTutorialText(message);
        lastSpeed = game.getHero().getVelocity().len();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        float speed = hero.getVelocity().len();
        float acceleration = (speed - lastSpeed)/timeStep;
        return acceleration < MAX_ACCELERATION && speed < threshold;
    }
}
