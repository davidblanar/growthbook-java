package com.davidblanar.growthbook;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class HelperTest {
    private final String cases = this.getCases();

    public HelperTest() throws IOException {}

    private String getCases() throws IOException {
        var path = this.getClass().getClassLoader().getResource("cases.json").getPath();
        return Files.readString(new File(path).toPath());
    };

    @Test
    public void testHash() {
        var casesObj = JsonParser.parseString(cases).getAsJsonObject();
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
        var casesObj = JsonParser.parseString(cases).getAsJsonObject();
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
            System.out.println("Testing bucket range " + title);
            var ranges = Helper.getBucketRanges(numVariations, coverage, weights);
            for (var i = 0; i < ranges.length; i++) {
                var inner = ranges[i];
                var expectedInner = expected.get(i).getAsJsonArray();
                for (var j = 0; j < inner.length; j++) {
                    var formattedExpected = df.format(expectedInner.get(j).getAsFloat());
                    var formattedActual = df.format(inner[j]);
                    Assertions.assertEquals(formattedExpected, formattedActual);
                }
            }
        }
    }
}
