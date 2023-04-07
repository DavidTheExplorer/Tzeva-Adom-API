package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alert.source.AlertSource;

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
	//requests
	private final AlertSource alertSource;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private LocalDateTime initialRequestTime;

	//general
	private final Set<Consumer<Alert>> listeners = new HashSet<>();
	private final Deque<Alert> history = new LinkedList<>();

	private TzevaAdomNotifier(AlertSource alertSource, Duration requestDelay, Consumer<Exception> requestFailureHandler) 
	{
		this.alertSource = alertSource;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}

	public void listen() throws InterruptedException
	{
		this.history.add(repeatedGetMostRecentAlert());
		
		this.initialRequestTime = LocalDateTime.now();

		while(true)
		{
			TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());

			//request the most recent alert again
			getMostRecentAlert()
			.filter(this::isTzevaAdom)
			.ifPresent(tzevaAdomAlert -> 
			{
				this.listeners.forEach(listener -> listener.accept(tzevaAdomAlert));
				this.history.add(tzevaAdomAlert);
			});
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
		return new HashSet<>(this.history);
	}

	public LocalDateTime getInitialRequestTime() 
	{
		return this.initialRequestTime;
	}

	public int size()
	{
		return this.history.size();
	}

	@Override
	public Iterator<Alert> iterator() 
	{
		return this.history.iterator();
	}

	private boolean isTzevaAdom(Alert alert) 
	{
		return !this.history.peekLast().equals(alert);
	}

	private Alert repeatedGetMostRecentAlert() throws InterruptedException
	{
		while(true) 
		{
			try 
			{
				return this.alertSource.getMostRecentAlert();
			}
			catch(Exception ignored) //we don't care about exceptions, we want to request forever until we get an alert
			{
				TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());
			}
		}
	}

	private Optional<Alert> getMostRecentAlert() 
	{
		try 
		{
			return Optional.of(this.alertSource.getMostRecentAlert());
		}
		catch(Exception exception) 
		{
			this.requestFailureHandler.accept(exception);
			return Optional.empty();
		}
	}



	public static class Builder
	{
		private AlertSource alertSource;
		private Duration requestDelay;
		private Set<Consumer<Alert>> listeners = new HashSet<>();
		private Consumer<Exception> requestFailureHandler;

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