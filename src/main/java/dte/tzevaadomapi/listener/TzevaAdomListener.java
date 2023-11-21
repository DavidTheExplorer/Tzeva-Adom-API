package dte.tzevaadomapi.listener;

import dte.tzevaadomapi.alert.Alert;

/**
 * Represents an action that should be done <b>immediately</b> when a Tzeva Adom takes place.
 */
@FunctionalInterface
public interface TzevaAdomListener
{
	void onTzevaAdom(Alert alert);
}