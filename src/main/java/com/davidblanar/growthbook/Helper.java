package com.davidblanar.growthbook;

import java.util.ArrayList;

public class Helper {
    private static final FNV fnv = new FNV();

    public static float hash(String str) {
        var bytes = str.getBytes();
        var n = fnv.fnv1a_32(bytes);
        return (n.floatValue() % 1000) / 1000;
    }

    public static boolean inNamespace(String userId, Namespace namespace) {
        var n = hash(userId + "__" + namespace.id);
        return n >= namespace.range[1] && n < namespace.range[2];
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
        if (coverage < 0) coverage = 0;
        if (coverage > 1) coverage = 1;
        if (weights.length != numVariations) {
            weights = getEqualWeights(numVariations);
        }
        var sum = sumArray(weights);
        if (sum < 0.99 || sum > 1.01) {
            weights = getEqualWeights(numVariations);
        }
        float cumulative = 0;
        float start;
        float[][] ranges = new float[weights.length][2];
        var i = 0;
        for (var w: weights) {
            start = cumulative;
            cumulative += w;
            ranges[i] = new float[]{start, start + coverage * w};
            i++;
        }
        return ranges;
    }

    public static int chooseVariation(float n, float[][] ranges) {
        for (int i = 0; i < ranges.length; i++) {
            if (n >= ranges[i][0] && n < ranges[i][1]) {
                return i;
            }
        }
        return -1;
    }

    private static float sumArray(float[] array) {
        int sum = 0;
        for (var value : array) {
            sum += value;
        }
        return sum;
    }
}
