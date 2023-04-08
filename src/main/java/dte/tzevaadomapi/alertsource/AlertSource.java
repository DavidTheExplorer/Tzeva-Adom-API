package dte.tzevaadomapi.alertsource;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface AlertSource
{
	Alert getMostRecentAlert() throws Exception;
}