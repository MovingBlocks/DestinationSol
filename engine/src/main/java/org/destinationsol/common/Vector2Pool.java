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
package org.destinationsol.common;

import com.badlogic.gdx.math.Vector2;

public class Vector2Pool {
    private static final int MAX_VECTORS = 100;
    private static final Vector2[] EBP = new Vector2[MAX_VECTORS];
    private static int esp = -1;
    private static final Vector2 NULL_VECTOR = new Vector2();

    public static Vector2 getVector() {
        if (esp != -1) {
            return EBP[esp--];
        }
        return new Vector2();
    }

    public static Vector2 getVector(float x, float y) {
        if (esp != -1) {
            EBP[esp].x = x;
            EBP[esp].y = y;
            return EBP[esp--];
        }
        return new Vector2(x, y);
    }

    public static Vector2 getVector(Vector2 v) {
        if (esp != -1) {
            EBP[esp].set(v);
            return EBP[esp--];
        }
        return new Vector2(v);
    }

    public static void freeVector(Vector2 v) {
        if (esp != MAX_VECTORS - 1) {
            v.set(NULL_VECTOR);
            EBP[++esp] = v;
        }
    }
}
