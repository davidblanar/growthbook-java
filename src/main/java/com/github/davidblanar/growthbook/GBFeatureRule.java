package com.github.davidblanar.growthbook;

import com.google.gson.JsonElement;

import java.util.List;

public class GBFeatureRule {
    public JsonElement condition;
    public Float coverage;
    public Object force;
    public List<Object> variations;
    public String key;
    public List<Float> weights;
    public Object[] namespace;
    public String hashAttribute;
}
