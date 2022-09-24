package com.davidblanar.growthbook;

import java.math.BigInteger;

public class Helper {
    private static final BigInteger INIT32 = new BigInteger("811c9dc5", 16);
    private static final BigInteger PRIME32 = new BigInteger("01000193", 16);
    private static final BigInteger MOD32 = new BigInteger("2").pow(32);

    private static double fnv1a32(String str) {
        BigInteger hash = INIT32;
        for (int i = 0; i < str.length(); i++) {
            hash = hash.xor(new BigInteger(String.valueOf(str.charAt(i) & 0xff)));
            hash = hash.multiply(PRIME32).mod(MOD32);
        }
        return hash.doubleValue();
    }

    public static double hash(String str) {
        var n = fnv1a32(str);
        return (n % 1000) / 1000;
    }

    public static boolean inNamespace(String userId, Object[] namespace) {
        var n = hash(userId + "__" + namespace[0]);
        var start = (double) namespace[1];
        var end = (double) namespace[2];
        return n >= start && n < end;
    }

    public static float[] getEqualWeights(int numVariations) {
        if (numVariations < 1) {
            return new float[]{};
        }
        var weights = new float[numVariations];
        for (var i = 0; i < numVariations; i++) {
            weights[i] = (float) (1.0 / numVariations);
        }
        return weights;
    }

    public static float[][] getBucketRanges(int numVariations, float coverage, float[] weights) {
        if (coverage < 0.0) coverage = 0.0F;
        if (coverage > 1.0) coverage = 1.0F;
        if (weights == null || weights.length != numVariations) {
            weights = getEqualWeights(numVariations);
        }
        var sum = sumArray(weights);
        if (sum < 0.99 || sum > 1.01) {
            weights = getEqualWeights(numVariations);
        }
        float cumulative = 0;
        float[][] ranges = new float[weights.length][2];
        var i = 0;

        for (var w: weights) {
            var start = cumulative;
            cumulative += w;
            ranges[i] = new float[]{start, start + coverage * w};
            i++;
        }
        return ranges;
    }

    public static int chooseVariation(double n, float[][] ranges) {
        for (int i = 0; i < ranges.length; i++) {
            if (n >= ranges[i][0] && n < ranges[i][1]) {
                return i;
            }
        }
        return -1;
    }

    private static float sumArray(float[] array) {
        float sum = 0;
        for (var value : array) {
            sum += value;
        }
        return sum;
    }
}
