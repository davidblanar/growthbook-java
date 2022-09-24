package com.davidblanar.growthbook;

import java.util.List;

public class Experiment {
    public String key;
    public List<Object> variations;
    public List<Float> weights;
    public boolean active = true;
    public Float coverage;
    public Condition condition;
    public Object[] namespace;
    public Integer force;
    public String hashAttribute;
}
