package dte.tzevaadomapi.alertsource;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface AlertSource
{
	Alert getMostRecentAlert() throws Exception;
	
	
	
	AlertSource PIKUD_HA_OREF = new PHOAlertSource("https://www.oref.org.il/WarningMessages/History/AlertsHistory.json");
}