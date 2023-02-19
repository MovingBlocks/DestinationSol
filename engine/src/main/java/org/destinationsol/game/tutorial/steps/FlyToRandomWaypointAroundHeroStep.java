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
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class FlyToRandomWaypointAroundHeroStep extends FlyToWaypointStep {
    private final float minDistance;
    private final float radius;

    public FlyToRandomWaypointAroundHeroStep(TutorialScreen tutorialScreen, SolGame game,
                                             float minDistance, float radius, String message) {
        super(tutorialScreen, game, Vector2.Zero, message);
        this.minDistance = minDistance;
        this.radius = radius;
    }

    @Override
    public void start() {
        Hero hero = game.getHero();
        waypointPosition = hero.getPosition().cpy();
        while (!game.isPlaceEmpty(waypointPosition, true)) {
            waypointPosition.set(
                    hero.getPosition().x + SolRandom.randomFloat(minDistance, minDistance + radius),
                    hero.getPosition().y + SolRandom.randomFloat(minDistance, minDistance + radius)
            );
        }

        super.start();
    }
}
