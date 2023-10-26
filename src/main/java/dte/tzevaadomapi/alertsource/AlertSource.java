package dte.tzevaadomapi.alertsource;

import java.util.Deque;

import dte.tzevaadomapi.alert.Alert;

/**
 * Responsible of gathering information about the most recent {@link Alert Tzeva Adom} in Israel.
 */
public interface AlertSource
{
	/**
	 * Returns an updated {@link Alert} that describes the last <b>Tzeva Adom</b> in Israel.
	 * 
	 * @return The last alert that happened in Israel.
	 * @throws Exception If the information gathering process encountered an exception(typically {@code IOException})
	 * @apiNote This should never return a cached value, don't make a joke.
	 */
	Alert getMostRecentAlert() throws Exception;
	
	/**
	 * Returns all alerts that took place since the provided {@code alert} had happened.
	 * 
	 * @param alert The 'minimum' alert.
	 * @return The alerts list.
	 * @throws Exception if an exception happened while gathering the alerts.
	 */
	Deque<Alert> getSince(Alert alert) throws Exception;
	
	
	/**
	 * This object signals that no Exception occurred while {@link #getMostRecentAlert()}, but no alert was returned.
	 * <p>
	 * Example: <i>Pikud Ha'oref</i> usually returns an empty JSON if no Tzeva Adom happened in the last 24 hours.
	 */
	Alert NO_RESULT = null;
}