package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;
import dte.tzevaadomapi.alertsource.PHOAlertSource;

/**
 * Notifies registered listeners once a Tzeva Adom takes place.
 * <p>
 * A request for the most recent alert is sent every constant duration, and the result is then compared to the previous one.
 * If the 2 alerts don't equal - It's <b>Tzeva Adom</b> and the registered listeners are notified.
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
	
	public static Builder requestFromPikudHaoref() 
	{
		return new Builder()
				.requestFrom(new PHOAlertSource());
	}

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
				if(lastTzevaAdom.equals(alert)) 
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
		private AlertSource alertSource;
		private Duration requestDelay;
		private Consumer<Exception> requestFailureHandler = (exception) -> {};
		private Set<TzevaAdomListener> listeners = new HashSet<>();

		public Builder requestFrom(AlertSource alertSource) 
		{
			this.alertSource = alertSource;
			return this;
		}

		public Builder every(Duration requestDelay) 
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
			Objects.requireNonNull(this.alertSource, "The source of the alerts must be provided!");
			Objects.requireNonNull(this.requestDelay, "The delay between requesting alerts must be provided!");
			
			TzevaAdomNotifier notifier = new TzevaAdomNotifier(this.alertSource, this.requestDelay, this.requestFailureHandler);
			this.listeners.forEach(notifier::addListener);

			return notifier;
		}
	}
}