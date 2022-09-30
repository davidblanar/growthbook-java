package io.github.davidblanar.growthbook;

import java.util.List;

public class GBExperiment {
    public String key;
    public List<Object> variations;
    public List<Float> weights;
    public boolean active = true;
    public Float coverage;
    public GBCondition condition;
    public Object[] namespace;
    public Integer force;
    public String hashAttribute;
}
