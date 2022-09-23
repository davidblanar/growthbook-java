package com.davidblanar.growthbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DecimalFormat;

public class HelperTest {
    private final JsonObject casesObj = CasesHelper.getCases();

    public HelperTest() throws IOException {}

    @Test
    public void testHash() {
        var tests = casesObj.get("hash").getAsJsonArray();
        for (var t: tests) {
            var test = t.getAsJsonArray();
            var hash = test.get(0).getAsString();
            var expected = test.get(1).getAsDouble();
            var actual = Helper.hash(hash);
            System.out.println("Testing hash " + hash);
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetBucketRange() {
        var tests = casesObj.get("getBucketRange").getAsJsonArray();
        for (var t: tests) {
            var df = new DecimalFormat("0.000000");
            var test = t.getAsJsonArray();
            var title = test.get(0).getAsString();
            var numVariations = test.get(1).getAsJsonArray().get(0).getAsInt();
            var coverage = test.get(1).getAsJsonArray().get(1).getAsFloat();
            var weightsArray = test.get(1).getAsJsonArray().get(2);
            float[] weights = null;
            if (!weightsArray.isJsonNull()) {
                var w = weightsArray.getAsJsonArray();
                weights = new float[w.size()];
                for (var i = 0; i < weights.length; i++) {
                    weights[i] = w.get(i).getAsFloat();
                }
            }
            var expected = test.get(2).getAsJsonArray();
            System.out.println("Testing getBucketRange " + title);
            var ranges = Helper.getBucketRanges(numVariations, coverage, weights);
            Assertions.assertEquals(expected.size(), ranges.length);
            for (var i = 0; i < ranges.length; i++) {
                var inner = ranges[i];
                var expectedInner = expected.get(i).getAsJsonArray();
                Assertions.assertEquals(expectedInner.size(), inner.length);
                for (var j = 0; j < inner.length; j++) {
                    var formattedExpected = df.format(expectedInner.get(j).getAsFloat());
                    var formattedActual = df.format(inner[j]);
                    Assertions.assertEquals(formattedExpected, formattedActual);
                }
            }
        }
    }

    @Test
    public void testChooseVariation() {
        var tests = casesObj.get("chooseVariation").getAsJsonArray();
        for (var t: tests) {
            var test = t.getAsJsonArray();
            var title = test.get(0).getAsString();
            var n = test.get(1).getAsFloat();
            var jsonRanges = test.get(2).getAsJsonArray();
            float[][] ranges = new float[jsonRanges.size()][2];
            for (var i = 0; i < jsonRanges.size(); i++) {
                var inner = jsonRanges.get(i).getAsJsonArray();
                for (var j = 0; j < inner.size(); j++) {
                    ranges[i][j] = inner.get(j).getAsFloat();
                }
            }
            System.out.println("Testing chooseVariation " + title);
            var expected = test.get(3).getAsInt();
            var actual = Helper.chooseVariation(n, ranges);
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    public void testInNamespace() {
        var tests = casesObj.get("inNamespace").getAsJsonArray();
        for (var t: tests) {
            var test = t.getAsJsonArray();
            var title = test.get(0).getAsString();
            var userId = test.get(1).getAsString();
            var namespace = test.get(2).getAsJsonArray();
            var ns = new Namespace();
            ns.id = namespace.get(0).getAsString();
            ns.range = new float[]{namespace.get(1).getAsFloat(), namespace.get(2).getAsFloat()};
            System.out.println("Testing inNamespace " + title);
            var expected = test.get(3).getAsBoolean();
            var actual = Helper.inNamespace(userId, ns);
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetEqualWeights() {
        var tests = casesObj.get("getEqualWeights").getAsJsonArray();
        for (var t: tests) {
            var test = t.getAsJsonArray();
            var numVariations = test.get(0).getAsInt();
            System.out.println("Testing getEqualWeights " + numVariations);
            var jsonWeights = test.get(1).getAsJsonArray();
            var actual = Helper.getEqualWeights(numVariations);
            Assertions.assertEquals(jsonWeights.size(), actual.length);
            for (var i = 0; i < actual.length; i++) {
                Assertions.assertEquals(jsonWeights.get(i).getAsFloat(), actual[i]);
            }
        }
    }
}
