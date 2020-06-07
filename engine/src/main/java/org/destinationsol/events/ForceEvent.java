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
package org.destinationsol.events;

import com.badlogic.gdx.math.Vector2;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that represents a prolonged force applied to an entity, such as the impact of a collision. This does NOT
 *  * represent anything that is a sudden, short force, such as a collision. That type of occurrence is handled by
 *  * {@link ImpulseEvent}.
 */
public class ForceEvent implements Event {

    /**
     * The force applied to the entity.
     */
    private Vector2 force;

    /**
     * Whether the force applied causes acceleration.
     */
    private boolean causesAcceleration;

    public ForceEvent(Vector2 force, boolean causesAcceleration) {
        this.force = force;
        this.causesAcceleration = causesAcceleration;
    }

    public Vector2 getForce() {
        return force;
    }

    public boolean causesAcceleration() {
        return causesAcceleration;
    }
}
