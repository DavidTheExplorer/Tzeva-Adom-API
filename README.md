# Tzeva Adom API
Java API that listens to Pikud Ha Oref's API and notifies your program once a Tzeva Adom takes place.\

It works by requesting the most recent alert from Pikud Ha Oref's API every constant amount of time, and then comparing it against the previous sent one(time & city).\
If the 2 don't equal - your program is immediately notified.

## Requirements
Java 8. We suffered enough from dinosaurs.

## How to use?
All you need is an instance of `TzevaAdomNotifier` which is fluently created.\
Let's create one that checks Pikud Ha Oref's API every 3 seconds, and sends a message to the console if it's Tzeva Adom:
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
