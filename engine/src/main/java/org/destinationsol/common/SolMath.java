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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import org.destinationsol.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A class with helpful mathematical functions
 */
public class SolMath {
    private static Logger logger = LoggerFactory.getLogger(SolMath.class);
    public static final float PI = MathUtils.PI;
    public static float radDeg = MathUtils.radDeg;
    public static float degRad = MathUtils.degRad;
    public static Pool<Vector2> vs = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    public static int VECTORS_TAKEN;
    public static Vector2 tmp = new Vector2();


    public static int toInt(boolean b) {
        return b ? 1 : -1;
    }

    public static float abs(float a) {
        return a < 0f ? -a : a;
    }

    /**
     * Use this method when you want some value to gradually transform to the desired value.
     *
     * @param src actual value
     * @param dst desired value
     * @param spd speed of change
     * @return a new value that is closer to the desired one
     */
    public static float approach(float src, float dst, float spd) {
        if (dst - spd <= src && src <= dst + spd) {
            return dst;
        }
        return src < dst ? src + spd : src - spd;
    }

    /**
     * Same as {@code approach()}, but in the radial coordinates. That is, 170 degrees would move to -10 by growing through 175 degrees
     */
    public static float approachAngle(float src, float dst, float spd) {
        float diff = norm(dst - src);
        float da = abs(diff);
        if (da <= spd) {
            return dst;
        }
        return diff > 0 ? src + spd : src - spd;
    }

    /**
     * Normalizes the angle
     */
    @Norm
    public static float norm(float a) {
        if (a != a) {
            throw new AssertionError("normalizing NaN angle");
        }
        while (a <= -180)
            a += 360;
        while (a > 180)
            a -= 360;
        return a;
    }

    /**
     * Returns a random float v such that -minMax <= v && v < minMax
     *
     * @param minMax a positive value
     */
    public static float rnd(float minMax) {
        return rnd(-minMax, minMax);
    }

