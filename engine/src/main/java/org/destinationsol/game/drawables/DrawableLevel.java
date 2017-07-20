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

package org.destinationsol.game.drawables;

public enum DrawableLevel {
    NEBULAE(11), STARS(10),
    FAR_DECO_3(2.5f), FAR_DECO_2(2f), FAR_DECO_1(1.5f),
    ATM, DECO, PART_BG_0, U_GUNS, STATIONS, BIG_BODIES, BODIES, GUNS, PART_FG_0, PART_FG_1, PROJECTILES, GROUND, CLOUDS;

    public final float depth;

    DrawableLevel(float depth) {
        this.depth = depth;
    }

    DrawableLevel() {
        this(1f);
    }
}
