package dte.tzevaadomapi.notifier;

import static dte.tzevaadomapi.utils.UncheckedExceptions.unchecked;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;

@TestInstance(Lifecycle.PER_CLASS)
public class TzevaAdomNotifierTest
{
	private LocalDateTime now;
	private AlertSource alertSource;
	
	private static final Duration SIMULATION_DURATION = Duration.ofMillis(200);
	
	@BeforeAll
	public void setup() throws Exception 
	{
		this.now = LocalDateTime.now();
		this.alertSource = mock(AlertSource.class);
	}

	@Test
	public void testNotTzevaAdom() throws Exception 
	{
		Alert alert = new Alert("Tel Aviv", this.now);
		when(this.alertSource.getMostRecentAlert()).thenReturn(alert, alert, alert);
		assertHistoryEquals(simulateNotifier(this.alertSource), alert);
	}
	
	@Test
	public void testTzevaAdomDetection() throws Exception
	{
		Alert firstAlert = new Alert("Tel Aviv", this.now);
		Alert secondAlert = new Alert("Haifa", this.now);
		
		when(this.alertSource.getMostRecentAlert()).thenReturn(firstAlert, firstAlert, firstAlert, firstAlert, secondAlert);
		assertHistoryEquals(simulateNotifier(this.alertSource), firstAlert, secondAlert);
	}
	
	
	private static void assertHistoryEquals(TzevaAdomNotifier notifier, Alert... expectedHistory) 
	{
		assertIterableEquals(notifier.getHistory(), Arrays.asList(expectedHistory));
	}
	
	private TzevaAdomNotifier simulateNotifier(AlertSource alertSource) throws InterruptedException 
	{
		TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
				.every(Duration.ofMillis(5))
				.requestFrom(alertSource)
				.onFailedRequest(Exception::printStackTrace)
				.build();
		
		CompletableFuture.runAsync(unchecked(notifier::listen));
		Thread.sleep(SIMULATION_DURATION.toMillis());
		
		return notifier;
	}
}