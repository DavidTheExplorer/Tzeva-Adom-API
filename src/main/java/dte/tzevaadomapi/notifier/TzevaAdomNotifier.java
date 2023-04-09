package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;

/**
 * Notifies registered listeners once a Tzeva Adom takes place.
 * <p>
 * A request for the most recent alert is sent every constant duration, and the result is then compared to the previous one.
 * If the 2 alerts don't equal - It's <b>Tzeva Adom</b> and the registered listeners are notified.
 * <p>
 * This class implements <b>Iterable{@literal <Alert>}</b> which returns the history of Tzeva Adom alerts.
 */
public class TzevaAdomNotifier implements Iterable<Alert>
{
	private final AlertSource alertSource;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private final Set<Consumer<Alert>> listeners = new HashSet<>();
	private final Deque<Alert> history = new LinkedList<>();

	private LocalDateTime initialRequestTime;

	private TzevaAdomNotifier(AlertSource alertSource, Duration requestDelay, Consumer<Exception> requestFailureHandler) 
	{
		this.alertSource = alertSource;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}

	public void listen() throws InterruptedException
	{
		//start with an initial alert - against which future alerts will be compared
		this.history.add(getMostRecentAlert());
		this.initialRequestTime = LocalDateTime.now();

		while(true)
		{
			TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());

			Alert alert = getMostRecentAlert();
			
			//if the last alert in history doesn't equal to the last requested one - It's TZEVA ADOM
			if(this.history.peekLast().equals(alert))
				continue;
			
			this.listeners.forEach(listener -> listener.accept(alert));
			this.history.add(alert);
		}
	}

	public void addListener(Consumer<Alert> tzevaAdomListener) 
	{
		this.listeners.add(tzevaAdomListener);
	}

	public Alert getLastAlert()
	{
		return this.history.peekLast();
	}

	public Set<Alert> getHistory()
	{
		return new LinkedHashSet<>(this.history);
	}

	public LocalDateTime getInitialRequestTime() 
	{
		return this.initialRequestTime;
	}

	@Override
	public Iterator<Alert> iterator() 
	{
		return this.history.iterator();
	}

	private Alert getMostRecentAlert()
	{
		while(true) 
		{
			try 
			{
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
		private Consumer<Exception> requestFailureHandler;
		private Set<Consumer<Alert>> listeners = new HashSet<>();

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
		
		public Builder onTzevaAdom(Consumer<Alert> listener)
		{
			this.listeners.add(listener);
			return this;
		}
		
		public TzevaAdomNotifier build()
		{
			Objects.requireNonNull(this.alertSource, "The source of the alerts must be provided!");
			Objects.requireNonNull(this.requestDelay, "The delay between requesting alerts must be provided!");
			Objects.requireNonNull(this.requestFailureHandler, "The alerts' request failure handler must be provided!");
			
			TzevaAdomNotifier notifier = new TzevaAdomNotifier(this.alertSource, this.requestDelay, this.requestFailureHandler);
			this.listeners.forEach(notifier::addListener);

			return notifier;
		}
	}
}