# Tzeva Adom API
Simple Java API that listens to `Pikud Haoref`'s API and allows you to register a listener that gets called once a Tzeva Adom takes place.
## Requirements
Java 8 and Maven. We suffered enough from dinosaurs.

Maven:
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



## How to use?
All you need is an instance of `TzevaAdomNotifier` which is fluently created.\
Let's create one that requests an alert every 3 seconds, and sends a message to the console if it was Tzeva Adom:
```java
TzevaAdomNotifier tzevaAdomNotifier = new TzevaAdomNotifier.Builder()
        .every(Duration.ofSeconds(3))
        .requestFrom(new PHOAlertSource("https://www.oref.org.il/WarningMessages/History/AlertsHistory.json"))
        .onFailedRequest(exception -> System.err.println("Failed to send a request to Pikud Ha'oref..."))
        .ifTzevaAdom(alert ->
        {
          System.out.println("-~- Tzeva ADOM -~-");
          System.out.println("At: " + alert.getCity());
          System.out.println("Time: " + alert.getDate().toLocalTime());
          System.out.println(); 
        })
        .build();
```

## Customization
If you want to request alerts from anywhere else, you need to implement `AlertSource` and use it instead of `PHOAlertSource`.\
If your source is an API, you should extend the provided `JSONAlertSource` class instead :)

