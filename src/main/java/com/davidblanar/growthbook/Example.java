package com.davidblanar.growthbook;

public class Example {
    public static void main(String[] args) {
        var jsonString = fakeApiCall();
        var features = Helper.parseFeaturesFromGBResponse(jsonString);
        var attributes = new Attributes();
        var forcedVariations = new ForcedVariations();
        var trackingCallback = new TrackingCallback() {
            @Override
            public void run(Experiment experiment, ExperimentResult result) {
                // call tracking code
            }
        };
        var context = new Context(
            true, // enabled
            attributes,
            "", // url
            features,
            forcedVariations,
            false, // qaMode
            trackingCallback
        );
        var gb = new GrowthBook(context);
        System.out.println(gb.evalFeature("my_feature").value); // "default"
        System.out.println(gb.evalFeature("doesnt_exist").value); // null
    }

    private static String fakeApiCall() {
        return "{\"status\":200,\"features\":{\"my_feature\":{\"defaultValue\":\"default\"},\"my_feature_2\":{\"defaultValue\":\"default\"}}}";
    }
}
