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
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Guardian;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.game.tutorial.steps.wrapper.TrackedSolObjectWrapper;

import javax.inject.Inject;

/**
 * A tutorial step that completes when all spawned ships have been destroyed. If the player dies, they are prompted to respawn.
 * The spawned ships are marked by waypoints, which guide the player towards them.
 * All ships are spawned at the start of the ships from a specified hull configuration.
 */
public class DestroySpawnedShipsStep extends DestroyObjectsStep {
    private final int shipCount;
    private final String hullConfig;
    private final String items;
    private final String respawnMessage;

    @Inject
    protected DestroySpawnedShipsStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public DestroySpawnedShipsStep(int shipCount, String hullConfig, String items, String attackMessage, String respawnMessage) {
        super(new SolObject[shipCount], attackMessage);
        this.shipCount = shipCount;
        this.hullConfig = hullConfig;
        this.items = items;
        this.respawnMessage = respawnMessage;
    }

    @Override
    public void start() {
        Hero hero = game.getHero();

        HullConfig enemyConfig = game.getHullConfigManager().getConfig(hullConfig);
        for (int enemyNo = 0; enemyNo < shipCount; enemyNo++) {
            Vector2 enemyPosition = hero.getPosition().cpy();

            Planet nearestPlanet = game.getPlanetManager().getNearestPlanet(hero.getPosition());
            if (nearestPlanet.isNearGround(hero.getPosition())) {
                Vector2 enemyOffset = SolMath.fromAl(game.getCam().getAngle() + SolRandom.randomFloat(-8, 8), SolRandom.randomFloat(4, 12));
                enemyPosition.add(enemyOffset);
                SolMath.free(enemyOffset);
                while (!game.isPlaceEmpty(enemyPosition, false)) {
                    enemyPosition.set(hero.getPosition());
                    enemyOffset = SolMath.fromAl(game.getCam().getAngle() + SolRandom.randomFloat(-8, 8), SolRandom.randomFloat(8, 20));
                    enemyPosition.add(enemyOffset);
                    SolMath.free(enemyOffset);
                }
            } else {
                while (!game.isPlaceEmpty(enemyPosition, false)) {
                    enemyPosition.set(hero.getPosition().x + SolRandom.randomFloat(2, 10), hero.getPosition().y + SolRandom.randomFloat(2, 10));
                }
            }

            Guardian dp = new Guardian(game, enemyConfig, hero.getPilot(), hero.getPosition(), hero.getHull().getHullConfig(), 0);
            Pilot pilot = new AiPilot(dp, true, game.getFactionMan().getGenericEnemyFaction(), false, null, Const.AI_DET_DIST);
            int money = 60;
            FarShip enemy = game.getShipBuilder().buildNewFar(game, enemyPosition, null,
                    0, 0, pilot, items,
                    enemyConfig, null, false, money, null, true);
            TrackedSolObjectWrapper enemyShip = new TrackedSolObjectWrapper(enemy.toObject(game));
            game.getObjectManager().addObjDelayed(enemyShip);
            objects[enemyNo] = enemyShip;
        }

        super.start();
    }

    @Override
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        if (hero.isDead()) {
            setTutorialText(respawnMessage);
        } else {
            setTutorialText(message);
        }

        return super.checkComplete(timeStep);
    }
}
