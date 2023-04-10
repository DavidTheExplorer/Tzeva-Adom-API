package dte.tzevaadomapi.notifier;

import static dte.tzevaadomapi.utils.UncheckedExceptions.unchecked;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TzevaAdomNotifierTest
{
	@Mock
	private AlertSource alertSource;

	@Test
	public void testNotTzevaAdom() throws Exception 
	{
		Alert alert = createAlert("Tel Aviv");
		
		when(this.alertSource.getMostRecentAlert()).thenReturn(alert, alert, alert);
		assertEquals(0, simulateNotifier(3).getHistory().size());
	}
	
	@Test
	public void testTzevaAdom() throws Exception
	{
		Alert firstAlert = createAlert("Tel Aviv");
		Alert secondAlert = createAlert("Haifa");
		
		when(this.alertSource.getMostRecentAlert()).thenReturn(firstAlert, firstAlert, firstAlert, firstAlert, secondAlert);
		assertEquals(1, simulateNotifier(5).getHistory().size());
	}
	
	@Test
	public void testConsecutiveTzevaAdom() throws Exception 
	{
		Alert firstAlert = createAlert("Tel Aviv");
		Alert secondAlert = createAlert("Haifa");
		Alert thirdAlert = createAlert("Jerusalem");
		
		when(this.alertSource.getMostRecentAlert()).thenReturn(firstAlert, secondAlert, thirdAlert, firstAlert);
		assertEquals(3, simulateNotifier(4).getHistory().size());
	}
	
	private static Alert createAlert(String city) 
	{
		return new Alert(city, "חדירת מחבלים", LocalDateTime.now());
	}
	
	/**
	 * Runs a notifier that records the specified amount of dummy alerts, and then returns it.
	 * 
	 * @return A notifier after a simulated run.
	 * @throws InterruptedException when the sleeping between requests fails.
	 */
	private TzevaAdomNotifier simulateNotifier(int alertsAmount) throws InterruptedException 
	{
		TzevaAdomNotifier notifier = new TzevaAdomNotifier.Builder()
				.every(Duration.ofMillis(5))
				.requestFrom(this.alertSource)
				.onFailedRequest(Exception::printStackTrace)
				.build();
		
		CompletableFuture.runAsync(unchecked(notifier::listen));
		
		//because I'm ahla gever, every alert gets 10ms to be recorded
		Thread.sleep(Duration.ofMillis(alertsAmount * 10).toMillis());
		
		return notifier;
	}
}