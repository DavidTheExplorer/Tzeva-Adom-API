package dte.tzevaadomapi.alert.notifier;

import dte.tzevaadomapi.alert.Alert;

/**
 * Represents an action that should be executed <b>immediately</b> upon a Tzeva Adom.
 */
@FunctionalInterface
public interface AlertListener
{
	void onTzevaAdom(Alert alert);
}