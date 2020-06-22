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
package org.destinationsol.location.components;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.location.events.VelocityUpdateEvent;
import org.terasology.gestalt.entitysystem.component.Component;

public class Velocity implements Component<Velocity> {

    /**
     * The velocity of the entity. This is changed every tick by a {@link VelocityUpdateEvent}.
     */
    public Vector2 velocity;

    @Override
    public void copy(Velocity other) {
        this.velocity = other.velocity;
    }
}
