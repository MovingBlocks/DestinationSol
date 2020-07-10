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
package org.destinationsol.asteroids.systems;

import org.destinationsol.asteroids.components.Asteroid;
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.force.events.ImpulseEvent;
import org.destinationsol.health.components.Health;
import org.destinationsol.health.events.DamageEvent;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

public class AsteroidImpulseHandler implements EventReceiver {

    private static final float DUR = .5f;

    @In
    private EntitySystemManager entitySystemManager;

    @ReceiveEvent(components = {Asteroid.class, Health.class, BodyLinked.class})
    public EventResult onImpulse(ImpulseEvent event, EntityRef entity){

        //TODO get the mass from the body
        float mass = 1;

        float damage = event.getMagnitude() / mass / DUR;
        entitySystemManager.sendEvent(new DamageEvent((int) damage), entity);

        return EventResult.CONTINUE;
    }
}
