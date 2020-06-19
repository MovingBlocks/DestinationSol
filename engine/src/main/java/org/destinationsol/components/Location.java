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
package org.destinationsol.components;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.events.LocationUpdateEvent;
import org.terasology.gestalt.entitysystem.component.Component;

public class Location implements Component<Location> {

    /**
     * The position of the entity. This is changed every tick by a {@link LocationUpdateEvent}.
     */
    public Vector2 position;

    /**
     * The angle, in degrees, of the entity. This is changed every tick by the {@link LocationUpdateEvent}.
     */
    public float angle;

    /**
     * The velocity of the entity. This is changed every tick by the {@link LocationUpdateEvent}.
     */
    public Vector2 velocity;

    @Override
    public void copy(Location other) {
        this.position = other.position;
        this.angle = other.angle;
        this.velocity = other.velocity;
    }
}
