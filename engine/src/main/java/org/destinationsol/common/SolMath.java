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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.json.JSONArray;
import org.json.JSONObject;
import com.badlogic.gdx.utils.Pool;
import org.destinationsol.Const;

import java.util.ArrayList;

/**
 * This class contains a collection of handy mathematical and programmatical functions for working with angles,
 * distances, speeds and vectors.
 */
public class SolMath {

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
        if (angleDiff(src, dst) <= speed) {
            return dst;
        }
        float diff = norm(dst - src);
        return norm(diff > 0 ? src + speed : src - speed);
    }

    /**
     * Normalizes the angle, ie puts it in the range (-180, 180].
     *
     * @param a Angle in degrees
     * @return Normalizes angle in degrees
     */
    public static float norm(float a) {
        if (a != a) {
            throw new AssertionError("normalizing NaN angle");
        }
        while (a <= -180) {
            a += 360;
        }
        while (a > 180) {
            a -= 360;
        }
        return a;
    }

    /**
     * Assures the value is in the range [0, 1], if not, sets it to 0, resp. 1
     *
     * @param val Value to clamp
     * @return Clamped value
     */
    public static float clamp(float val) {
        return MathUtils.clamp(val, (float) 0, (float) 1);
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
     * Check that an object has returned all the vectors he has borrowed.
     *
     * @param o Object to blame upon the not returning of vectors.
     * @throws AssertionError When not all of the vectors were returned
     */
    public static void checkVectorsTaken(Object o) {
        if (SolMath.vectorsTaken != 0) {
            throw new AssertionError("vectors " + SolMath.vectorsTaken + ", blame on " + o);
        }
    }

    /**
     * Converts position in the specified relative coordinate system to absolute position.
     * <p>
     * Be wary that the returned vector is {@link Bound}.
     *
     * @param relPos    Position you want to be converted
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos   Offset of the relative coordinate system to the absolute coordinate system
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
     * @param position  Position you want to set
     * @param relPos    Relative position you want to be converted
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos   Offset of the relative coordinate system to the absolute coordinate system
     */
    public static void toWorld(Vector2 position, Vector2 relPos, float baseAngle, Vector2 basePos) {
        position.set(relPos);
        rotate(position, baseAngle);
        position.add(basePos);
    }

    /**
     * Converts absolute position to position in the specified relative coordinate system.
     * <p>
     * Be wary that the returned vector is {@link Bound}.
     *
     * @param position  Position you want to be converted
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos   Offset of the relative coordinate system to the absolute coordinate system
     * @return {@link Bound} vector with the relative position.
     */
    @Bound
    public static Vector2 toRel(Vector2 position, float baseAngle, Vector2 basePos) {
        Vector2 v = getVec();
        toRel(position, v, baseAngle, basePos);
        return v;
    }

    /**
     * Sets position to relative position in specified relative coordinate system from absolute position.
     *
     * @param position  Absolute position you want to have converted
     * @param relPos    Relative position you want to set
     * @param baseAngle Angle of the relative coordinate system to the absolute coordinate system
     * @param basePos   Offset of the relative coordinate system to the absolute coordinate system
     */
    public static void toRel(Vector2 position, Vector2 relPos, float baseAngle, Vector2 basePos) {
        relPos.set(position);
        relPos.sub(basePos);
        rotate(relPos, -baseAngle);
    }

    /**
     * Rotates {@link Vector2} by the given angle.
     *
     * @param angle Angle to rotate by, in degrees
     * @param v     Vector to rotate
     */
    public static void rotate(Vector2 v, float angle) {
        v.rotate(angle);
    }

    /**
     * Computes a distance between two {@link Vector2 vectors}.
     * <p>
     * Be wary that the returned {@link Vector2} is {@link Bound}.
     *
     * @param from 1st vector
     * @param to   2nd vector
     * @return {@link Bound} vector representing the distance.
     */
    @Bound
    public static Vector2 distVec(Vector2 from, Vector2 to) {
        Vector2 v = getVec(to);
        v.sub(from);
        return v;
    }

    /**
     * Computes a length of projection of given {@link Vector2} on line under specified angle.
     *
     * @param v     Vector projection of which to calculate
     * @param angle Angle of the line to project onto, in degrees
     * @return Length of the projection
     */
    public static float project(Vector2 v, float angle) {
        float angleDiff = angle - SolMath.angle(v);
        return v.len() * MathUtils.cosDeg(angleDiff);
    }

    /**
     * Computes a square root of number.
     *
     * @param v Number square root of which to calculate
     * @return Calculated square root
     */
    public static float sqrt(float v) {
        return (float) Math.sqrt(v);
    }

    /**
     * Computes normalized angle between 2 {@link Vector2 vectors}.
     *
     * @param from 1st vector
     * @param to   2nd vector
     * @return The computed angle
     */
    public static float angle(Vector2 from, Vector2 to) {
        Vector2 tmp = distVec(from, to);
        final float angle = angle(tmp);
        free(tmp);
        return angle;
    }

    /**
     * Computes normalized angle of a {@link Vector2}.
     *
     * @param v Vector angle of which to compute
     * @return The computed angle
     */
    public static float angle(Vector2 v) {
        return norm(MathUtils.atan2(v.y, v.x) * MathUtils.radDeg);
    }

    /**
     * Computes asin (inverse function of sin) of value.
     *
     * @param val Value asin of which to compute
     * @return The computed angle, in degrees.
     */
    public static float arcSin(float val) {
        return (float) Math.asin(val) * MathUtils.radDeg;
    }

    /**
     * Computes angular diameter for object with given radius and distance.
     * <p>
     * Angular diameter works as follows: given a spherical units that has specified {@code radius}, and is {@code dist}
     * units away from you, result of this function should be the angle the object is taking up in your view.
     *
     * @param radius Radius of the object in talk
     * @param dist   Distance from the object in talk
     * @return Calculated angular diameter, in degrees
     * @see <a href=https://en.wikipedia.org/wiki/Angular_diameter>https://en.wikipedia.org/wiki/Angular_diameter</a>
     */
    public static float angularWidthOfSphere(float radius, float dist) {
        return arcSin(radius / dist);
    }

    /**
     * Given an arc of certain length and radius, calculate what angular part of circle it is.
     *
     * @param hordeLen Length of the arc
     * @param radius   Radius of the arc
     * @return Calculated angle, in degrees
     */
    public static float arcToAngle(float hordeLen, float radius) {
        return MathUtils.radDeg * hordeLen / radius;
    }

    /**
     * Calculate the length hypotenuse from the two other sides in triangle.
     * <p>
     * Hypotenuse: the longest side of any triangle that has one angle of 90°
     * (<a href=https://dictionary.cambridge.org/dictionary/english/hypotenuse>dictionary.cambridge.org</a>)
     *
     * @param a 1st side of the triangle
     * @param b 2nd side of the triangle
     * @return The calculated length of hypotenuse
     */
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

    /**
     * Calculates the difference between two angles.
     *
     * @param a 1st angle, in degrees
     * @param b 2nd angle in degrees
     * @return Difference between those two angles, in degrees
     */
    public static float angleDiff(float a, float b) {
        return abs(norm(a - b));
    }

    /**
     * Given a circle of certain radius, calculate the length of arc represented by certain angle in the circle.
     *
     * @param angle Angle in the circle, in degrees
     * @param r     Radius of the circle
     * @return Length of the arc
     */
    public static float angleToArc(float angle, float r) {
        return (angle * MathUtils.degRad) * r;
    }

    /**
     * Calculates solution to a quadratic equation.
     * <p>
     * Quadratic equation has generally this form:
     * {@code 0 = (a * (x^2)) + (b * x) + c}
     * , where {@code a}, {@code b}, and {@code c} are known, and {@code x} is the unknown we want to get.
     *
     * @param a {@code a} variable in the quadratic equation
     * @param b {@code b} variable in the quadratic equation
     * @param c {@code c} variable in the quadratic equation
     * @return The solution (variable {@code x} of the quadratic equation. When there are two possible solutions, the
     * greater of them is returned.
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

    /**
     * Calculates solution to a linear equation.
     * <p>
     * Linear equation has generally this form:
     * {@code 0 = (b * x) + c}
     * , where {@code b}, and {@code c} are known, and {@code x} is the unknown we want to get.
     *
     * @param b {@code b} variable in the quadratic equation
     * @param c {@code c} variable in the quadratic equation
     * @return The solution (variable {@code x}) of the linear equation. When there is no solution possible, NaN is returned.
     */
    public static float genLin(float b, float c) {
        if (b == 0) {
            return c == 0 ? 0 : Float.NaN;
        }
        float res = -c / b;
        return res < 0 ? Float.NaN : res;
    }

    //TODO The three following functions aren't as much of mathematical functions as they are input processing functions. Move them somewhere else?
    public static Vector2 readV2(JSONObject v, String name) {
        return readV2(v.getString(name));
    }

    public static Vector2 readV2(String encoded) {
        String[] parts = encoded.split(" ");
        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        return new Vector2(x, y);
    }

    public static ArrayList<Vector2> readV2List(JSONObject parentNode, String name) {
        ArrayList<Vector2> res = new ArrayList<>();
        JSONArray listNode = parentNode.has(name) ? parentNode.getJSONArray(name) : null;
        if (listNode == null) {
            return res;
        }
        for (Object val : listNode) {
            if(val instanceof String) {
                Vector2 vec = readV2((String) val);
                res.add(vec);
            }
        }
        return res;
    }

    /**
     * Returns whether object can accelerate in direction, based on its current velocity.
     *
     * Object can accelerate until reaching {@link Const#MAX_MOVE_SPD maximal movement speed}, and afterwards, if it
     * attempts to accelerate in direction that would not bring its speed further over the maximum speed.
     *
     * @param accAngle Angle under which the object tries to accelerate
     * @param velocity Current velocity of the object
     * @return True if object can accelerate further, false otherwise
     */
    public static boolean canAccelerate(float accAngle, Vector2 velocity) {
        return velocity.len() < Const.MAX_MOVE_SPD || angleDiff(angle(velocity), accAngle) > 90;
    }

    /**
     * Returns a String representation of float value.
     *
     * @param v Value to represent in String
     * @return String representation of the value
     */
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
