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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.faction.FactionsConfigs;
import org.destinationsol.game.faction.ReputationEvent;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.projectile.Projectile;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.ResourceUrn;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The faction manager is responsible for managing all factions in the game.
 */
public class FactionManager {
    private static final Logger logger = LoggerFactory.getLogger(FactionManager.class);
    private static final ResourceUrn PLAYER_FACTION_URN = new ResourceUrn("engine:player");
    private static final ResourceUrn GENERIC_ALLY_FACTION_URN = new ResourceUrn("engine:laani");
    private static final ResourceUrn GENERIC_ENEMY_FACTION_URN = new ResourceUrn("engine:ehar");
    private final MyRayBack myRayBack;
    private final List<Faction> factions;
    private Faction playerFaction;
    private Faction genericAllyFaction;
    private Faction genericEnemyFaction;

    @Inject
    public FactionManager(FactionsConfigs factionsConfigs) {
        myRayBack = new MyRayBack();
        factions = new ArrayList<>(factionsConfigs.factionConfigs.values());
        for (Faction faction : factions) {
            if (faction.getId().equals(PLAYER_FACTION_URN)) {
                playerFaction = faction;
            } else if (faction.getId().equals(GENERIC_ALLY_FACTION_URN)) {
                genericAllyFaction = faction;
            } else if (faction.getId().equals(GENERIC_ENEMY_FACTION_URN)) {
                genericEnemyFaction = faction;
            }
        }
    }

    /**
     * Reports an event that may influence relations between two factions.
     * @param instigator the instigating faction that triggered the event.
     * @param target the faction targeted by the event.
     * @param event the event that occurred.
     * @param <T> the type of event.
     */
    public <T extends Enum<T> & ReputationEvent> void reportEvent(Faction instigator, Faction target, T event) {
        // TODO: Add support for custom event handlers.
        //       Some examples:
        //       - A pacifist faction is offended by any attacks made by a faction, regardless of the target.
        //       - A merchant faction may randomly give a free bonus when buying items.
        //       - A protective faction may dispatch a fleet to intercept the attacker if one of their ships is attacked.
        Integer targetReputationImpact = target.getReputationImpact(event);
        if (targetReputationImpact != null) {
            target.setRelation(instigator, target.getRelation(instigator) + targetReputationImpact);
        } else {
            target.setRelation(instigator, target.getRelation(instigator) + event.getDefaultReputationImpact());
        }
    }

    /**
     * Returns all known factions.
     * @return all known factions.
     */
    public Iterable<Faction> getFactions() {
        return factions;
    }

    /**
     * Returns the player's personal faction.
     * @return the player's personal faction.
     */
    public Faction getPlayerFaction() {
        return playerFaction;
    }

    /**
     * Returns the built-in generic ally faction, which is friendly with everyone.
     * @return the generic ally faction.
     */
    public Faction getGenericAllyFaction() {
        return genericAllyFaction;
    }

    /**
     * Returns the built-in generic enemy faction, which is hostile to everyone (except themselves).
     * @return the generic enemy faction.
     */
    public Faction getGenericEnemyFaction() {
        return genericEnemyFaction;
    }

    /**
     * Finds the nearest Enemy {@link SolShip} for the given ship
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
        Faction faction = pilot.getFaction();
        return getNearestEnemy(game, detectionDist, faction, ship.getPosition());
    }

    /**
     * Finds the nearest Enemy for target seeking projectiles
     *
     * @param game       the game object
     * @param projectile the target seeking projectile
     * @return the nearest Enemy ship
     */
    public SolShip getNearestEnemy(SolGame game, Projectile projectile) {
        return getNearestEnemy(game, game.getCam().getViewDistance(), projectile.getFaction(), projectile.getPosition());
    }

    /**
     * Finds the nearest Enemy {@link SolShip}
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
        List<SolObject> objects = game.getObjectManager().getObjects();
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

    /**
     * Returns a faction capable of constructing the given hull.
     * @param hull the hull to be constructed.
     * @return a faction capable of constructing the given hull, if found, otherwise null.
     */
    public Faction getBuilderForHull(HullConfig hull) {
        for (Faction faction : factions) {
            if (faction.getShipDesigns().contains(new ResourceUrn(hull.getInternalName()))) {
                return faction;
            }
        }
        logger.error("Failed to find faction that produces hull: {}", hull.getInternalName());
        return null;
    }

    private boolean hasObstacles(SolGame game, SolShip shipFrom, SolShip shipTo) {
        myRayBack.shipFrom = shipFrom;
        myRayBack.shipTo = shipTo;
        myRayBack.hasObstacle = false;
        game.getObjectManager().getWorld().rayCast(myRayBack, shipFrom.getPosition(), shipTo.getPosition());
        return myRayBack.hasObstacle;
    }

    /**
     * Specifies whether two ships are enemies to each other.
     * @param s1 the first ship.
     * @param s2 the second ship.
     * @return true, if s1 and s1 are enemies, otherwise false.
     */
    public boolean areEnemies(SolShip s1, SolShip s2) {
        Faction f1 = s1.getPilot().getFaction();
        Faction f2 = s2.getPilot().getFaction();
        return areEnemies(f1, f2);
    }

    /**
     * Specifies whether two factions are enemies of each other.
     * @param f1 the first faction.
     * @param f2 the second faction.
     * @return true, if f1 and f2 are enemies, otherwise false.
     */
    public boolean areEnemies(Faction f1, Faction f2) {
        return f1 != null && f2 != null && f1.getRelation(f2) < 0;
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
