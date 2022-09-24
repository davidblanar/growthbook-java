package com.davidblanar.growthbook;

public class GBFeatureResult {
    public static final String UNKNOWN_FEATURE = "unknownFeature";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String FORCE = "force";
    public static final String EXPERIMENT = "experiment";

    public Object value;
    public boolean on;
    public boolean off;
    // one of "unknownFeature", "defaultValue", "force", "experiment"
    public String source;
    public GBExperiment experiment;
    public GBExperimentResult experimentResult;
}
