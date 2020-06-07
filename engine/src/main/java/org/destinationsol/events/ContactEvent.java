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
 * Event that represents the contact between two entities. This event can be used to create an {@link ImpulseEvent}
 * if an impulse should be applied because of the contact. Long-term forces, such as gravity, should be handled by a
 * {@link ForceEvent}.
 */
public class ContactEvent implements Event {

    /**
     * The entity causing the contact event.
     */
    private EntityRef triggeringEntity;

    /**
     * The position where the contact happened.
     */
    private Vector2 contactPosition;

    /**
     * The impulse applied to the other entity.
     */
    private float impulse;

    public ContactEvent(EntityRef triggeringEntity, Vector2 contactPosition, float impulse) {
        this.triggeringEntity = triggeringEntity;
        this.contactPosition = contactPosition;
        this.impulse = impulse;
    }

    public EntityRef getTriggeringEntity() {
        return triggeringEntity;
    }

    public Vector2 getContactPosition() {
        return contactPosition;
    }

    public float getImpulse() {
        return impulse;
    }
}
