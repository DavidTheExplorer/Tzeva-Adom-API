package dte.tzevaadomapi.alert.notifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alert.provider.AlertProvider;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class AlertNotifierTest
{
	@Mock
	private AlertProvider alertProvider;
	
	private static final Deque<Alert> NO_UPDATES = new LinkedList<>();
	
	@Test
	public void testNotTzevaAdom() throws Exception
	{
		Alert alert = createAlert("Tel Aviv");
		
		when(this.alertProvider.getMostRecentAlert()).thenReturn(alert);
		when(this.alertProvider.getSince(alert)).thenReturn(NO_UPDATES);

		assertEquals(0, simulateNotifier().size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testTzevaAdom() throws Exception
	{
		Alert first = createAlert("Tel Aviv");
		Alert second = createAlert("Haifa");
		
		when(this.alertProvider.getMostRecentAlert()).thenReturn(first);
		when(this.alertProvider.getSince(first)).thenReturn(NO_UPDATES, NO_UPDATES, dequeOf(second), NO_UPDATES);
		
		assertEquals(1, simulateNotifier().size());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testConsecutiveTzevaAdom() throws Exception 
	{
		Alert first = createAlert("Tel Aviv");
		Alert second = createAlert("Haifa");
		Alert third = createAlert("Jerusalem");
		
		when(this.alertProvider.getMostRecentAlert()).thenReturn(first);
		when(this.alertProvider.getSince(any())).thenReturn(dequeOf(second), dequeOf(third), dequeOf(first), NO_UPDATES);
		
		assertEquals(3, simulateNotifier().size());
	}
	
	@Test
	@DisplayName("The usual request flow where 99% are no responses")
	@SuppressWarnings("unchecked")
	public void testUsualRoutine() throws Exception 
	{
		Alert first = createAlert("Tel Aviv");
		
		when(this.alertProvider.getMostRecentAlert()).thenReturn(first);
		
		when(this.alertProvider.getSince(any())).thenReturn(
				NO_UPDATES, 
				NO_UPDATES, 
				NO_UPDATES,
				NO_UPDATES, 
				dequeOf(first),
				NO_UPDATES);
		
		assertEquals(1, simulateNotifier().size());
	}
	
	private static Alert createAlert(String region) 
	{
		String randomDescription = ThreadLocalRandom.current().nextBoolean() ? "חדירת מחבלים" : "חדירת כלי טיס עוין";
		
		return new Alert(region, randomDescription, LocalDateTime.now());
	}
	
	/**
	 * Runs a new notifier based on the mocked alert provider, and then returns its tzeva adom history.
	 */
	private AlertHistory simulateNotifier() throws InterruptedException
	{
		AlertNotifier notifier = new AlertNotifier.Builder()
				.requestEvery(Duration.ofMillis(5))
				.requestFrom(this.alertProvider)
				.onFailedRequest(Assertions::fail)
				.onTzevaAdom(alert -> {})
				.listenAsync();
		
		//each test gets 200ms to run(or 40 alerts because of 200/the delay)
		TimeUnit.MILLISECONDS.sleep(200);
		
		return notifier.getHistory();
	}
	
	private static Deque<Alert> dequeOf(Alert alert)
	{
		Deque<Alert> deque = new LinkedList<>();
		deque.add(alert);

		return deque;
	}
}