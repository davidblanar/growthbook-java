package com.davidblanar.growthbook;

import com.google.gson.Gson;

public class GrowthBook {
    private final Context context;
    private final Gson gson = new Gson();

    public GrowthBook(Context context) {
        this.context = context;
    }

    public Attributes getAttributes() {
        return (Attributes) this.context.attributes.clone();
    }

    public void setAttributes(Attributes attributes) {
        this.context.attributes = attributes;
    }

    public Features getFeatures() {
        return (Features) this.context.features.clone();
    }

    public void setFeatures(Features features) {
        this.context.features = features;
    }

    public ForcedVariations getForcedVariations() {
        return (ForcedVariations) this.context.forcedVariations.clone();
    }

    public void setForcedVariations(ForcedVariations forcedVariations) {
        this.context.forcedVariations = forcedVariations;
    }

    public String getUrl() {
        return this.context.url;
    }

    public void setUrl(String url) {
        this.context.url = url;
    }

    public boolean getEnabled() {
        return this.context.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.context.enabled = enabled;
    }

    private FeatureResult getFeatureResult(Object value, String source, Experiment experiment, ExperimentResult experimentResult) {
        var isTruthy = isTruthy(value);
        if (value instanceof Float || value instanceof Double) {
            value = ((Number) value).intValue();
        }
        var featureResult = new FeatureResult();
        featureResult.value = value;
        featureResult.on = isTruthy;
        featureResult.off = !isTruthy;
        featureResult.experiment = experiment;
        featureResult.experimentResult = experimentResult;
        featureResult.source = source;
        return featureResult;
    }

    private boolean isTruthy(Object value) {
        var isTruthy = true;
        if (value == null) {
            isTruthy = false;
        } else if (value instanceof Integer || value instanceof Float || value instanceof Double) {
            isTruthy = ((Number) value).intValue() != 0;
        } else if (value instanceof String) {
            isTruthy = !value.equals("");
        } else if (value instanceof Boolean) {
            isTruthy = (boolean) value;
        }
        return isTruthy;
    }

    private ExperimentResult getExperimentResult(Experiment experiment, Integer variationIndex, boolean hashUsed, String featureId, boolean inExperiment) {
        if (variationIndex == null) {
            variationIndex = -1;
        }
        if (variationIndex < 0 || experiment.variations == null || variationIndex >= experiment.variations.size()) {
            variationIndex = 0;
        }
        var hashAttribute = experiment.hashAttribute != null ? experiment.hashAttribute : "id";
        var hashValueFromContext = (String) context.attributes.get(hashAttribute);
        var hashValue = hashValueFromContext != null ? hashValueFromContext : "";
        var experimentResult = new ExperimentResult();
        experimentResult.featureId = featureId;
        experimentResult.inExperiment = inExperiment;
        experimentResult.hashUsed = hashUsed;
        experimentResult.variationId = variationIndex;
        experimentResult.value = experiment.variations != null ? experiment.variations.get(variationIndex) : null;
        experimentResult.hashAttribute = hashAttribute;
        experimentResult.hashValue = hashValue;
        return experimentResult;
    }

    public FeatureResult evalFeature(String key) {
        if (!context.features.containsKey(key)) {
            return getFeatureResult(null, FeatureResult.UNKNOWN_FEATURE, null, null);
        }
        var feature = context.features.get(key);
        if (feature.rules != null) {
            for (var rule: feature.rules) {
                if (rule.condition != null) {
                    if (!ConditionEvaluator.evalCondition(gson.toJsonTree(context.attributes), rule.condition)) {
                        continue;
                    }
                }
                if (rule.force != null) {
                    if (rule.coverage != null) {
                        var hashAttribute = rule.hashAttribute != null ? rule.hashAttribute : "id";
                        var hashValue = context.attributes.get(hashAttribute);
                        if (hashValue == null) {
                            continue;
                        }
                        var n = Helper.hash(hashValue + key);
                        if (n > rule.coverage) {
                            continue;
                        }
                    }
                    return getFeatureResult(rule.force, FeatureResult.FORCE, null, null);
                }
                var experiment = new Experiment();
                experiment.key = rule.key != null ? rule.key : key;
                experiment.variations = rule.variations;
                if (rule.coverage != null) {
                    experiment.coverage = rule.coverage;
                }
                if (rule.weights != null) {
                    experiment.weights = rule.weights;
                }
                if (rule.hashAttribute != null) {
                    experiment.hashAttribute = rule.hashAttribute;
                }
                if (rule.namespace != null) {
                    experiment.namespace = rule.namespace;
                }
                var result = run(experiment, key);
                if (!result.inExperiment) {
                    continue;
                }
                return getFeatureResult(result.value, FeatureResult.EXPERIMENT, experiment, result);
            }
        }
        return getFeatureResult(feature.defaultValue != null ? feature.defaultValue : null, FeatureResult.DEFAULT_VALUE, null, null);
    }

    public ExperimentResult run(Experiment experiment, String featureId) {
        if (experiment.variations == null || experiment.variations.size() < 2 || !context.enabled || !experiment.active) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        if (context.forcedVariations.containsKey(experiment.key)) {
            return getExperimentResult(experiment, context.forcedVariations.get(experiment.key), false, featureId, false);
        }
        var hashAttribute = experiment.hashAttribute != null ? experiment.hashAttribute : "id";
        var hashValueFromContext = (String) context.attributes.get(hashAttribute);
        var hashValue = hashValueFromContext != null ? hashValueFromContext : "";
        if (hashValue.equals("")) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        if (experiment.namespace != null && !Helper.inNamespace(hashValue, experiment.namespace)) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        if (experiment.condition != null && !ConditionEvaluator.evalCondition(gson.toJsonTree(context.attributes), gson.toJsonTree(experiment.condition))) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        float[] weights;
        if (experiment.weights == null) {
            weights = new float[]{};
        } else {
            weights = new float[experiment.weights.size()];
            for (var i = 0; i < weights.length; i++) {
                weights[i] = experiment.weights.get(i);
            }
        }
        var ranges = Helper.getBucketRanges(
                experiment.variations.size(),
                experiment.coverage != null ? experiment.coverage : 1,
                weights
        );
        var n = Helper.hash(hashValue + experiment.key);
        var assigned = Helper.chooseVariation(n, ranges);
        if (assigned == -1) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        if (experiment.force != null) {
            return getExperimentResult(experiment, experiment.force, false, featureId, false);
        }
        if (context.qaMode) {
            return getExperimentResult(experiment, 0, false, featureId, false);
        }
        var result = getExperimentResult(experiment, assigned, true, featureId, true);
        // TODO fire context tracking callback if not null
        return result;
    }

    public boolean isOn(String key) {
        return evalFeature(key).on;
    }

    public boolean isOff(String key) {
        return evalFeature(key).off;
    }

    public Object getFeatureValue(String key, Object fallback) {
        var value = evalFeature(key).value;
        return value == null ? fallback : value;
    }
}
