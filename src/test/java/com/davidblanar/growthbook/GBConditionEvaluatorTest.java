package com.davidblanar.growthbook;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GBConditionEvaluatorTest {
    private final JsonObject casesObj = CasesHelper.getCases();

    public GBConditionEvaluatorTest() throws IOException {}

    @Test
    public void testEvalCondition() {
        var tests = casesObj.get("evalCondition").getAsJsonArray();
        for (var t: tests) {
            var test = t.getAsJsonArray();
            var title = test.get(0).getAsString();
            System.out.println("Testing evalCondition " + title);
            var condition = test.get(1);
            var attributes = test.get(2);
            var expected = test.get(3).getAsBoolean();
            var actual = GBConditionEvaluator.evalCondition(attributes, condition);
            Assertions.assertEquals(expected, actual);

        }
    }
}
