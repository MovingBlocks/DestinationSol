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
import org.destinationsol.game.SolObject;
import org.destinationsol.game.asteroid.Asteroid;
import org.destinationsol.game.tutorial.steps.wrapper.TrackedSolObjectWrapper;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the player destroys a spawned asteroid.
 * The asteroid is spawned somewhere around the player ship at the start of this step.
 */
public class DestroySpawnedAsteroidAroundHeroStep extends DestroyObjectsStep {
    private final float minDistance;
    private final float spawnRadius;

    @Inject
    protected DestroySpawnedAsteroidAroundHeroStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public DestroySpawnedAsteroidAroundHeroStep(float minDistance, float spawnRadius, String message) {
        super(new SolObject[1], message);
        this.minDistance = minDistance;
        this.spawnRadius = spawnRadius;
    }
    @Override
    public void start() {
        Hero hero = game.getHero();
        Vector2 asteroidPosition = hero.getPosition().cpy();
        while (!game.isPlaceEmpty(asteroidPosition, true)) {
            asteroidPosition.set(
                    hero.getPosition().x + SolRandom.randomFloat(minDistance, minDistance + spawnRadius),
                    hero.getPosition().y + SolRandom.randomFloat(minDistance, minDistance + spawnRadius)
            );
        }

        Asteroid asteroid = game.getAsteroidBuilder().buildNew(game, asteroidPosition, Vector2.Zero, 1.0f, null);
        objects[0] = new TrackedSolObjectWrapper(asteroid);
        game.getObjectManager().addObjDelayed(asteroid);

        super.start();
    }

}
