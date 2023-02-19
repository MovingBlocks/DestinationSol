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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.ui.nui.screens.TutorialScreen;

import java.util.ArrayList;
import java.util.List;

public class FlyToPlanetSellingMercenariesStep extends FlyToPlanetStep {
    public FlyToPlanetSellingMercenariesStep(TutorialScreen tutorialScreen, SolGame game, String message) {
        super(tutorialScreen, game, null, message);
    }

    @Override
    public void start() {
        List<Planet> planetsWithMercenaries = new ArrayList<>();

        Vector2 heroPosition = game.getHero().getPosition();
        for (Planet planet : game.getPlanetManager().getNearestSystem(heroPosition).getPlanets()) {
            if (planet.getConfig().tradeConfig.mercs.groupCount() > 0) {
                planetsWithMercenaries.add(planet);
            }
        }

        if (planetsWithMercenaries.size() == 0) {
            tutorialScreen.setTutorialText("ERROR: Failed to find suitable planet.");
            return;
        }

        Planet closestPlanet = planetsWithMercenaries.get(0);
        float closestDistance = Float.MAX_VALUE;

        for (Planet planet : planetsWithMercenaries) {
            float distance = planet.getPosition().dst(heroPosition);
            if (distance < closestDistance) {
                closestPlanet = planet;
                closestDistance = distance;
            }
        }

        planet = closestPlanet;
        super.start();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        boolean nearPlanet = super.checkComplete(timeStep);
        if (nearPlanet && planet.areObjectsCreated()) {
            return game.getScreens().talkScreen.isTargetFar(game.getHero());
        }
        return false;
    }
}
