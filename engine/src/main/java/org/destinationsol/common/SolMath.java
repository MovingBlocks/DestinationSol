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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import org.destinationsol.Const;

import java.util.ArrayList;

/**
 * This class presents a collection of handy mathematical as well as programmatical functions, for working with angles,
 * distances, speeds, vectors.
 */
public class SolMath {
    /**
     * The pi number constant.
     */
    public static final float PI = MathUtils.PI;
    /**
     * Multiply radians by this number to convert them to degrees.
     */
    public static float radDeg = MathUtils.radDeg;
    /**
     * Multiply degrees by this number to convert them to radians.
     */
    public static float degRad = MathUtils.degRad;
    /**
     * Stores {@link Vector2 vectors} used for borrowing, see {@link #getVec()} for more info.
     */
    private static Pool<Vector2> vectorPool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    /**
     * Represents the amount of {@link Vector2 vectors} currently borrowed by {@link #getVec()}.
     */
    private static int vectorsTaken;

    /**
     * Converts boolean to integer, where {@code true} equals {@code 1} and {@code false} equals {@code -1}.
     *
     * @param b boolean to convert
     * @return int representation of the boolean
     */
    public static int toInt(boolean b) {
        return b ? 1 : -1;
    }

    /**
     * Returns absolute value of a float.
     *
     * @param a float absolute value of which to calculate
     * @return Absolute value of the float
     */
    public static float abs(float a) {
        return a < 0f ? -a : a;
    }

    /**
     * Use this method when you want some value to gradually transform to the desired value.
     *
     * @param src   Current value
     * @param dst   Desired value
     * @param speed Speed of change in value per call
     * @return New value that is closer to the desired one
     */
    public static float approach(float src, float dst, float speed) {
        if (dst - speed <= src && src <= dst + speed) {
            return dst;
        }
        return src < dst ? src + speed : src - speed;
    }

    /**
     * Use this method when you want some angle to gradually transform to desired angle.
     *
     * @param src   Current angle in degrees
     * @param dst   Desired angle in degrees
     * @param speed Speed of change in degrees per call
     * @return New angle closer to the desired one
     */
    public static float approachAngle(float src, float dst, float speed) {
        float diff = norm(dst - src);
        float absoluteDiff = abs(diff);
        if (absoluteDiff <= speed) {
            return dst;
        }
        return norm(diff > 0 ? src + speed : src - speed);
    }

    /**
     * Normalizes the angle, ie puts it in the range [-179.999, 180].
     *
     * @param a Angle in degrees
     * @return Normalizes angle in degrees
     */
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
     * Assures the value is in the bounds specified by upper and lower bound, if not, sets it to the upper/lower bound.
     *
     * @param val Value to clamp
     * @param min Lower bound
     * @param max Higher bound
     * @return Clamped value
     */
    public static float clamp(float val, float min, float max) {
        return MathUtils.clamp(val, min, max);
    }

    /**
     * Assures the value is in the range [0, 1], if not, sets it to 0, resp. 1
     *
     * @param val Value to clamp
     * @return Clamped value
     */
    public static float clamp(float val) {
        return clamp(val, 0, 1);
    }

    /**
     * Sets to given {@link Vector2} to specified angle and length.
     *
     * @param vec   Vector to set
     * @param angle Angle the vector should have
     * @param len   Length the vector should have
     */
    public static void fromAl(Vector2 vec, float angle, float len) {
        vec.set(len, 0);
        rotate(vec, angle);
    }

    /**
     * Builds a {@link Bound} vector with the given angle and length.
     *
     * @param angle Angle the vector should have
     * @param len   Length the vector should have
     */
    @Bound
    public static Vector2 fromAl(float angle, float len) {
        Vector2 vec = getVec();
        fromAl(vec, angle, len);
        return vec;
    }

    /**
     * Builds a {@link Bound} vector set to the value of specified vector.
     *
     * @param src Vector to copy
     * @return Bound copy of the vector
     */
    @Bound
    public static Vector2 getVec(Vector2 src) {
        return getVec(src.x, src.y);
    }

    /**
     * Builds a {@link Bound} vector set to the specified value.
     *
     * @param x x value to set the vector to
     * @param y y value to set the vector to
     * @return Bound copy of the vector
     */
    @Bound
    public static Vector2 getVec(float x, float y) {
        vectorsTaken++;
        Vector2 v = vectorPool.obtain();
        v.set(x, y);
        return v;
    }

    /**
     * Frees the bound vector. The freed vector is meant to be no longer used after freeing.
     *
     * @param v Vector to free.
     */
    public static void free(Vector2 v) {
        vectorsTaken--;
        vectorPool.free(v);
    }

    /**
     * Builds a {@link Bound} vector set to the value {@code (0, 0)}.
     *
     * @return Bound copy of the vector
     */
    @Bound
    public static Vector2 getVec() {
        return getVec(0, 0);
    }

    /**
     * Converts position in the specified relative coordinate system to absolute position.
     * <p>
     * Be wary that the returned vector is {@link Bound}.
     *
     * @param relPos Position you want to be converted
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos Offset of the relative coordinate system to the absolute coordinate system
     * @return {@link Bound} vector with the absolute position.
     */
    @Bound
    public static Vector2 toWorld(Vector2 relPos, float baseAngle, Vector2 basePos) {
        Vector2 v = getVec();
        toWorld(v, relPos, baseAngle, basePos);
        return v;
    }

    /**
     * Sets position to absolute position calculated from relative position in specified relative coordinate system.
     *
     * @param position Position you want to set
     * @param relPos Relative position you want to be converted
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos Offset of the relative coordinate system to the absolute coordinate system
     */
    public static void toWorld(Vector2 position, Vector2 relPos, float baseAngle, Vector2 basePos) {
        position.set(relPos);
        rotate(position, baseAngle);
        position.add(basePos);
    }

    /**
     * converts position (a position in an absolute coordinate system) to the position in the relative system of coordinates (defined by baseAngle and basePos)
     */
    @Bound
    public static Vector2 toRel(Vector2 position, float baseAngle, Vector2 basePos) {
        Vector2 v = getVec();
        toRel(position, v, baseAngle, basePos);
        return v;
    }

    /**
     * converts position (a position in an absolute coordinate system) to the position in the relative system of coordinates
     * (defined by baseAngle and basePos) (which is written to relPos)
     */
    public static void toRel(Vector2 position, Vector2 relPos, float baseAngle, Vector2 basePos) {
        relPos.set(position);
        relPos.sub(basePos);
        rotate(relPos, -baseAngle);
    }

    /**
     * rotates a vector to an angle. if not precise, works faster, but the actual angle might slightly differ from the given one
     */
    public static void rotate(Vector2 v, float angle) {
        v.rotate(angle);
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
        return v.len() * MathUtils.cosDeg(angleDiff);
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
        Vector2 tmp = getVec(to);
        tmp.sub(from);
        final float angle = angle(tmp, precise);
        free(tmp);
        return angle;
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

    public static boolean isAngleBetween(float a, float b, float x) {
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

    public static void checkVectorsTaken(Object o) {
        if (SolMath.vectorsTaken != 0) {
            throw new AssertionError("vectors " + SolMath.vectorsTaken + ", blame on " + o);
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

    public static boolean canAccelerate(float accAngle, Vector2 speed) {
        return speed.len() < Const.MAX_MOVE_SPD || angleDiff(angle(speed), accAngle) > 90;
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
