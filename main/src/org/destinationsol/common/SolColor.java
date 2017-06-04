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

package org.destinationsol.common;

import com.badlogic.gdx.graphics.Color;

public class SolColor {
    public static final Color DDG = col(.12f, 1);
    public static final Color DG = col(.25f, 1);
    public static final Color G = col(.5f, 1);
    public static final Color LG = col(.75f, 1);
    public static final Color W50 = col(1, .5f);
    public static final Color WHITE = col(1, 1);
    public static final Color BLACK = col(0, 1);

    public static final Color UI_BG = col(0, .75f);
    public static final Color UI_BG_LIGHT = col(0, .5f);
    public static final Color UI_INACTIVE = new Color(0, .75f, 1, .1f);
    public static final Color UI_DARK = new Color(0, .75f, 1, .17f);
    public static final Color UI_MED = new Color(0, .75f, 1, .25f);
    public static final Color UI_LIGHT = new Color(0, .75f, 1, .5f);
    public static final Color UI_OPAQUE = new Color(0, .56f, .75f, 1f);
    public static final Color UI_WARN = new Color(1, .5f, 0, .5f);

    public static Color col(float b, float t) {
        return new Color(b, b, b, t);
    }
}
