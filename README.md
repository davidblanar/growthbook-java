# GrowthBook Java SDK

## Installation
Maven
```xml
<dependency>
  <groupId>io.github.davidblanar</groupId>
  <artifactId>growthbook</artifactId>
  <version>0.1.0</version>
</dependency>
```
Gradle
```groovy
implementation 'io.github.davidblanar:growthbook:0.1.0'
```

## Usage

```java
import io.github.davidblanar.growthbook.*;

public class Main {
    public static void main(String[] args) {
        // {
        //   "status":200,
        //   "features":{
        //      "my_feature":{
        //         "defaultValue":"default"
        //      },
        //      "my_feature_2":{
        //         "defaultValue":"default"
        //      }
        //   }
        // }
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
    }

    private static String fakeApiCall() {
        return "{\"status\":200,\"features\":{\"my_feature\":{\"defaultValue\":\"default\"},\"my_feature_2\":{\"defaultValue\":\"default\"}}}";
    }
}
```