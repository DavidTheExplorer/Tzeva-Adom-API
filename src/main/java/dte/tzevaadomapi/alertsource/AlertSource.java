package dte.tzevaadomapi.alertsource;

import java.util.Deque;

import dte.tzevaadomapi.alert.Alert;

/**
 * Responsible of providing information about Tzeva Adoms in Israel. The info comes in form of {@link Alert} objects.
 */
public interface AlertSource
{
	/**
	 * Returns the most recent {@link Alert} that happened in Israel.
	 * 
	 * @return Either the alert, or {@link #NO_RESULT} if one wasn't found.
	 * @throws Exception If an exception happened while gathering the alert.
	 */
	Alert getMostRecentAlert() throws Exception;
	
	/**
	 * Returns all the alerts that happened since the provided {@code alert}.
	 * 
	 * @param alert The 'minimum' alert.
	 * @return The alerts list, or an empty result if there was no Tzeva Adom afterwards.
	 * @throws Exception iF an exception happened while gathering the alerts.
	 */
	Deque<Alert> getSince(Alert alert) throws Exception;
	
	
	/**
	 * This object signals when {@link #getMostRecentAlert()} couldn't find an alert to return, and no exceptions occurred.
	 * <p>
	 * This is addressed because <i>Pikud Ha'oref</i> returns an empty JSON if no Tzeva Adom happened in the last 24 hours.
	 */
	Alert NO_RESULT = null;
}