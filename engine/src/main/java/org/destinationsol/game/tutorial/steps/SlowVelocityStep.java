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

import javax.inject.Inject;

public class SlowVelocityStep extends TutorialStep {
    private static final float MAX_ACCELERATION = 0.8f;
    @Inject
    protected SolGame game;
    private final float threshold;
    private final String message;
    private float lastSpeed;

    @Inject
    protected SlowVelocityStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public SlowVelocityStep(float threshold, String message) {
        this.threshold = threshold;
        this.message = message;
    }

    @Override
    public void start() {
        setTutorialText(message);
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
