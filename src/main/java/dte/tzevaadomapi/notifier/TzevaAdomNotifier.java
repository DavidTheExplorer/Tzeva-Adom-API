package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;
import dte.tzevaadomapi.alertsource.PHOAlertSource;

/**
 * Notifies registered listeners once a <b>Tzeva Adom</b> takes place.
 * <p>
 * The workflow of the notifier is:
 * <ol>
 * 	<li> Request the most recent alert sent every constant duration(typically ~2 seconds)
 * 	<li> Compare it to the previous one, or store the first result:
 * 	<li> If the 2 alerts are not identical, It's <b>Tzeva Adom</b> - and the listeners are notified immediately.
 * </ol>
 */
public class TzevaAdomNotifier
{
	private final AlertSource alertSource;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private final Set<TzevaAdomListener> listeners = new HashSet<>();
	private final Deque<Alert> history = new LinkedList<>();

	private TzevaAdomNotifier(AlertSource alertSource, Duration requestDelay, Consumer<Exception> requestFailureHandler) 
	{
		this.alertSource = alertSource;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}
	
	/**
	 * @deprecated Pikud Ha'oref now provides Alerts by default(overridable) - Use the Builder directly.
	 */
	@Deprecated
	public static Builder basedOnPikudHaoref() 
	{
		return new Builder()
				.requestFrom(new PHOAlertSource());
	}

	/**
	 * Starts listening and reacting to <b>Tzeva Adom</b> on a separate Thread, 
	 * and returns the the corresponding {@link CompletableFuture} object for further control.
	 * 
	 * @return The {@link CompletableFuture} responsible of reacting to <b>Tzeva Adoms</b>.
	 */
	public CompletableFuture<Void> listen()
	{
		return CompletableFuture.runAsync(() -> 
		{
			//start with an initial alert - against which future alerts will be compared
			Alert lastTzevaAdom = getMostRecentAlert();

			while(true)
			{
				Alert alert = getMostRecentAlert();
				
				//ignore empty responses
				if(alert == AlertSource.NO_RESPONSE)
					continue;
				
				//if the last alert in history equals the the last requested - it's not Tzeva Adom
				if(alert.equals(lastTzevaAdom)) 
					continue;
				
				lastTzevaAdom = alert;
				
				this.listeners.forEach(listener -> listener.onTzevaAdom(alert));
				this.history.add(alert);
			}
		});
	}

	public void addListener(TzevaAdomListener listener) 
	{
		this.listeners.add(listener);
	}

	public Alert getLastAlert()
	{
		return this.history.peekLast();
	}

	public Set<Alert> getHistory()
	{
		return new LinkedHashSet<>(this.history);
	}

	private Alert getMostRecentAlert()
	{
		while(true)
		{
			try
			{
				TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());
				
				return this.alertSource.getMostRecentAlert();
			}
			catch(Exception exception)
			{
				this.requestFailureHandler.accept(exception);
			}
		}
	}



	public static class Builder
	{
		private AlertSource alertSource = new PHOAlertSource(); //obviously Pikud Ha'oref is the default source
		private Duration requestDelay = Duration.ofMillis(500); //half a second is a reasonable delay
		private Consumer<Exception> requestFailureHandler = (exception) -> {};
		private Set<TzevaAdomListener> listeners = new HashSet<>();
		
		public Builder requestFrom(AlertSource alertSource) 
		{
			this.alertSource = alertSource;
			return this;
		}

		public Builder requestEvery(Duration requestDelay) 
		{
			this.requestDelay = requestDelay;
			return this;
		}
		
		public Builder onFailedRequest(Consumer<Exception> handler) 
		{
			this.requestFailureHandler = handler;
			return this;
		}
		
		public Builder onTzevaAdom(TzevaAdomListener listener)
		{
			this.listeners.add(listener);
			return this;
		}
		
		public CompletableFuture<Void> listen()
		{
			return build().listen();
		}
		
		public TzevaAdomNotifier build()
		{
			TzevaAdomNotifier notifier = new TzevaAdomNotifier(this.alertSource, this.requestDelay, this.requestFailureHandler);
			this.listeners.forEach(notifier::addListener);

			return notifier;
		}
	}
}