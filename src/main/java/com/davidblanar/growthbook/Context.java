package com.davidblanar.growthbook;

import java.util.Map;

public class Context {
    public boolean enabled;
    public Map<String, Object> attributes;
    public String url;
    public Map<String, Feature> features;
    public Map<String, Integer> forcedVariations;
    public boolean qaMode;
    public Runnable trackingCallback;

    public Context(
        boolean enabled,
        Map<String, Object> attributes,
        String url,
        Map<String, Feature> features,
        Map<String, Integer> forcedVariations,
        boolean qaMode,
        Runnable trackingCallback
    ) {
        this.enabled = enabled;
        this.attributes = attributes;
        this.url = url;
        this.features = features;
        this.forcedVariations = forcedVariations;
        this.qaMode = qaMode;
        this.trackingCallback = trackingCallback;
    }
}
