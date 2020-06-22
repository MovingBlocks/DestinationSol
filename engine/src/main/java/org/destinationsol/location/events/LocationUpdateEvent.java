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
import org.destinationsol.location.components.Location;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Updates the {@link Location} of an entity.
 */
public class LocationUpdateEvent implements Event {

    private Vector2 position;
    private float angle;
    private Vector2 velocity;

    public LocationUpdateEvent(Vector2 position, float angle, Vector2 velocity) {
        this.position = position;
        this.angle = angle;
        this.velocity = velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
