/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

public class SystemBelt {
    private final Float myHalfWidth;
    private final float myRadius;
    private final SolSystem myS;
    private final SysConfig myConfig;
    private final float myDps;

    public SystemBelt(Float halfWidth, float radius, SolSystem s, SysConfig config) {
        myHalfWidth = halfWidth;
        myRadius = radius;
        myS = s;
        myConfig = config;
        myDps = HardnessCalc.getBeltDps(config);
    }

    public float getRadius() {
        return myRadius;
    }

    public Float getHalfWidth() {
        return myHalfWidth;
    }

    public boolean contains(Vector2 pos) {
        float toCenter = myS.getPos().dst(pos);
        return myRadius - myHalfWidth < toCenter && toCenter < myRadius + myHalfWidth;
    }

    public SysConfig getConfig() {
        return myConfig;
    }

    public float getDps() {
        return myDps;
    }
}
