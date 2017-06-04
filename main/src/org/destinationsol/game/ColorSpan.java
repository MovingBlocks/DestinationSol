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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolMath;

public abstract class ColorSpan {

    public static RgbSpan rgb(Color start, Color end) {
        return new RgbSpan(start, end);
    }

    public static RgbSpan rgb(float[] start, float[] end) {
        Color startC = new Color();
        SolColorUtil.fromHSB(start[0], start[1], start[2], start[3], startC);
        Color endC = new Color();
        SolColorUtil.fromHSB(end[0], end[1], end[2], end[3], endC);
        return rgb(startC, endC);
    }

    public static HsbSpan hsb(Color start, Color end) {
        return hsb(SolColorUtil.toHSB(start), SolColorUtil.toHSB(end));
    }

    public static HsbSpan hsb(float[] start, float[] end) {
        return new HsbSpan(start, end);
    }

    public abstract void set(float perc, Color col);

    public static class RgbSpan extends ColorSpan {
        private final Color myStart;
        private final Color myEnd;

        public RgbSpan(Color start, Color end) {
            myStart = new Color(start);
            myEnd = new Color(end);
        }

        @Override
        public void set(float perc, Color col) {
            perc = SolMath.clamp(perc, 0, 1);
            col.r = midVal(myStart.r, myEnd.r, perc);
            col.g = midVal(myStart.g, myEnd.g, perc);
            col.b = midVal(myStart.b, myEnd.b, perc);
            col.a = midVal(myStart.a, myEnd.a, perc);
        }

        private float midVal(float s, float e, float perc) {
            return s + perc * (e - s);
        }
    }

}
