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
package org.destinationsol.force.events;

import com.badlogic.gdx.math.Vector2;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that represents a continuous force applied to an entity, like gravity. This does NOT represent anything
 * that is a sudden, instantaneous force, like a collision. That type of occurrence is handled by {@link ImpulseEvent}.
 * This event is repeatedly sent as long as the force still affects the entity.
 * <p>
 * This event is sent every timestep for as long as the entity is being affected by the force.
 */
public class ForceEvent implements Event {

    private Vector2 force;

    public ForceEvent(Vector2 force) {
        this.force = force;
    }

    /**
     * The force applied to the entity.
     */
    public Vector2 getForce() {
        return force;
    }
}
