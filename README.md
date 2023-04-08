# Tzeva Adom API
Simple Java API that listens to `Pikud Haoref's API` and notifies registered listeners once a Tzeva Adom takes place.


## How to use
All you need is a `TzevaAdomNotifier` object, let's create one that sends a message to the console when it's Tzeva Adom:
```java
TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
        .every(Duration.ofSeconds(3)) //amount of delay between requests
        .requestFrom(new PHOAlertSource())
        .onFailedRequest(exception -> System.err.println("Failed to send a request to Pikud Ha'oref..."))
        .onTzevaAdom(alert -> System.out.println("Tzeva Adom at: " + alert.getCity()))
        .build();
        
notifier.listen(); //throws InterruptedException due to the sleeping between requests
```


## How to import
Maven(Jitpack) Repository:
```xml
<repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
        <groupId>com.github.DavidTheExplorer</groupId>
        <artifactId>Tzeva-Adom-API</artifactId>
        <version>master-SNAPSHOT</version>
</dependency>
```


## Customization
If your alerts come from anywhere else, you need to either implement `AlertSource` or inherit `JSONAlertSource` for JSON APIs.
