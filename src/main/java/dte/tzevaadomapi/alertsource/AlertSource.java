package dte.tzevaadomapi.alertsource;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface AlertSource
{
	Alert getMostRecentAlert() throws Exception;
	
	
	/**
	 * This object signals that no Exception occurred while {@link #getMostRecentAlert()}, but no alert was returned.
	 * <p>
	 * Example: <i>Pikud Ha'oref</i> usually returns an empty JSON if no Tzeva Adom happened in the last 24 hours.
	 */
	Alert NO_RESPONSE = null;
}