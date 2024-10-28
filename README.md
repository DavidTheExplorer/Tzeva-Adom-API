# Tzeva Adom API
Java library that communicates with `Pikud Ha'oref` and notifies registered listeners as soon as a Tzeva Adom happens.\
Integrating it in projects(games, etc) used by many israelis, **increases** the chances of saving someone's life.

## How to use
How to stop your addicting game on Tzeva Adom:
```java
Game game = ...;

TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
        .onFailedRequest(exception -> LOGGER.error("Failed to request the latest alert from Pikud Haoref", exception))
        .onTzevaAdom(alert ->
        {
                game.stop();
                game.displayMessage("There is a Tzeva Adom in: " + alert.getRegion());
        })
        .build();

//this returns a CompletableFuture - so you can join() if your program needs to stay silent until a Tzeva Adom happens
notifier.listenAsync();
```
\
By saving the notifier object, you can receive the captured alert history:
```java
import java.util.concurrent.TimeUnit;
import dte.tzevaadomapi.alert.Alert;

...
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
        <version>[latest version]</version>
</dependency>
```


## Adjustments
- Override the frequency of Tzeva Adom checks:
  ```java
  new TzevaAdomNotifier.Builder()
  	.requestEvery(Duration.ofSeconds(3))
  ```
  
-  Change the endpoint of Pikud Ha'oref if it was changed:
   ```java
   //PHOAlertSource is the default AlertSource implementation, you can also implement your own.
   PHOAlertSource alertSource = new PHOAlertSource();
   alertSource.changeRequestURL(new URL("..."));
   
   new TzevaAdomNotifier.Builder()
   	.requestFrom(alertSource)
   ```
   
- Prevent console spam when handling exceptions:
  ```java
  //LimitedExceptionHandler is a wrapper of Consumer<Exception> that stops handling after X times
  new TzevaAdomNotifier.Builder()
  	.onFailedRequest(new LimitedExceptionHandler(3, yourExceptionHandler));
   ```
