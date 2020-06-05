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
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that represents the contact between two entities. This event can be used to create a force event, if force
 * should be applied because of the contact.
 */
public class ContactEvent implements Event {

    /**
     * The entity causing the contact event.
     */
    private EntityRef triggeringEntity;

    /**
     * The position that the contact happened at.
     */
    private Vector2 contactPosition;


    /**
     * The force applied to the other entity.
     */
    private Vector2 force;

    /**
     * Whether the force applied causes acceleration.
     */
    private boolean causesAcceleration;

    public ContactEvent(EntityRef triggeringEntity, Vector2 force, Vector2 contactPosition, boolean causesAcceleration) {
        this.triggeringEntity = triggeringEntity;
        this.force = force;
        this.contactPosition = contactPosition;
        this.causesAcceleration = causesAcceleration;
    }

    public EntityRef getTriggeringEntity() {
        return triggeringEntity;
    }

    public Vector2 getContactPosition() {
        return contactPosition;
    }

    public Vector2 getForce() {
        return force;
    }

    public boolean causesAcceleration() {
        return causesAcceleration;
    }
}
