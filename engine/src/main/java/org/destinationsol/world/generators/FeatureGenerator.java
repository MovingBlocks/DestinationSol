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
package org.destinationsol.world.generators;

import com.badlogic.gdx.math.Vector2;

/**
 * This class represents the framework
 */
public abstract class FeatureGenerator {
    private Vector2 position;
    private float radius;
    private boolean positioned;

    public abstract void build();

    public void setPosition(Vector2 position) {
        this.position = position;
        setPositioned(true);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setPositioned(boolean p) {
        positioned = p;
    }

    public boolean getPositioned() {
        return positioned;
    }

}
