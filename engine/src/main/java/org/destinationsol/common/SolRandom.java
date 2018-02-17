package org.destinationsol.common;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;

public class SolRandom {
    // Seeded random is for deterministic processes
    private static long seed;
    private static Random seededRandom = new Random(seed);
    
    private static Random random = new Random();
    
    /**
     * Returns a seeded random float v such that -minMax <= v < minMax
     *
     * @param minMax a positive value that is used as the max while the negative value is used as the min
     */
    public static float seededRandomFloat(float minMax) {
        return SolRandom.seededRandomFloat(-minMax, minMax);
    }

    /**
     * Returns a seeded random float v such that min <= v < max. Min shouldn't equal to max
     *
     * @param min the minimum value that can possibly be generated
     */
    public static float seededRandomFloat(float min, float max) {
        float result = max;
        if (min == max) {
            Gdx.app.log("SolMath", "seededRandomFloat was called with bad parameters! Min " + min + " matches max " + max + ", accepting max.");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", stackTraceElement.toString());
            }
    
            return result;
        }
        return seededRandom.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a seeded random int v such that 0 <= v < max.
     *
     * @param max the maximum number possible to be generated
     */
    public static int seededRandomInt(int max) {
        return SolRandom.seededRandomInt(0f, max);
    }

    /**
     * Returns a seeded random int v such that max*percentage <= v < max.
     *
     * @param percentage should be >= 0 and < 1
     * @param max  the maximum number possible to be generated
     */
    public static int seededRandomInt(float percentage, int max) {
        int min = (int) (max * percentage);
        if (min == max) {
            throw new AssertionError("seededRandomInt min equals max " + min);
        }
        return seededRandom.nextInt(max - min) + min;
    }

    /**
     * Returns a seeded random int v such that min <= v <= max
     *
     * @param min the minimum possible number to be generated
     * @param max the maximum possible number to be generated
     */
    public static int seededRandomInt(int min, int max) {
        return seededRandom.nextInt(max - min) + min;
    }
    
    /**
     * Returns a random float v such that -minMax <= v < minMax
     *
     * @param minMax a positive value that is used as the max while the negative value is used as the min
     */
    public static float randomFloat(float minMax) {
        return SolRandom.randomFloat(-minMax, minMax);
    }

    /**
     * Returns a random float v such that min <= v < max. Min shouldn't equal to max
     *
     * @param min the minimum possible number to be generated
     * @param max the maximum possible number to be generated
     */
    public static float randomFloat(float min, float max) {
        float result = max;
        if (min == max) {
            Gdx.app.log("SolMath", "randomFloat was called with bad parameters! Min " + min + " matches max " + max + ", accepting max.");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", stackTraceElement.toString());
            }
    
            return result;
        }
        return random.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a random int v such that 0 <= v < max.
     *
     * @param max a positive value
     */
    public static int randomInt(int max) {
        return SolRandom.randomInt(0f, max);
    }

    /**
     * Returns a random int v such that max*percentage <= v < max.
     *
     * @param percentage should be >= 0 and < 1
     * @param max  a positive value
     */
    public static int randomInt(float percentage, int max) {
        int min = (int) (max * percentage);
        if (min == max) {
            throw new AssertionError("randomInt min equals max " + min);
        }
        return random.nextInt(max - min) + min;
    }

    /**
     * Returns a random int v such that min <= v <= max
     */
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * @return a random element of a list
     */
    public static <T> T randomElement(List<T> list) {
        int idx = randomInt(list.size());
        return list.get(idx);
    }
    
    /**
     * @return a seeded random element of a list
     */
    public static <T> T seededRandomElement(List<T> list) {
        int idx = seededRandomInt(list.size());
        return list.get(idx);
    }

    public static void setSeed(long seed) {
        SolRandom.seed = seed; 
        seededRandom = new Random(seed);
    }

    /**
     * Gets the seed currently in use
     * @return The seed from seededRandom
     */
    public static long getSeed() {
        return seed;
    }

    /**
     * generates a random number between 0 and 1 and returns true if it is less than v, false otherwise
     */
    public static boolean test(float v) {
        return randomFloat(0, 1) < v;
    }
    
    /**
     * generates a seeded random number between 0 and 1 and returns true if it is less than v, false otherwise
     */
    public static boolean seededTest(float v) {
        return seededRandomFloat(0, 1) < v;
    }

}
