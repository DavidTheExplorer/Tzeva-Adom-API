package dte.tzevaadomapi.alert.notifier;

import dte.tzevaadomapi.alert.Alert;

/** An action that is executed immediately upon a Tzeva Adom alert. */
@FunctionalInterface
public interface AlertListener
{
	void onTzevaAdom(Alert alert);
}