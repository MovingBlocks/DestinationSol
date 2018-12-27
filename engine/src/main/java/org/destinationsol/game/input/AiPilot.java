/*
 * Copyright 2018 MovingBlocks
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

package org.destinationsol.game.input;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetBind;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.HashMap;
import java.util.Map;

public class AiPilot implements Pilot {

    public static final float MIN_IDLE_DIST = .8f;
    public static final float MAX_GROUND_BATTLE_SPD = .7f;
    public static final float MAX_BATTLE_SPD_BIG = 1f;
    public static final float MAX_BATTLE_SPD = 2f;
    public static final float MAX_BIND_AWAIT = .25f;
    public static final float MAX_RE_EQUIP_AWAIT = 3f;

    private final MoveDestProvider myDestProvider;
    private final boolean myCollectsItems;
    private final Mover myMover;
    private final Shooter myShooter;
    private Faction myFaction;
    private final boolean myShootAtObstacles;
    private final String myMapHint;
    private final BattleDestProvider myBattleDestProvider;
    private final float myDetectionDist;
    private final AbilityUpdater myAbilityUpdater;

    private float myBindAwait;
    private PlanetBind myPlanetBind;
    private float myReEquipAwait;

    public AiPilot(MoveDestProvider destProvider, boolean collectsItems, Faction faction,
                   boolean shootAtObstacles, String mapHint, float detectionDist) {
        myDestProvider = destProvider;
        myDetectionDist = detectionDist;
        myMover = new Mover();
        myShooter = new Shooter();
        myBattleDestProvider = new BattleDestProvider();
        myCollectsItems = collectsItems;
        myFaction = faction;
        myShootAtObstacles = shootAtObstacles;
        myMapHint = mapHint;
        myAbilityUpdater = new AbilityUpdater();
    }

    @Override
    public void update(SolGame game, SolShip ship, SolShip nearestEnemy) {
        myAbilityUpdater.update(ship, nearestEnemy);
        myPlanetBind = null;
        Vector2 shipPos = ship.getPosition();
        HullConfig hullConfig = ship.getHull().config;
        float maxIdleDist = getMaxIdleDist(hullConfig);
        myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, nearestEnemy);

        Boolean canShoot = canShoot0(ship);
        boolean canShootUnfixed = canShoot == null;
        if (canShootUnfixed) {
            canShoot = true;
        }
        Planet np = game.getPlanetManager().getNearestPlanet();
        boolean nearGround = np.isNearGround(shipPos);

        Vector2 dest = null;
        Vector2 destVelocity = null;
        boolean shouldStopNearDest = false;
        boolean avoidBigObjs = false;
        float desiredSpeed = myDestProvider.getDesiredSpeed();
        boolean hasEngine = ship.getHull().getEngine() != null;
        if (hasEngine) {
            Boolean battle = null;
            if (nearestEnemy != null) {
                battle = myDestProvider.shouldManeuver(canShoot, nearestEnemy, nearGround);
            }
            if (battle != null) {
                dest = myBattleDestProvider.getDest(ship, nearestEnemy, np, battle, game.getTimeStep(), canShootUnfixed, nearGround);
                shouldStopNearDest = myBattleDestProvider.shouldStopNearDest();
                destVelocity = nearestEnemy.getVelocity();
                boolean big = hullConfig.getType() == HullConfig.Type.BIG;
                float maxBattleSpeed = nearGround ? MAX_GROUND_BATTLE_SPD : big ? MAX_BATTLE_SPD_BIG : MAX_BATTLE_SPD;
                if (maxBattleSpeed < desiredSpeed) {
                    desiredSpeed = maxBattleSpeed;
                }
                if (!big) {
                    desiredSpeed += destVelocity.len();
                }
            } else {
                dest = myDestProvider.getDestination();
                destVelocity = myDestProvider.getDestinationVelocity();
                shouldStopNearDest = myDestProvider.shouldStopNearDestination();
                avoidBigObjs = myDestProvider.shouldAvoidBigObjects();
            }
        }

        myMover.update(game, ship, dest, np, maxIdleDist, hasEngine, avoidBigObjs, desiredSpeed, shouldStopNearDest, destVelocity);
        boolean moverActive = myMover.isActive();

        Vector2 enemyPos = nearestEnemy == null ? null : nearestEnemy.getPosition();
        Vector2 enemyVelocity = nearestEnemy == null ? null : nearestEnemy.getVelocity();
        float enemyApproxRad = nearestEnemy == null ? 0 : nearestEnemy.getHull().config.getApproxRadius();
        myShooter.update(ship, enemyPos, moverActive, canShoot, enemyVelocity, enemyApproxRad);
        if (hasEngine && !moverActive && !isShooterRotated()) {
            myMover.rotateOnIdle(ship, np, dest, shouldStopNearDest, maxIdleDist);
        }

        if (myReEquipAwait <= 0) {
            myReEquipAwait = MAX_RE_EQUIP_AWAIT;
        } else {
            myReEquipAwait -= game.getTimeStep();
        }
    }

    private float getMaxIdleDist(HullConfig hullConfig) {
        float maxIdleDist = hullConfig.getApproxRadius();
        if (maxIdleDist < MIN_IDLE_DIST) {
            maxIdleDist = MIN_IDLE_DIST;
        }
        return maxIdleDist;
    }

    private Boolean canShoot0(SolShip ship) {
        Gun g1 = ship.getHull().getGun(false);
        if (g1 != null && g1.canShoot()) {
            return !g1.config.fixed ? null : true;
        }
        Gun g2 = ship.getHull().getGun(true);
        if (g2 != null && (g2.canShoot())) {
            return !g2.config.fixed ? null : true;
        }
        return false;
    }

    private boolean isShooterRotated() {
        return myShooter.isLeft() || myShooter.isRight();
    }

    @Override
    public boolean isUp() {
        return myMover.isUp();
    }

    @Override
    public boolean isLeft() {
        return myMover.isLeft() || myShooter.isLeft();
    }

    @Override
    public boolean isRight() {
        return myMover.isRight() || myShooter.isRight();
    }

    @Override
    public boolean isShoot() {
        return myShooter.isShoot();
    }

    @Override
    public boolean isShoot2() {
        return myShooter.isShoot2();
    }

    @Override
    public boolean collectsItems() {
        return myCollectsItems;
    }

    @Override
    public boolean isAbility() {
        return myAbilityUpdater.isAbility();
    }

    @Override
    public Faction getFaction() {
        return myFaction;
    }

    @Override
    public void stringToFaction(String faction) {
        Map<String, Faction> factionMap = new HashMap<>();
        if (faction.equals("laani")) {
            factionMap.put(faction, Faction.LAANI);
        }
        if (faction.equals("ehar")) {
            factionMap.put(faction, Faction.EHAR);
        }
        myFaction = factionMap.get(faction);
    }

    @Override
    public boolean shootsAtObstacles() {
        return myShootAtObstacles;
    }

    @Override
    public float getDetectionDist() {
        return myDetectionDist;
    }

    @Override
    public String getMapHint() {
        return myMapHint;
    }

    @Override
    public void updateFar(SolGame game, FarShip farShip) {
        Vector2 shipPos = farShip.getPosition();
        HullConfig hullConfig = farShip.getHullConfig();
        float maxIdleDist = getMaxIdleDist(hullConfig);
        myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, null);
        Vector2 dest = myDestProvider.getDestination();

        Vector2 velocity = farShip.getVelocity();
        float angle = farShip.getAngle();
        Engine engine = farShip.getEngine();
        float ts = game.getTimeStep();
        if (dest == null || engine == null) {
            if (myPlanetBind == null) {
                if (myBindAwait > 0) {
                    myBindAwait -= ts;
                } else {
                    myPlanetBind = PlanetBind.tryBind(game, shipPos, angle);
                    myBindAwait = MAX_BIND_AWAIT;
                }
            }
            if (myPlanetBind != null) {
                myPlanetBind.setDiff(velocity, shipPos, false);
                velocity.scl(1 / ts);
                angle = myPlanetBind.getDesiredAngle();
            }
        } else {
            float toDestLen = shipPos.dst(dest);
            float desiredAngle;
            float maxIdleDistHack = .05f; // to avoid StillGuards from getting stuck inside ground
            if (myDestProvider.shouldStopNearDestination() && toDestLen < maxIdleDistHack) {
                velocity.set(myDestProvider.getDestinationVelocity());
                desiredAngle = angle; // can be improved
            } else {
                desiredAngle = SolMath.angle(shipPos, dest);
                if (myDestProvider.shouldAvoidBigObjects()) {
                    desiredAngle = myMover.getBigObjAvoider().avoid(game, shipPos, dest, desiredAngle);
                }
                float desiredSpeed = myDestProvider.getDesiredSpeed();
                float speedDiff = engine.getAcceleration() * ts;
                float speed = SolMath.approach(velocity.len(), desiredSpeed, speedDiff);
                if (toDestLen < speed) {
                    speed = toDestLen;
                }
                SolMath.fromAl(velocity, desiredAngle, speed);
            }
            angle = SolMath.approachAngle(angle, desiredAngle, engine.getMaxRotationSpeed() * ts);
        }

        farShip.setVelocity(velocity);
        farShip.setAngle(angle);

        Vector2 newPos = SolMath.getVec(velocity);
        newPos.scl(ts);
        newPos.add(shipPos);
        farShip.setPos(newPos);
        SolMath.free(newPos);
    }

    @Override
    public String toDebugString() {
        return "moverActive: " + myMover.isActive();
    }

    @Override
    public boolean isPlayer() {
        return myDestProvider instanceof BeaconDestProvider;
    }

}
