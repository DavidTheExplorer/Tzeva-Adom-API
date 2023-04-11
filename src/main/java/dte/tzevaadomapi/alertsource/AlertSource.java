package dte.tzevaadomapi.alertsource;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface AlertSource
{
	Alert getMostRecentAlert() throws Exception;
	
	
	public static final Alert EMPTY_RESPONSE = null;
}