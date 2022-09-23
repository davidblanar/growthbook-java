package com.davidblanar.growthbook;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
        var hashTests = casesObj.get("hash").getAsJsonArray();
        for (var t: hashTests) {
            var test = t.getAsJsonArray();
            var hash = test.get(0).getAsString();
            var expected = test.get(1).getAsDouble();
            var actual = Helper.hash(hash);
            Assertions.assertEquals(expected, actual);
        }
    }
}
