package dte.tzevaadomapi.alert.provider;

import java.util.Deque;

import dte.tzevaadomapi.alert.Alert;

/** Provides information about Tzeva Adom alerts in Israel. */
public interface AlertProvider
{
	/**
	 * Returns the most recent {@link Alert}.
	 * 
	 * @return The alert, or null if no info was found.
	 */
	Alert getMostRecent();
	
	/**
	 * Returns all the alerts that happened since the provided {@code alert}.
	 * 
	 * @param alert The alert used as a beginning point.
	 * @return The alerts list, will be empty if there were no such alerts.
	 */
	Deque<Alert> getSince(Alert alert);
}