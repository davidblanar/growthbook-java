package com.davidblanar.growthbook;

public class Context {
    public boolean enabled;
    public Attributes attributes;
    public String url;
    public Features features;
    public ForcedVariations forcedVariations;
    public boolean qaMode;
    public Runnable trackingCallback;

    public Context(
        boolean enabled,
        Attributes attributes,
        String url,
        Features features,
        ForcedVariations forcedVariations,
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
