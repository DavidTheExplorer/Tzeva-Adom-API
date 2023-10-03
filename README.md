# Tzeva Adom API
Async Java API that listens to `Pikud Ha'oref` and notifies registered listeners as soon as a Tzeva Adom happens.

## How to use
Let's create a notifier that stops a game when it's Tzeva Adom:
```java
Game game = ...;

TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
        .every(Duration.ofSeconds(3))
        .onFailedRequest(exception -> LOGGER.error("Failed to request the last alert from Pikud Ha'oref", exception))
        .onTzevaAdom(alert ->
        {
                game.stop();
                game.sendMessage("There is a Tzeva Adom in: " + alert.getCity());
        })
        .build();
	
notifier.listen(); //async
```

By saving the notifier object, you can gather information while your program is running.\
Here is an example:
```java
notifier.listen();

//sleep for a day
TimeUnit.DAYS.sleep(1);

Set<Alert> history = notifier.getHistory();

LOGGER.info("There were {} alerts in the last 24 hours:", history.size());

for(Alert alert : history) 
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
        <version>1.4.0</version>
</dependency>
```


## Customization
Relevant if the endpoint of Pikud Ha'oref was changed or your alerts come from somewhere else.\
\
Either implement `AlertSource` or extend `JSONAlertSource` for JSON APIs, and then create your notifier like this:
```java
new TzevaAdomNotifier.Builder()
.requestFrom(new YourAlertSource())
