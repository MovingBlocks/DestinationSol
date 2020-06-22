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
package org.destinationsol.location.events;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.location.components.Position;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Updates the {@link Position} component of an entity.
 */
public class PositionUpdateEvent implements Event {

    private Vector2 position;

    public PositionUpdateEvent(Vector2 position) {
        this.position = position;
    }

    /**
     * The new value for the position of the entity.
     */
    public Vector2 getPosition() {
        return position;
    }

}
