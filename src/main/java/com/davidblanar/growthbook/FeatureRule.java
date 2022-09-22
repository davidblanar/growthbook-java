package com.davidblanar.growthbook;

import java.util.List;
import java.util.Map;

public class FeatureRule {
    public Map<String, Object> condition;
    public float coverage;
    public Object force;
    public List<Object> variations;
    public String key;
    public List<Float> weights;
    public Namespace namespace;
    public String hashAttribute;
}
