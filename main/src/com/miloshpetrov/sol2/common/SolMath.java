package com.miloshpetrov.sol2.common;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.List;

public class SolMath {
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

  public static float approach(float src, float dst, float spd) {
    if (dst - spd <= src && src <= dst + spd) return dst;
    return src < dst ? src + spd : src - spd;
  }

  public static float approachAngle(float src, float dst, float spd) {
    float diff = norm(dst - src);
    float da = abs(diff);
    if (da <= spd) return dst;
    return diff > 0 ? src + spd : src - spd;
  }

  @Norm
  public static float norm(float a) {
    while (a <= -180) a += 360;
    while (a > 180) a -= 360;
    return a;
  }

  public static float rnd(float minMax) {
    return rnd(-minMax, minMax);
  }

  public static float rnd(float min, float max) {
    float res = max;
    if (min == max) throw new RuntimeException("intRnd min equals max " + min);
    while (res == max) {
      res = MathUtils.random(min, max);
    }
    return res;
  }

  public static int intRnd(int max) {
    return intRnd(0, max);
  }

  public static int intRnd(float perc, int max) {
    int r = max;
    int min = (int) (max * perc);
    if (min == max) throw new RuntimeException("intRnd min equals max " + min);
    while (r == max) r = MathUtils.random(min, max);
    return r;
  }

  public static float clamp(float val, float min, float max) {
    return MathUtils.clamp(val, min, max);
  }

  public static void fromAl(Vector2 vec, float angle, float len) {
    fromAl(vec, angle, len, false);
  }

  public static void fromAl(Vector2 vec, float angle, float len, boolean precise) {
    vec.set(len, 0);
    rotate(vec, angle, precise);
  }

  @Bound
  public static Vector2 fromAl(float angle, float len) {
    return fromAl(angle, len, false);
  }

  @Bound
  public static Vector2 fromAl(float angle, float len, boolean precise) {
    Vector2 vec = getVec();
    fromAl(vec, angle, len, precise);
    return vec;
  }

  @Bound
  public static Vector2 getVec(Vector2 src) {
    return getVec(src.x, src.y);
  }

  @Bound
  public static Vector2 getVec(float x, float y) {
    VECTORS_TAKEN++;
    Vector2 v = vs.obtain();
    v.set(x, y);
    return v;
  }

  public static void free(Vector2 v) {
    VECTORS_TAKEN--;
    vs.free(v);
  }

  @Bound
  public static Vector2 getVec() {
    return getVec(0, 0);
  }

  public static boolean test(float v) {
    return rnd(0, 1) < v;
  }

  public static void log(Vector2 pos) {
    System.out.println((int)pos.x + " " + (int)pos.y);
  }

  public static float cos(float a) {
    return MathUtils.cosDeg(a);
  }

  public static float sin(float a) {
    return MathUtils.sinDeg(a);
  }

  public static void log(float f) {
    System.out.println(f);
  }

  @Bound
  public static Vector2 toWorld(Vector2 relPos, float baseAngle, Vector2 basePos) {
    Vector2 v = getVec();
    toWorld(v, relPos, baseAngle, basePos);
    return v;
  }

  public static void toWorld(Vector2 pos, Vector2 relPos, float baseAngle, Vector2 basePos) {
    pos.set(relPos);
    rotate(pos, baseAngle);
    pos.add(basePos);
  }

  @Bound
  public static Vector2 toRel(Vector2 pos, float baseAngle, Vector2 basePos) {
    Vector2 v = getVec();
    toRel(pos, v, baseAngle, basePos);
    return v;
  }

  public static void toRel(Vector2 pos, Vector2 relPos, float baseAngle, Vector2 basePos) {
    relPos.set(pos);
    relPos.sub(basePos);
    rotate(relPos, -baseAngle);
  }

  public static void rotate(Vector2 v, float angle, boolean precise) {
    if (precise) {
      v.rotate(angle);
    }
    else {
      float cos = cos(angle);
      float sin = sin(angle);
      float newX = v.x * cos - v.y * sin;
      float newY = v.x * sin + v.y * cos;
      v.x = newX;
      v.y = newY;
    }
  }

  public static void rotate(Vector2 v, float angle) {
    rotate(v, angle, false);
  }

  @Bound
  public static Vector2 distVec(Vector2 from, Vector2 to) {
    Vector2 v = getVec(to);
    v.sub(from);
    return v;
  }

  public static float project(Vector2 v, float angle) {
    float angleDiff = angle - SolMath.angle(v);
    return v.len() * cos(angleDiff);
  }

  public static float sqrt(float v) {
    return (float) Math.sqrt(v);
  }

  public static float angle(Vector2 from, Vector2 to) {
    return angle(from, to, false);
  }

  public static float angle(Vector2 from, Vector2 to, boolean precise) {
    tmp.set(to);
    tmp.sub(from);
    return angle(tmp, precise);
  }

  public static float angle(Vector2 v, boolean precise) {
    if (precise) return v.angle();
    else return MathUtils.atan2(v.y, v.x) * radDeg;
  }

  public static float angle(Vector2 v) {
    return angle(v, false);
  }

  public static float arcSin(float val) {
    return (float) Math.asin(val) * radDeg;
  }

  public static float hordeToAnlge(float hordeLen, float r) {
    return 180 * hordeLen /(PI * r);
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
    if (a <= b) return a <= x && x < b;
    return  a <= x || x < b;
  }

  public static float angleDiff(float a, float b) {
    return abs(norm(a - b));
  }

  public static float angleToArc(float angle, float r) {
    return angle / 180 * PI * r;
  }

  public static <T> T elemRnd(List<T> list) {
    int idx = intRnd(list.size());
    return list.get(idx);
  }

  public static void checkVectorsTaken(Object o) {
    if (SolMath.VECTORS_TAKEN != 0) throw new RuntimeException("vectors " + SolMath.VECTORS_TAKEN + ", blame on " + o);
  }

  public static float genQuad(float a, float b, float c) {
    if (a == 0) return genLin(b, c);
    float disc = b * b - 4 * a * c;
    if (disc < 0) return Float.NaN;
    if (disc == 0) return -b / 2 / a;
    float dsq = sqrt(disc);
    float x1 = (-b - dsq) / 2 / a;
    float x2 = (-b + dsq) / 2 / a;
    if (x1 < 0) {
      return x2 < 0 ? Float.NaN : x2;
    }
    if (x2 < 0) return x1;
    return x1 < x2 ? x1 : x2;
  }

  private static float genLin(float b, float c) {
    if (b == 0) return c == 0 ? 0 : Float.NaN;
    float res = -c / b;
    return res < 0 ? Float.NaN : res;
  }
}
