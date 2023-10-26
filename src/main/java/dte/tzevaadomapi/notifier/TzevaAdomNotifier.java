package dte.tzevaadomapi.notifier;

import java.time.Duration;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import dte.tzevaadomapi.alert.Alert;
import dte.tzevaadomapi.alertsource.AlertSource;
import dte.tzevaadomapi.alertsource.PHOAlertSource;
import dte.tzevaadomapi.utils.UncheckedExceptions.CheckedSupplier;

/**
 * Notifies registered listeners immediately once a <b>Tzeva Adom</b> takes place.
 */
public class TzevaAdomNotifier
{
	private final AlertSource alertSource;
	private final Duration requestDelay;
	private final Consumer<Exception> requestFailureHandler;
	private final Set<TzevaAdomListener> listeners = new HashSet<>();
	private final TzevaAdomHistory history = new TzevaAdomHistory();
	
	//used to be a local variable in listen(), until it caused the effectively final problem
	private Alert mostRecentAlert;

	private TzevaAdomNotifier(AlertSource alertSource, Duration requestDelay, Consumer<Exception> requestFailureHandler) 
	{
		this.alertSource = alertSource;
		this.requestDelay = requestDelay;
		this.requestFailureHandler = requestFailureHandler;
	}

	/**
	 * Starts an async listening to incoming alerts, and returns the corresponding {@link CompletableFuture} object for further control.
	 * <p>
	 * This method can be sync by calling {@code join()} on the result.
	 * 
	 * @return The wrapping {@link CompletableFuture} object.
	 */
	public CompletableFuture<Void> listen()
	{
		return CompletableFuture.runAsync(() -> 
		{
			//start with the most recent alert
			this.mostRecentAlert = requestMostRecentAlert();
			
			while(true)	
			{
				Deque<Alert> newAlerts = request(() -> this.alertSource.getSince(this.mostRecentAlert));
				
				//if there are no new alerts - it's not Tzeva Adom
				if(newAlerts.isEmpty()) 
					continue;
				
				//update the history variables
				this.mostRecentAlert = newAlerts.getFirst();
				this.history.update(newAlerts);
				
				//notify Tzeva Adom
				newAlerts.forEach(this::notifyListeners);
			}
		});
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
	 * Returns the Tzeva Adom history since {@link #listen()} was called for this notifier.
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
			alert = request(this.alertSource::getMostRecentAlert);
		}
		while(alert == AlertSource.NO_RESULT);
		
		return alert;
	}

	private <R> R request(CheckedSupplier<R> resultFactory)
	{
		while(true)
		{
			try 
			{
				//sleep a reasonable amount of time
				TimeUnit.MILLISECONDS.sleep(this.requestDelay.toMillis());
				
				return resultFactory.get();
			}
			catch(Exception exception) 
			{
				//pass the exception to the handler
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