package com.davidblanar.growthbook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GrowthBookTest {
    private final JsonObject casesObj = CasesHelper.getCases();
    private final Gson gson = new Gson();

    public GrowthBookTest() throws IOException {}

    private Context mockContext(Attributes attributes, Features features, ForcedVariations forcedVariations) {
        return new Context(
                true,
                attributes,
                "",
                features,
                forcedVariations,
                false,
                null
        );
    }

    @Test
    public void testFeature() {
        var tests = casesObj.get("feature").getAsJsonArray();
        for (var t : tests) {
            var test = t.getAsJsonArray();
            var title = test.get(0).getAsString();
            System.out.println("Testing feature " + title);
            var featuresJson = test.get(1).getAsJsonObject();
            var features = new Features();
            if (featuresJson.has("features")) {
                features = gson.fromJson(featuresJson.get("features"), Features.class);
            }
            var attributes = new Attributes();
            if (featuresJson.has("attributes")) {
                attributes = gson.fromJson(featuresJson.get("attributes"), Attributes.class);
            }
            var forcedVariations = new ForcedVariations();
            if (featuresJson.has("forcedVariations")) {
                forcedVariations = gson.fromJson(featuresJson.get("forcedVariations"), ForcedVariations.class);
            }
            var key = test.get(2).getAsString();
            var expected = test.get(3).getAsJsonObject();
            var context = mockContext(attributes, features, forcedVariations);
            var gb = new GrowthBook(context);
            var actual = gb.evalFeature(key);
            Assertions.assertEquals(expected.get("source").getAsString(), actual.source);
            Assertions.assertEquals(expected.get("on").getAsBoolean(), actual.on);
            Assertions.assertEquals(expected.get("off").getAsBoolean(), actual.off);
            var value = expected.get("value");
            if (value.isJsonNull()) {
                Assertions.assertNull(actual.value);
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                Assertions.assertEquals(value.getAsInt(), actual.value);
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                Assertions.assertEquals(value.getAsString(), actual.value);
            } else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                Assertions.assertEquals(value.getAsBoolean(), actual.value);
            }
            if (expected.has("experiment")) {
                var experiment = expected.get("experiment").getAsJsonObject();
                var experimentKey = experiment.get("key").getAsString();
                var variations = experiment.get("variations").getAsJsonArray();
                Assertions.assertEquals(experimentKey, actual.experiment.key);
                Assertions.assertEquals(variations.size(), actual.experiment.variations.size());
                for (var i = 0; i < variations.size(); i++) {
                    var expectedVariation = variations.get(i).getAsJsonPrimitive();
                    if (expectedVariation.isBoolean()) {
                        Assertions.assertEquals(expectedVariation.getAsBoolean(), actual.experiment.variations.get(i));
                    }
                    if (expectedVariation.isNumber()) {
                        Assertions.assertEquals(expectedVariation.getAsInt(), actual.experiment.variations.get(i));
                    }
                }
            } else {
                Assertions.assertNull(actual.experiment);
            }
            if (expected.has("experimentResult")) {
                var experimentResult = expected.get("experimentResult").getAsJsonObject();
                var featureId = experimentResult.get("featureId").getAsString();
                Assertions.assertEquals(featureId, actual.experimentResult.featureId);
                var experimentResultValue = experimentResult.get("value");
                if (experimentResultValue.isJsonNull()) {
                    Assertions.assertNull(actual.experimentResult.value);
                } else if (experimentResultValue.isJsonPrimitive() && experimentResultValue.getAsJsonPrimitive().isNumber()) {
                    Assertions.assertEquals(experimentResultValue.getAsInt(), actual.experimentResult.value);
                } else if (experimentResultValue.isJsonPrimitive() && experimentResultValue.getAsJsonPrimitive().isString()) {
                    Assertions.assertEquals(experimentResultValue.getAsString(), actual.experimentResult.value);
                } else if (experimentResultValue.isJsonPrimitive() && experimentResultValue.getAsJsonPrimitive().isBoolean()) {
                    Assertions.assertEquals(experimentResultValue.getAsBoolean(), actual.experimentResult.value);
                }
                var variationId = experimentResult.get("variationId").getAsInt();
                Assertions.assertEquals(variationId, actual.experimentResult.variationId);
                var inExperiment = experimentResult.get("inExperiment").getAsBoolean();
                Assertions.assertEquals(inExperiment, actual.experimentResult.inExperiment);
                var hashUsed = experimentResult.get("hashUsed").getAsBoolean();
                Assertions.assertEquals(hashUsed, actual.experimentResult.hashUsed);
                var hashAttribute = experimentResult.get("hashAttribute").getAsString();
                Assertions.assertEquals(hashAttribute, actual.experimentResult.hashAttribute);
                var hashValue = experimentResult.get("hashValue").getAsString();
                Assertions.assertEquals(hashValue, actual.experimentResult.hashValue);
            } else {
                Assertions.assertNull(actual.experimentResult);
            }
        }
    }
}
