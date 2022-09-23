package com.davidblanar.growthbook;

import com.google.gson.JsonElement;

import java.util.List;

public class FeatureRule {
    public JsonElement condition;
    public Float coverage;
    public Object force;
    public List<Object> variations;
    public String key;
    public List<Float> weights;
    public Namespace namespace;
    public String hashAttribute;
}
