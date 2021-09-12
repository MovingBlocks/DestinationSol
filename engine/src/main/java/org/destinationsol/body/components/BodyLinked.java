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
package org.destinationsol.body.components;

import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.body.systems.BodyHandlerSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.entitysystem.component.Component;


/**
 * Indicates that there is a {@link Body} associated with the entity. It also contains the mass of the entity.
 */
public class BodyLinked implements Component<BodyLinked> {
    private static final Logger logger = LoggerFactory.getLogger(BodyLinked.class);

    private float mass;

    /**
     * Sets the mass of the entity. This is called every tick by the {@link BodyHandlerSystem}.
     */
    public void setMass(float mass) {
        if (mass <= 0) {
            logger.error("Invalid value for the mass. It can't be less than or equal to zero.");
            this.mass = 1;
        } else {
            this.mass = mass;
        }
    }

    public float getMass() {
        return mass;
    }

    @Override
    public void copyFrom(BodyLinked other) {
        this.mass = other.mass;
    }
}
