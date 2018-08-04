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

import org.destinationsol.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class ConsumedAngles {
    private final List<Float> angles;
    private final List<Float> halfWidths;

    public ConsumedAngles() {
        angles = new ArrayList<>();
        halfWidths = new ArrayList<>();
    }

    public boolean isConsumed(float angle, float objAngularHalfWidth) {
        int size = angles.size();
        for (int i = 0; i < size; i++) {
            Float a = angles.get(i);
            Float halfWidth = halfWidths.get(i);
            if (SolMath.angleDiff(angle, a) < halfWidth + objAngularHalfWidth) {
                return true;
            }
        }
        return false;
    }

    public void add(float angle, float hw) {
        angles.add(angle);
        halfWidths.add(hw);
    }
}
