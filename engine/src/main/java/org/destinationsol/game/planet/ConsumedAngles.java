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

import org.destinationsol.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class ConsumedAngles {
    private final List<Float> myAngles;
    private final List<Float> myHalfWidths;

    public ConsumedAngles() {
        myAngles = new ArrayList<>();
        myHalfWidths = new ArrayList<>();
    }

    public boolean isConsumed(float angle, float objAngularHalfWidth) {
        int sz = myAngles.size();
        for (int i = 0; i < sz; i++) {
            Float a = myAngles.get(i);
            Float hw = myHalfWidths.get(i);
            if (SolMath.angleDiff(angle, a) < hw + objAngularHalfWidth) {
                return true;
            }
        }
        return false;
    }

    public void add(float angle, float hw) {
        myAngles.add(angle);
        myHalfWidths.add(hw);
    }
}
