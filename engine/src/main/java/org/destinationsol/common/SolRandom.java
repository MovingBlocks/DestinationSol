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
     * @param minMax The upper bound (exclusive) for magnitude of the generated number. Must be positive.
     */
    public static float seededRandomFloat(float minMax) {
        return SolRandom.seededRandomFloat(-minMax, minMax);
    }

    /**
     * Returns a seeded random float v such that min <= v < max. Min shouldn't equal to max
     *
     * @param min The lower bound (inclusive) for magnitude of the generated number.
     * @param max The upper bound (exclusive) for magnitude of the generated number.
     */
    public static float seededRandomFloat(float min, float max) {
        if (min >= max) {
            Gdx.app.log("SolMath", "seededRandomFloat was called with bad parameters! Min " + min + " >= max " + max + ".");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", stackTraceElement.toString());
            }
    
            return max;
        }
        return seededRandom.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a seeded random int v such that 0 <= v < max.
     *
     * @param max The upper bound (exclusive) for magnitude of the generated number. Must be positive.
     */
    public static int seededRandomInt(int max) {
        return SolRandom.seededRandomInt(0, max);
    }

    /**
     * Returns a seeded random int v such that max*percentage <= v < max.
     *
     * @param percentage Ratio of lower bound to upper bound.
     * @param max The upper bound (exclusive) for magnitude of the generated number.
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
     * @param min The lower bound (inclusive) for magnitude of the generated number.
     * @param max The upper bound (exclusive) for magnitude of the generated number.
     */
    public static int seededRandomInt(int min, int max) {
        return seededRandom.nextInt(max - min) + min;
    }
    
    /**
     * Returns a random float v such that -minMax <= v < minMax
     *
     * @param minMax The upper bound (exclusive) for magnitude of the generated number. Must be positive.
     */
    public static float randomFloat(float minMax) {
        return SolRandom.randomFloat(-minMax, minMax);
    }

    /**
     * Returns a random float v such that min <= v < max. Min shouldn't equal to max
     *
     * @param min The lower bound (inclusive) for magnitude of the generated number.
     * @param max The upper bound (exclusive) for magnitude of the generated number.
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
     * @param max The upper bound (exclusive) for magnitude of the generated number. Must be positive.
     */
    public static int randomInt(int max) {
        return SolRandom.randomInt(0, max);
    }

    /**
     * Returns a random int v such that max*percentage <= v < max.
     *
     * @param percentage The ratio of lower bound to upper bound.
     * @param max The upper bound (exclusive) for magnitude of the generated number. Must be positive.
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
     * Selects a random element from a list.
     *
     * @param list The list to select the element from
     *
     * @return The randomly selected element
     */
    public static <T> T randomElement(List<T> list) {
        int index = randomInt(list.size());
        return list.get(index);
    }

    /**
     * Selects a seeded random element from a list.
     *
     * @param list The list to select the element from
     *
     * @return The seeded randomly selected element
     */
    public static <T> T seededRandomElement(List<T> list) {
        int index = seededRandomInt(list.size());
        return list.get(index);
    }

    public static void setSeed(long seed) {
        SolRandom.seed = seed; 
        seededRandom = new Random(seed);
    }

    /**
     * Gets the seed currently in use
     *
     * @return The seed from currently in use seeded Random object
     */
    public static long getSeed() {
        return seed;
    }

    /**
     * Returns a random boolean value with unequal probabilities.
     *
     * @param value the probability of returning true.
     */
    public static boolean test(float value) {
        return randomFloat(0, 1) < value;
    }

    /**
     * Returns a seeded random boolean value with unequal probabilities.
     *
     * @param value the probability of returning true.
     */
    public static boolean seededTest(float value) {
        return seededRandomFloat(0, 1) < value;
    }

}
