package com.davidblanar.growthbook;

public class FeatureResult {
    public static final String UNKNOWN_FEATURE = "unknownFeature";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String FORCE = "force";
    public static final String EXPERIMENT = "experiment";

    public Object value;
    public boolean on;
    public boolean off;
    // one of "unknownFeature", "defaultValue", "force", "experiment"
    public String source;
    public Experiment experiment;
    public ExperimentResult experimentResult;
}
