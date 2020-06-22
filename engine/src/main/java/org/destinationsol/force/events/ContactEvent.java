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

import com.badlogic.gdx.physics.box2d.Contact;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.ship.SolShip;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that represents the contact between two entities. Both entities involved in the contact will be sent separate
 * contact events. If the contact should be modified in any way, the {@link Contact} should be changed. For example, if
 * a {@link SolShip} comes in contact with {@link Loot}, the contact should handled without generating an impulse.
 * <p>
 * Long-term forces, such as gravity, should be handled by a {@link ForceEvent}.
 */
public class ContactEvent implements Event {

    private EntityRef otherEntity;
    private Contact contact;

    public ContactEvent(EntityRef otherEntity, Contact contact) {
        this.otherEntity = otherEntity;
        this.contact = contact;
    }

    /**
     * The other entity involved in the contact.
     */
    public EntityRef getOtherEntity() {
        return otherEntity;
    }

    /**
     * Returns the {@link Contact} from the physics engine, which contains the information about the contact. This
     * should be modified if any aspect of the contact should be changed before it is processed by the physics engine.
     */
    public Contact getContact() {
        return contact;
    }
}
