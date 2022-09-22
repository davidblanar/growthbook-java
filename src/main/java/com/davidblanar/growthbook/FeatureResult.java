package com.davidblanar.growthbook;

public class FeatureResult {
    public final String UNKNOWN_FEATURE = "unknownFeature";
    public final String DEFAULT_VALUE = "defaultValue";
    public final String FORCE = "force";
    public final String EXPERIMENT = "experiment";

    public Object value;
    public boolean on;
    public boolean off;
    // one of "unknownFeature", "defaultValue", "force", "experiment"
    public String source;
    public Experiment experiment;
    public ExperimentResult experimentResult;
}
