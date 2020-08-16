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
package org.destinationsol.force.components;

import org.destinationsol.force.events.ImpulseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Contains the durability of on entity, used for calculating how much damage the entity takes when receiving an
 * {@link ImpulseEvent}. A durability value greater than one means that the entity takes less damage, and a durability
 * between zero and one means more damage.
 */
public class Durability implements Component<Durability> {

    private static final Logger logger = LoggerFactory.getLogger(Durability.class);

    private float durability;

    public float getDurability() {
        return durability;
    }

    /**
     * This sets the durability of an entity. If the value passed in is less than or equal to zero, the durability
     * defaults to be one.
     */
    public void setDurability(float durability) {
        if (durability <= 0) {
            logger.error("Invalid value for the durability. It can't be less than or equal to zero.");
            this.durability = 1;
        }
        this.durability = durability;
    }

    @Override
    public void copy(Durability other) {
        this.durability = other.durability;
    }
}
