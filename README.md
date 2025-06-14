# Tzeva Adom API
Notifies registered listeners as soon as a Tzeva Adom happens, based on real-time updates from `Pikud Ha'oref`.\
Integrating this in projects used by many israelis **increases** the chances of **saving a life**.

## How to use
Example:
```java
Game game = ...

TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
        .onFailedRequest(exception -> LOGGER.error("Failed to check the latest alert", exception))
        .onTzevaAdom(alert ->
        {
                game.stop();
                game.displayMessage("There is a Tzeva Adom in: " + alert.getRegion());
        })
        .build();

//returns a CompletableFuture - so you can join() if your program needs to be idle until a Tzeva Adom
notifier.listenAsync();
```
\
By saving the notifier object, you can receive the captured alert history:
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
List<Alert> alerts = notifier.getHistory().ofRegion("תל אביב");
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
  
-  Change the endpoint of Pikud Ha'oref(temp fix in case it has been changed):
   ```java
   //PHOAlertSource is the default AlertSource implementation, you can also implement your own.
   PHOAlertSource alertSource = new PHOAlertSource();
   alertSource.changeRequestURL(new URL("..."));
   
   new TzevaAdomNotifier.Builder()
   	.requestFrom(alertSource)
   ```
   
- Prevent spam when handling request exceptions:
  ```java
  //LimitedExceptionHandler is a wrapper of Consumer<Exception> that stops handling after X times
  new TzevaAdomNotifier.Builder()
  	.onFailedRequest(new LimitedExceptionHandler(3, yourExceptionHandler));
   ```

## Contributions
All Pull Requests are welcome!
