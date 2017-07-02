/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.projectile.Projectile;
import org.destinationsol.game.ship.SolShip;

import java.util.List;

public class FactionManager {

    private final MyRayBack myRayBack;

    public FactionManager() {
        myRayBack = new MyRayBack();
    }

    /**
     * Finds the nearest Enemy @{link SolShip} for the given ship
     *
     * @param game the game object
     * @param ship the ship to find enemies for
     * @return the nearest Enemy ship
     */
    public SolShip getNearestEnemy(SolGame game, SolShip ship) {
        Pilot pilot = ship.getPilot();
        float detectionDist = pilot.getDetectionDist();
        if (detectionDist <= 0) {
            return null;
        }
        detectionDist += ship.getHull().config.getApproxRadius();
        Faction f = pilot.getFaction();
        return getNearestEnemy(game, detectionDist, f, ship.getPosition());
    }

    /**
     * Finds the nearest Enemy for target seeking projectiles
     *
     * @param game       the game object
     * @param projectile the target seeking projectile
     * @return the nearest Enemy ship
     */
    public SolShip getNearestEnemy(SolGame game, Projectile projectile) {
        return getNearestEnemy(game, game.getCam().getViewDist(), projectile.getFaction(), projectile.getPosition());
    }

    /**
     * Finds the nearest Enemy @{link SolShip}
     *
     * @param game          the game object
     * @param detectionDist the maximum distance allowed for detection
     * @param faction       the faction of the entity
     * @param position      the position of the entity
     * @return the nearest Enemy ship
     */
    public SolShip getNearestEnemy(SolGame game, float detectionDist, Faction faction, Vector2 position) {
        SolShip nearestEnemyShip = null;
        float minimumDistance = detectionDist;
        List<SolObject> objects = game.getObjMan().getObjs();
        for (SolObject solObject : objects) {
            if (!(solObject instanceof SolShip)) {
                continue;
            }
            SolShip potentialEnemyShip = (SolShip) solObject;
            if (!areEnemies(faction, potentialEnemyShip.getPilot().getFaction())) {
                continue;
            }
            float distance = potentialEnemyShip.getPosition().dst(position) - potentialEnemyShip.getHull().config.getApproxRadius();
            if (minimumDistance < distance) {
                continue;
            }
            minimumDistance = distance;
            nearestEnemyShip = potentialEnemyShip;
        }
        return nearestEnemyShip;
    }

    private boolean hasObstacles(SolGame game, SolShip shipFrom, SolShip shipTo) {
        myRayBack.shipFrom = shipFrom;
        myRayBack.shipTo = shipTo;
        myRayBack.hasObstacle = false;
        game.getObjMan().getWorld().rayCast(myRayBack, shipFrom.getPosition(), shipTo.getPosition());
        return myRayBack.hasObstacle;
    }

    public boolean areEnemies(SolShip s1, SolShip s2) {
        Faction f1 = s1.getPilot().getFaction();
        Faction f2 = s2.getPilot().getFaction();
        return areEnemies(f1, f2);
    }

    public boolean areEnemies(Faction f1, Faction f2) {
        return f1 != null && f2 != null && f1 != f2;
    }

    private static class MyRayBack implements RayCastCallback {
        public SolShip shipFrom;
        public SolShip shipTo;
        public boolean hasObstacle;

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            SolObject o = (SolObject) fixture.getBody().getUserData();
            if (o == shipFrom || o == shipTo) {
                return -1;
            }
            hasObstacle = true;
            return 0;
        }
    }
}
