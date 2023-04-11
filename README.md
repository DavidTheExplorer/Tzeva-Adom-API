# Tzeva Adom API
Async Java API that listens to `Pikud Ha'oref API` and notifies registered listeners as soon as a Tzeva Adom happens.


## How to use
Let's create a notifier that logs a message when it's Tzeva Adom:
```java
TzevaAdomNotifier
        .requestFromPikudHaoref()
        .every(Duration.ofSeconds(3)) //amount of delay between requests
        .onFailedRequest(exception -> LOGGER.error("Failed to request the last alert from Pikud Ha'oref", exception))
        .onTzevaAdom(alert -> LOGGER.info("Tzeva Adom at: " + alert.getCity()))
        .listen(); //async
```

You can save the notifier object by calling `build()` instead of `listen()`, in order to add functionality or get data from it:
```java
TzevaAdomNotifier notifier = TzevaAdomNotifier
        // builder pattern goes here
        .build();
        
notifier.listen();
```

Add more listeners anytime:
```java
notifier.addListener(alert -> ...);
```

Retrieve the Tzeva Adom alerts encountered while running:
```java
//run the notifier async and sleep for a day
notifier.run();
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
