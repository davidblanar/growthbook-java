package com.github.davidblanar.growthbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CasesHelper {
    public static JsonObject getCases() throws IOException {
        var path = CasesHelper.class.getClassLoader().getResource("cases.json").getPath();
        var str = Files.readString(new File(path).toPath());
        return JsonParser.parseString(str).getAsJsonObject();
    }
}
