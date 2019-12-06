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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import org.destinationsol.common.SolColorUtil;

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
        public void set(final float perc, Color col) {
            float percentage = MathUtils.clamp(perc, (float) 0, (float) 1);
            col.r = midVal(myStart.r, myEnd.r, percentage);
            col.g = midVal(myStart.g, myEnd.g, percentage);
            col.b = midVal(myStart.b, myEnd.b, percentage);
            col.a = midVal(myStart.a, myEnd.a, percentage);
        }

        private float midVal(float s, float e, float perc) {
            return s + perc * (e - s);
        }
    }

}
