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
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Event that represents the magnitude of a sudden, instantaneous force applied to an entity, like the impact of a
 * collision. This is only used for calculating damage. This does NOT handle the movement caused - that is handled
 * internally by libGDX, and can be modified by a system altering the {@link Contact} in a {@link ContactEvent}.
 * <p>
 * This does not represent anything that is an application of continuous force, like gravity. That type of occurrence
 * is handled by {@link ForceEvent}.
 */
public class ImpulseEvent {

    private Vector2 contactPosition;
    private float magnitude;

    public ImpulseEvent(Vector2 contactPosition, float magnitude) {
        this.contactPosition = contactPosition;
        this.magnitude = magnitude;
    }

    /**
     * The position where the contact happened.
     */
    public Vector2 getContactPosition() {
        return contactPosition;
    }

    /**
     * The magnitude of the impulse that was applied to the entity (used for calculating damage).
     */
    public float getMagnitude() {
        return magnitude;
    }

}
