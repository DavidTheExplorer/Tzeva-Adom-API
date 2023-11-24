# Tzeva Adom API
Java API that communicates with `Pikud Ha'oref` and notifies registered listeners as soon as a Tzeva Adom happens.\
Integrating it in projects(games, etc) used by many israelis, **increases** the chances of saving lives.

## How to use
Let's create a notifier that is responsible of stopping your addicting game:
```java
Game game = ...;

TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
        .onFailedRequest(exception -> LOGGER.error("Failed to request the last alert from Pikud Ha'oref", exception))
        .onTzevaAdom(alert ->
        {
                game.stop();
                game.sendMessage("There is a Tzeva Adom in: " + alert.getRegion());
        })
        .build();
	
notifier.listenAsync();
```
\
By saving the notifier object, you can gather information while your program is running:
```java
notifier.listenAsync();

//sleep for a day
TimeUnit.DAYS.sleep(1);

//print all the alerts from the last day
for(Alert alert : notifier.getHistory()) 
{
        LOGGER.warn("Alert at {} was in {}", alert.getRegion(), alert.getDate());
}

//check a specific region's alerts
List<Alert> telAvivAlerts = notifier.getHistory().ofRegion("תל אביב");
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
        <version>1.6.0</version>
</dependency>
```


## Builder Options
Everything in the library is customizable.
-  If the endpoint of Pikud Ha'oref was changed or your alerts come from somewhere else:\
   Implement `AlertSource` and then create your notifier like this:
   ```java
   new TzevaAdomNotifier.Builder()
   .requestFrom(new YourAlertSource())
   ```
- If you want to lower the frequency of the Tzeva Adom checks, create your notifier like this:
  ```java
  new TzevaAdomNotifier.Builder()
  .requestEvery(Duration.ofSeconds(3))
  ```
