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

import org.destinationsol.location.events.AngleUpdateEvent;
import org.terasology.gestalt.entitysystem.component.Component;

public class Angle implements Component<Angle> {

    /**
     * The angle, in degrees, of the entity. This is changed every tick by an {@link AngleUpdateEvent}.
     */
    private float angle;

    @Override
    public void copy(Angle other) {
        this.angle = other.angle;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle >= 360) {
            angle -= 360;
        }
        
        this.angle = angle;
    }
}
