/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.Starport.systems;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.Starport.components.InStarportTransit;
import org.destinationsol.Starport.events.EnteringStarportEvent;
import org.destinationsol.Starport.events.InTransitUpdateEvent;
import org.destinationsol.Starport.events.StarportTransitFinishedEvent;
import org.destinationsol.common.In;
import org.destinationsol.common.SolMath;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.HashMap;

public class InTransitUpdateHandler implements EventReceiver {

    private static final float ENTITY_IN_TRANSIT_MOVEMENT_SPEED = Const.MAX_MOVE_SPD * 2;

    private HashMap<EntityRef, Planet> sourcePlanetMap = new HashMap<>();
    private HashMap<EntityRef, Planet> destinationPlanetMap = new HashMap<>();

    @In
    private EntitySystemManager entitySystemManager;

    /**
     * When an entity enters a {@link StarPort}, this stores its source and destination information.
     *
     * @param event  the information about the source and destination
     * @param entity the entity entering the StarPort
     */
    @ReceiveEvent(components = InStarportTransit.class)
    public EventResult onEnteringStarport(EnteringStarportEvent event, EntityRef entity) {
        sourcePlanetMap.put(entity, event.getSourcePlanet());
        destinationPlanetMap.put(entity, event.getDestinationPlanet());
        return EventResult.CONTINUE;
    }

    /**
     * @param event
     * @param entity
     * @return
     */
    @ReceiveEvent(components = {InStarportTransit.class, Position.class, Angle.class, Velocity.class})
    public EventResult onUpdate(InTransitUpdateEvent event, EntityRef entity) {

        if (!sourcePlanetMap.containsKey(entity) || !destinationPlanetMap.containsKey(entity)) {
            return EventResult.CANCEL;
        }

        Planet sourcePlanet = sourcePlanetMap.get(entity);
        Planet destinationPlanet = destinationPlanetMap.get(entity);
        Vector2 sourcePlanetPosition = sourcePlanet.getPosition();

        Vector2 destinationStarportPosition = new Vector2();
        float trajectoryAngle = SolMath.angle(destinationPlanet.getPosition(), sourcePlanetPosition);

        //the following block calculates the location of the starport by getting the location relative to the planet,
        //then adding the position of the planet to the relative position.
        float distance = destinationPlanet.getFullHeight() + StarPort.DIST_FROM_PLANET + StarPort.SIZE / 2;
        SolMath.fromAl(destinationStarportPosition, trajectoryAngle, distance);
        destinationStarportPosition.add(destinationPlanet.getPosition());

        Angle angleComponent = entity.getComponent(Angle.class).get();
        Position positionComponent = entity.getComponent(Position.class).get();
        float angle = SolMath.angle(positionComponent.position, destinationStarportPosition);
        angleComponent.setAngle(angle);

        Velocity velocityComponent = entity.getComponent(Velocity.class).get();
        SolMath.fromAl(velocityComponent.velocity, angleComponent.getAngle(), ENTITY_IN_TRANSIT_MOVEMENT_SPEED);

        float timeStep = event.getTimeStep();
        Vector2 displacement = SolMath.getVec(velocityComponent.velocity);
        displacement.scl(timeStep);
        positionComponent.position.add(displacement);
        SolMath.free(displacement);

        entity.setComponent(positionComponent);
        entity.setComponent(angleComponent);
        entity.setComponent(velocityComponent);

        if (positionComponent.position.dst(destinationStarportPosition) < .5f) {
            entitySystemManager.sendEvent(new StarportTransitFinishedEvent(), entity);
            return EventResult.COMPLETE;
        }

        return EventResult.CONTINUE;
    }

    /**
     * After an entity travels through a {@link StarPort}, this removes the {@link InStarportTransit} component.
     */
    @ReceiveEvent(components = InStarportTransit.class)
    public EventResult onStarportTransitFinished(StarportTransitFinishedEvent event, EntityRef entity) {
        entity.removeComponent(InStarportTransit.class);
        return EventResult.CONTINUE;
    }
}
