package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;
import dte.tzevaadomapi.alertsource.PHOAlertSource;
import dte.tzevaadomapi.listener.TzevaAdomListener;
import dte.tzevaadomapi.utils.UncheckedExceptions.CheckedSupplier;

/**
 * Notifies registered listeners immediately once a <b>Tzeva Adom</b> takes place.
 */
public class TzevaAdomNotifier
{
	private final AlertSource alertSource;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private final Collection<TzevaAdomListener> listeners;
	private final TzevaAdomHistory history = new TzevaAdomHistory();
	private Alert mostRecentAlert; //only used in listenAsync(), holding it here solves the effectively final problem

	private TzevaAdomNotifier(Collection<TzevaAdomListener> listeners, AlertSource alertSource, Duration requestDelay, Consumer<Exception> requestFailureHandler)
	{
		this.listeners = listeners;
		this.alertSource = alertSource;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}

	/**
	 * Starts listening to incoming alerts, and returns the corresponding {@link CompletableFuture} object for further control.
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
			this.mostRecentAlert = requestMostRecentAlert();
			
			while(true)	
			{
				Deque<Alert> newAlerts = requestFromSource(() -> this.alertSource.getSince(this.mostRecentAlert));
				
				//if there are no new alerts - it's not Tzeva Adom
				if(newAlerts.isEmpty()) 
					continue;
				
				//update the history variables
				this.mostRecentAlert = newAlerts.getFirst();
				this.history.update(newAlerts);
				
				//notify Tzeva Adom
				newAlerts.forEach(this::notifyListeners);
			}
		}, Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "Tzeva-Adom-Notifier")));
	}
	
	/**
	 * Adds a listener to notify when it's Tzeva Adom.
	 * 
	 * @param listener The listener.
	 */
	public void addListener(TzevaAdomListener listener) 
	{
		this.listeners.add(listener);
	}
	
	/**
	 * Returns the captured history since {@link #listenAsync()} was called.
	 * 
	 * @return The tzeva adom history.
	 */
	public TzevaAdomHistory getHistory()
	{
		return this.history;
	}
	
	private Alert requestMostRecentAlert() 
	{
		Alert alert;
		
		//wait until Hamas decides to launch rockets
		do 
		{
			alert = requestFromSource(this.alertSource::getMostRecentAlert);
		}
		while(alert == AlertSource.NO_RESULT);
		
		return alert;
	}

	private <T> T requestFromSource(CheckedSupplier<T> resultFactory)
	{
		while(true)
		{
			try 
			{
				//sleep the defined delay
				TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());
				
				return resultFactory.get();
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
		private Collection<TzevaAdomListener> listeners = new ArrayList<>();
		private AlertSource alertSource = new PHOAlertSource(); //obviously Pikud Ha'oref is the default source
		private Duration requestDelay = Duration.ofMillis(500); //half a second is a reasonable delay
		private Consumer<Exception> requestFailureHandler;

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

		public TzevaAdomNotifier listenAsync()
		{
			TzevaAdomNotifier notifier = build();
			notifier.listenAsync();

			return notifier;
		}

		public TzevaAdomNotifier build()
		{
			if(this.requestFailureHandler == null)
				throw new IllegalStateException("A request failure handler must be provided to create a TzevaAdomNotifier.");

			if(this.listeners.isEmpty())
				throw new IllegalStateException("At least one listener must be provided to create a TzevaAdomNotifier.");

			return new TzevaAdomNotifier(this.listeners, this.alertSource, this.requestDelay, this.requestFailureHandler);
		}
	}
}