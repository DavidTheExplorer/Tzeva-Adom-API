package dte.tzevaadomapi.notifier;

import dte.tzevaadomapi.alert.Alert;

@FunctionalInterface
public interface TzevaAdomListener
{
	void onTzevaAdom(Alert alert);
}