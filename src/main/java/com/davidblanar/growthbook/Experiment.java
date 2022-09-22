package com.davidblanar.growthbook;

import java.util.List;
import java.util.Map;

public class Experiment {
    public String key;
    public List<Object> variations;
    public List<Float> weights;
    public boolean active;
    public float coverage;
    public Map<String, Object>  condition;
    public Namespace namespace;
    public int force;
    public String hashAttribute;
}
