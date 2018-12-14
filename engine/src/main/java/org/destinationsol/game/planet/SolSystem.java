/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.HardnessCalc;

import java.util.ArrayList;

public class SolSystem {

    private final Vector2 position;
    private final ArrayList<Planet> planets;
    private final ArrayList<SystemBelt> belts;
    private final SysConfig config;
    private final String name;
    private final float radius;
    private final float damagePerSecond;
    private final float innerDamagePerSecond;
    private float innerRadius;

    public SolSystem(Vector2 position, SysConfig config, String name, float sysRadius) {
        this.config = config;
        this.name = name;
        this.position = new Vector2(position);
        planets = new ArrayList<>();
        belts = new ArrayList<>();
        radius = sysRadius;
        damagePerSecond = HardnessCalc.getSysDps(config, false);
        innerRadius = radius / 2;
        innerDamagePerSecond = HardnessCalc.getSysDps(config, true);

    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public ArrayList<SystemBelt> getBelts() {
        return belts;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public SysConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public void addBelt(SystemBelt belt) {
        float newInnerRad = belt.getRadius() - belt.getHalfWidth();
        if (belts.size() == 0 || innerRadius < newInnerRad) {
            innerRadius = newInnerRad;
        }
        belts.add(belt);
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public float getDps() {
        return damagePerSecond;
    }

    public float getInnerDps() {
        return innerDamagePerSecond;
    }
}
