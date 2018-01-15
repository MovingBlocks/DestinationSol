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
     * Returns a seeded random float v such that -minMax <= v && v < minMax
     *
     * @param minMax a positive value
     */
    public static float seededRandomFloat(float minMax) {
        return SolRandom.seededRandomFloat(-minMax, minMax);
    }

    /**
     * Returns a seeded random float v such that min <= v && v < max. Min shouldn't equal to max
     */
    public static float seededRandomFloat(float min, float max) {
        float result = max;
        if (min == max) {
            Gdx.app.log("SolMath", "rnd was called with bad parameters! Min " + min + " matches max " + max + ", accepting max.");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", ste.toString());
            }
    
            return result;
        }
        return seededRandom.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a seeded random int v such that 0 <= v && v < max.
     *
     * @param max a positive value
     */
    public static int seededRandomInt(int max) {
        return SolRandom.seededRandomInt(0f, max);
    }

    /**
     * Returns a seeded random int v such that max*perc <= v && v < max.
     *
     * @param perc should be >= 0 and < 1
     * @param max  a positive value
     */
    public static int seededRandomInt(float perc, int max) {
        int min = (int) (max * perc);
        if (min == max) {
            throw new AssertionError("intRnd min equals max " + min);
        }
        return seededRandom.nextInt(max - min) + min;
    }

    /**
     * Returns a seeded random int v such that min <= v && v <= max
     */
    public static int seededRandomInt(int min, int max) {
        return seededRandom.nextInt(max - min) + min;
    }
    
    /**
     * Returns a random float v such that -minMax <= v && v < minMax
     *
     * @param minMax a positive value
     */
    public static float randomFloat(float minMax) {
        return SolRandom.randomFloat(-minMax, minMax);
    }

    /**
     * Returns a random float v such that min <= v && v < max. Min shouldn't equal to max
     */
    public static float randomFloat(float min, float max) {
        float result = max;
        if (min == max) {
            Gdx.app.log("SolMath", "rnd was called with bad parameters! Min " + min + " matches max " + max + ", accepting max.");
            Gdx.app.log("SolMath", "Please review appropriate code in the stack dump:");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                Gdx.app.log("SolMath", ste.toString());
            }
    
            return result;
        }
        return random.nextFloat() * (max - min) + min;
    }

    /**
     * Returns a random int v such that 0 <= v && v < max.
     *
     * @param max a positive value
     */
    public static int randomInt(int max) {
        return SolRandom.randomInt(0f, max);
    }

    /**
     * Returns a random int v such that max*perc <= v && v < max.
     *
     * @param perc should be >= 0 and < 1
     * @param max  a positive value
     */
    public static int randomInt(float perc, int max) {
        int min = (int) (max * perc);
        if (min == max) {
            throw new AssertionError("intRnd min equals max " + min);
        }
        return random.nextInt(max - min) + min;
    }

    /**
     * Returns a random int v such that min <= v && v <= max
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

    public static void setSeed(long seed) {
        SolRandom.seed = seed; 
        seededRandom = new Random(seed);
    }

    public static long getSeed() {
        return seed;
    }

}
