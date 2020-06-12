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
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.ship.SolShip;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that represents the contact between two entities. Both entities involved in the contact will be sent separate
 * contact events. If an entity should be moved by the contact, its contact handling system should create an
 * {@link ImpulseEvent}. In some cases, the contact should handled without generating an impulse event, such as a
 * {@link SolShip} coming in contact with {@link Loot}.
 * <p>
 * Long-term forces, such as gravity, should be handled by a {@link ForceEvent}.
 */
public class ContactEvent implements Event {

    private EntityRef otherEntity;
    private Vector2 contactPosition;
    private float absoluteImpulse;

    public ContactEvent(EntityRef otherEntity, Vector2 contactPosition, float absoluteImpulse) {
        this.otherEntity = otherEntity;
        this.contactPosition = contactPosition;
        this.absoluteImpulse = absoluteImpulse;
    }

    /**
     * The other entity involved in the contact.
     */
    public EntityRef getOtherEntity() {
        return otherEntity;
    }

    /**
     * The position where the contact happened.
     */
    public Vector2 getContactPosition() {
        return contactPosition;
    }

    /**
     * The impulse applied to the entity.
     */
    public float getAbsoluteImpulse() {
        return absoluteImpulse;
    }
}
