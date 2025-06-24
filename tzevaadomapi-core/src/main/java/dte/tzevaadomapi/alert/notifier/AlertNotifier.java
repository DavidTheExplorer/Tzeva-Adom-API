package dte.tzevaadomapi.alert.notifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alert.provider.AlertProvider;
import dte.tzevaadomapi.alert.provider.PHOAlertProvider;

/** Notifies registered listeners immediately upon a Tzeva Adom alert. */
public class AlertNotifier
{
	private final AlertProvider alertProvider;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private final Collection<AlertListener> listeners;
	private final AlertHistory history = new AlertHistory();
	
	private Alert mostRecentAlert; //only used in listenAsync(), holding it here solves the effectively final problem

	private AlertNotifier(Collection<AlertListener> listeners, AlertProvider alertProvider, Duration requestDelay, Consumer<Exception> requestFailureHandler)
	{
		this.listeners = listeners;
		this.alertProvider = alertProvider;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}

	/**
	 * Starts listening to alerts, and returns the corresponding {@link CompletableFuture} object for further control.
	 * <p>
	 * In order to implement a program whose sole workflow is to be idle until there is an alert, call {@link CompletableFuture#join() join()} on the result.
	 * 
	 * @return The wrapping {@link CompletableFuture} object.
	 */
	public CompletableFuture<Void> listenAsync()
	{
		return CompletableFuture.runAsync(() -> 
		{
			//start with the most recent alert
			this.mostRecentAlert = queryProvider(AlertProvider::getMostRecentAlert);
			
			while(true)	
			{
				Deque<Alert> newAlerts = queryProvider(provider -> provider.getSince(this.mostRecentAlert));

				//wait until a terror organization decides to launch rockets
				if(newAlerts.isEmpty()) 
					continue;
				
				//update the history variables
				this.mostRecentAlert = newAlerts.getLast();
				this.history.addAll(newAlerts);
				
				//notify Tzeva Adom
				newAlerts.forEach(this::notifyListeners);
			}
		}, Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "Tzeva-Adom-Notifier")));
	}
	
	/**
	 * Adds a listener to notify upon a Tzeva Adom.
	 * 
	 * @param listener The listener.
	 */
	public void addListener(AlertListener listener)
	{
		this.listeners.add(listener);
	}
	
	/**
	 * Returns the captured history since {@link #listenAsync()} was called.
	 * 
	 * @return The alert history.
	 */
	public AlertHistory getHistory()
	{
		return this.history;
	}

	private <T> T queryProvider(Function<AlertProvider, T> request)
	{
		while(true)
		{
			try 
			{
				//sleep the defined delay
				TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());

				T result = request.apply(this.alertProvider);

				if(result == null)
					continue;

				return result;
			}
			catch(Exception exception) 
			{
				//handle the exception and request again
				this.requestFailureHandler.accept(exception);
			}
		}
	}
	
	private void notifyListeners(Alert alert)
	{
		this.listeners.forEach(listener -> listener.onTzevaAdom(alert));
	}



	public static class Builder
	{
		private final Collection<AlertListener> listeners = new ArrayList<>();
		private AlertProvider alertProvider = new PHOAlertProvider(); //Pikud Ha'oref is the default provider
		private Duration requestDelay = Duration.ofMillis(500); //half a second is a reasonable delay
		private Consumer<Exception> requestFailureHandler;

		public Builder requestFrom(AlertProvider alertProvider)
		{
			this.alertProvider = alertProvider;
			return this;
		}

		public Builder requestEvery(Duration requestDelay) 
		{
			this.requestDelay = requestDelay;
			return this;
		}

		/**
		 * Determines how to handle exceptions when alerts are fetched.
		 *
		 * @param handler The exception handler.
		 * @return The same instance.
		 * @apiNote To prevent unwanted behaviour such as spam-logging the same IOException when the server is offline, You can pass a <i>LimitedExceptionHandler</i>(extras module).
		 */
		public Builder onFailedRequest(Consumer<Exception> handler) 
		{
			this.requestFailureHandler = handler;

			//support for LimitedExceptionHandler
			if(handler instanceof AlertListener alertListener)
				this.listeners.add(alertListener);

			return this;
		}

		public Builder onTzevaAdom(AlertListener listener)
		{
			this.listeners.add(listener);
			return this;
		}

		public AlertNotifier listenAsync()
		{
			AlertNotifier notifier = build();
			notifier.listenAsync();

			return notifier;
		}

		public AlertNotifier build()
		{
			if(this.requestFailureHandler == null)
				throw new IllegalStateException("A request failure handler must be provided to create an AlertNotifier.");

			if(this.listeners.isEmpty())
				throw new IllegalStateException("At least one listener must be provided to create an AlertNotifier.");

			return new AlertNotifier(this.listeners, this.alertProvider, this.requestDelay, this.requestFailureHandler);
		}
	}
}