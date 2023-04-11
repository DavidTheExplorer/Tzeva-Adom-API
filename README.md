# Tzeva Adom API
Simple Java API that listens to `Pikud Haoref's API` and notifies registered listeners once a Tzeva Adom takes place.


## How to use
Let's create a notifier object that logs a message when it's Tzeva Adom:
```java
TzevaAdomNotifier
        .requestFromPikudHaoref()
        .every(Duration.ofSeconds(3)) //amount of delay between requests
        .onFailedRequest(exception -> LOGGER.error("Failed to send a request to Pikud Ha'oref...", exception))
        .onTzevaAdom(alert -> LOGGER.info("Tzeva Adom at: " + alert.getCity()))
        .listen();
```

You can save the notifier object in order to add functionality or get data from it, by calling `build()` instead of `listen()`:
```java
TzevaAdomNotifier notifier = TzevaAdomNotifier
        // builder pattern goes here
        .build();
        
notifier.listen();
```

Adding Listeners anytime:
```java
notifier.addListener(alert -> ...);
```

Retrieving the Tzeva Adom Alerts encountered while running:
```java
TimeUnit.DAYS.sleep(1);
LOGGER.info("There were {} alerts in the last 24 hours:", notifier.getHistory().size());

//Pro Tip: TzevaAdomNotifier implements Iterable!
for(Alert alert : notifier) 
{
        LOGGER.info("Alert in {} at {}", alert.getCity(), alert.getDate());
}
```

## How to import
Maven Repository:
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
        <version>1.1.0</version>
</dependency>
```


## Customization
Relevant if Pikud Haoref's endpoint was changed or your alerts come from somewhere else.\
\
Either implement `AlertSource` or extend `JSONAlertSource` for JSON APIs, and then create your notifier like this:
```java
new TzevaAdomNotifier.Builder()
.requestFrom(new YourAlertSource())
.listen();
