package dte.tzevaadomapi.alert.source;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface AlertSource
{
	Alert requestMostRecentAlert() throws Exception;
}