    /**
     * Returns a random float v such that min <= v && v < max. Min shouldn't equal to max
     */
    public static float rnd(float min, float max) {
        float result = max;
        if (min == max) {
            Gdx.app.log("SolMath", "rnd was called with bad parameters! Min " + min + " matches max " + max + ", accepting max.");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", ste.toString());
            }

            return result;
        }
        while (result == max) {
            result = MathUtils.random(min, max);
        }
        return result;
    }

    /**
     * Returns a random int v such that 0 <= v && v < max.
     *
     * @param max a positive value
     */
    public static int intRnd(int max) {
        return intRnd(0f, max);
    }

    /**
     * Returns a random int v such that max*perc <= v && v < max.
     *
     * @param perc should be >= 0 and < 1
     * @param max  a positive value
     */
    public static int intRnd(float perc, int max) {
        int r = max;
        int min = (int) (max * perc);
        if (min == max) {
            throw new AssertionError("intRnd min equals max " + min);
        }
        while (r == max)
            r = MathUtils.random(min, max);
        return r;
    }

    /**
     * Returns a random int v such that min <= v && v <= max
     */
    public static int intRnd(int min, int max) {
        return MathUtils.random(min, max);
    }

    /**
     * Clamps the value (returns min if val < min, max if max < val, val otherwise)
     */
    public static float clamp(float val, float min, float max) {
        return MathUtils.clamp(val, min, max);
    }

    public static float clamp(float val) {
        return clamp(val, 0, 1);
    }

    /**
     * Modifies the given vector so it has the given angle and length. The resulting vector angle may slightly differ from a given one.
     */
    public static void fromAl(Vector2 vec, float angle, float len) {
        fromAl(vec, angle, len, false);
    }

    /**
     * Modifies the given vector so it has the given angle and length. If not {@code precice}, the resulting vector angle may slightly differ from a given one, in the cost of performance.
     */
    public static void fromAl(Vector2 vec, float angle, float len, boolean precise) {
        vec.set(len, 0);
        rotate(vec, angle, precise);
    }

    /**
     * Builds a bound vector with the given angle and length.
     */
    @Bound
    public static Vector2 fromAl(float angle, float len) {
        return fromAl(angle, len, false);
    }

    /**
     * Builds a bound vector with the given angle and length. If not {@code precice}, the resulting vector angle may slightly differ from a given one, in the cost of performance.
     */
    @Bound
    public static Vector2 fromAl(float angle, float len, boolean precise) {
        Vector2 vec = getVec();
        fromAl(vec, angle, len, precise);
        return vec;
    }

    /**
     * @return a new bound copy of src
     */
    @Bound
    public static Vector2 getVec(Vector2 src) {
        return getVec(src.x, src.y);
    }

    /**
     * @return a new bound vector
     */
    @Bound
    public static Vector2 getVec(float x, float y) {
        VECTORS_TAKEN++;
        Vector2 v = vs.obtain();
        v.set(x, y);
        return v;
    }

    /**
     * frees the bound vector. Don't use this vector after freeing!
     */
    public static void free(Vector2 v) {
        VECTORS_TAKEN--;
        vs.free(v);
    }

    /**
     * @return a new bound vector
     */
    @Bound
    public static Vector2 getVec() {
        return getVec(0, 0);
    }

    /**
     * generates a random number between 0 and 1 and returns true if it is less than v, false otherwise
     */
    public static boolean test(float v) {
        return rnd(0, 1) < v;
    }

    /**
     * @return approximate cos of a degrees
     */
    public static float cos(float a) {
        return MathUtils.cosDeg(a);
    }

    /**
     * @return approximate sin of a degrees
     */
    public static float sin(float a) {
        return MathUtils.sinDeg(a);
    }

    /**
     * converts relPos (a position in a relative coordinate system defined by baseAngle and basePos) to the absolute position
     */
    @Bound
    public static Vector2 toWorld(Vector2 relPos, float baseAngle, Vector2 basePos) {
        Vector2 v = getVec();
        toWorld(v, relPos, baseAngle, basePos, false);
        return v;
    }

    /**
     * converts relPos (a position in a relative coordinate system defined by baseAngle and basePos) to the absolute position (which is written to pos)
     */
    public static void toWorld(Vector2 pos, Vector2 relPos, float baseAngle, Vector2 basePos, boolean precise) {
        pos.set(relPos);
        rotate(pos, baseAngle, precise);
        pos.add(basePos);
    }

    /**
     * converts pos (a position in an absolute coordinate system) to the position in the relative system of coordinates (defined by baseAngle and basePos)
     */
    @Bound
    public static Vector2 toRel(Vector2 pos, float baseAngle, Vector2 basePos) {
        Vector2 v = getVec();
        toRel(pos, v, baseAngle, basePos);
        return v;
    }

    /**
     * converts pos (a position in an absolute coordinate system) to the position in the relative system of coordinates
     * (defined by baseAngle and basePos) (which is written to relPos)
     */
    public static void toRel(Vector2 pos, Vector2 relPos, float baseAngle, Vector2 basePos) {
        relPos.set(pos);
        relPos.sub(basePos);
        rotate(relPos, -baseAngle);
    }

    /**
     * rotates a vector to an angle. if not precise, works faster, but the actual angle might slightly differ from the given one
     */
    public static void rotate(Vector2 v, float angle, boolean precise) {
        if (precise) {
            v.rotate(angle);
        } else {
            float cos = cos(angle);
            float sin = sin(angle);
            float newX = v.x * cos - v.y * sin;
            float newY = v.x * sin + v.y * cos;
            v.x = newX;
            v.y = newY;
        }
    }

    /**
     * rotates a vector to an angle. The actual angle might slightly differ from the given one
     */
    public static void rotate(Vector2 v, float angle) {
        rotate(v, angle, false);
    }

    /**
     * @return a new bound vector that is a substraction (to - from)
     */
    @Bound
    public static Vector2 distVec(Vector2 from, Vector2 to) {
        Vector2 v = getVec(to);
        v.sub(from);
        return v;
    }

    /**
     * @return a length of a projection of a vector onto a line defined by angle
     */
    public static float project(Vector2 v, float angle) {
        float angleDiff = angle - SolMath.angle(v);
        return v.len() * cos(angleDiff);
    }

    public static float sqrt(float v) {
        return (float) Math.sqrt(v);
    }

    /**
     * @return approximate angle between 2 vectors. may be negative.
     */
    public static float angle(Vector2 from, Vector2 to) {
        return angle(from, to, false);
    }

    /**
     * @return angle between 2 vectors. may be negative. if not precise, approximation is returned
     */
    public static float angle(Vector2 from, Vector2 to, boolean precise) {
        tmp.set(to);
        tmp.sub(from);
        return angle(tmp, precise);
    }

    /**
     * @return angle of a vector. if not precise, approximation is returned.
     * (1, 0) is right and 0 degrees
     * (0, 1) is down and 90 degrees
     * (-1, 0) is left and 180 degrees
     * (0, -1) is up and -90 degrees
     */
    public static float angle(Vector2 v, boolean precise) {
        if (precise) {
            return v.angle();
        } else {
            return MathUtils.atan2(v.y, v.x) * radDeg;
        }
    }

    /**
     * @return angle of a vector. approximation is returned.
     */
    public static float angle(Vector2 v) {
        return angle(v, false);
    }

    public static float arcSin(float val) {
        return (float) Math.asin(val) * radDeg;
    }

    public static float angularWidthOfSphere(float radius, float dist) {
        return arcSin(radius / dist);
    }

    public static float arcToAngle(float hordeLen, float radius) {
        return 180 * hordeLen / (PI * radius);
    }

    public static float hypotenuse(float a, float b) {
        return sqrt(a * a + b * b);
    }

    public static float windowCenter(float val, float window) {
        float winNr = (int) (val / window);
        winNr += .5f * toInt(val > 0);
        return winNr * window;
    }

    public static boolean isAngleBetween(@Norm float a, @Norm float b, @Norm float x) {
        if (a <= b) {
            return a <= x && x < b;
        }
        return a <= x || x < b;
    }

    public static float angleDiff(float a, float b) {
        return abs(norm(a - b));
    }

    public static float angleToArc(float angle, float r) {
        return angle / 180 * PI * r;
    }

    /**
     * @return a random element of a list
     */
    public static <T> T elemRnd(List<T> list) {
        int idx = intRnd(list.size());
        return list.get(idx);
    }

    public static void checkVectorsTaken(Object o) {
        if (SolMath.VECTORS_TAKEN != 0) {
            throw new AssertionError("vectors " + SolMath.VECTORS_TAKEN + ", blame on " + o);
        }
    }

    /**
     * @return solution of a quadratic equation. if 2 solutions possible, the greater is returned.
     */
    public static float genQuad(float a, float b, float c) {
        if (a == 0) {
            return genLin(b, c);
        }
        float disc = b * b - 4 * a * c;
        if (disc < 0) {
            return Float.NaN;
        }
        if (disc == 0) {
            return -b / 2 / a;
        }
        float dsq = sqrt(disc);
        float x1 = (-b - dsq) / 2 / a;
        float x2 = (-b + dsq) / 2 / a;
        if (x1 < 0) {
            return x2 < 0 ? Float.NaN : x2;
        }
        if (x2 < 0) {
            return x1;
        }
        return x1 < x2 ? x1 : x2;
    }

    private static float genLin(float b, float c) {
        if (b == 0) {
            return c == 0 ? 0 : Float.NaN;
        }
        float res = -c / b;
        return res < 0 ? Float.NaN : res;
    }

    public static Vector2 readV2(JsonValue v, String name) {
        return readV2(v.getString(name));
    }

    public static Vector2 readV2(String encoded) {
        String[] parts = encoded.split(" ");
        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        return new Vector2(x, y);
    }

    public static ArrayList<Vector2> readV2List(JsonValue parentNode, String name) {
        ArrayList<Vector2> res = new ArrayList<>();
        JsonValue listNode = parentNode.get(name);
        if (listNode == null) {
            return res;
        }
        for (JsonValue vNode : listNode) {
            Vector2 vec = readV2(vNode.asString());
            res.add(vec);
        }
        return res;
    }

    public static boolean canAccelerate(float accAngle, Vector2 spd) {
        return spd.len() < Const.MAX_MOVE_SPD || angleDiff(angle(spd), accAngle) > 90;
    }

    public static String nice(float v) {
        int i = (int) (v * 10);
        int whole = i / 10;
        int dec = i - 10 * whole;
        if (dec < 0) {
            dec = -dec;
        }
        return whole + "." + dec;
    }
}
