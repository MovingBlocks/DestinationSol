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
package org.destinationsol.Starport.events;

import org.destinationsol.Starport.systems.InTransitUpdateHandler;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.planet.Planet;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * When an entity enters a {@link StarPort}, this event is used to send the source and destination information to the
 * {@link InTransitUpdateHandler} system.
 */
public class EnteringStarportEvent implements Event {

    private final Planet sourcePlanet;
    private final Planet destinationPlanet;

    public EnteringStarportEvent(Planet sourcePlanet, Planet destinationPlanet) {
        this.sourcePlanet = sourcePlanet;
        this.destinationPlanet = destinationPlanet;
    }

    /**
     * @return the planet that the entity entered the {@link StarPort} at
     */
    public Planet getSourcePlanet() {
        return sourcePlanet;
    }

    /**
     * @return the planet that the entity is traveling to
     */
    public Planet getDestinationPlanet() {
        return destinationPlanet;
    }
}
