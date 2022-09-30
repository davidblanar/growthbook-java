package com.github.davidblanar.growthbook;

public class GBContext {
    public boolean enabled;
    public GBAttributes attributes;
    public String url;
    public GBFeatures features;
    public GBForcedVariations forcedVariations;
    public boolean qaMode;
    public GBTrackingCallback trackingCallback;

    public GBContext(
        boolean enabled,
        GBAttributes attributes,
        String url,
        GBFeatures features,
        GBForcedVariations forcedVariations,
        boolean qaMode,
        GBTrackingCallback trackingCallback
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
