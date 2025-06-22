package dte.tzevaadomapi.alertsource;

import java.util.Deque;

import dte.tzevaadomapi.alert.Alert;

/**
 * Provides information about Tzeva Adoms in Israel, in form of {@link Alert} objects.
 */
public interface AlertSource
{
	/**
	 * Returns the most recent {@link Alert} that happened in Israel
	 * 
	 * @return The alert, or null if no info was found.
	 */
	Alert getMostRecentAlert();
	
	/**
	 * Returns all the alerts that happened since the provided {@code alert}.
	 * 
	 * @param alert The 'minimum' alert.
	 * @return The alerts list, or an empty result if there was no Tzeva Adom afterward.
	 */
	Deque<Alert> getSince(Alert alert);
}