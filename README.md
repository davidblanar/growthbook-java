# GrowthBook Java SDK

## Usage

```java
var jsonString = fakeApiCall();
var features = GBHelper.parseFeaturesFromGBResponse(jsonString);
var attributes = new GBAttributes();
var forcedVariations = new GBForcedVariations();
var trackingCallback = new GBTrackingCallback() {
    @Override
    public void run(GBExperiment experiment, GBExperimentResult result) {
        // call tracking code
    }
};
var context = new GBContext(
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
System.out.println(gb.evalFeature("my_feature").on); // true
System.out.println(gb.evalFeature("doesnt_exist").value); // null
System.out.println(gb.evalFeature("doesnt_exist").on); // false
```