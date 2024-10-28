package dte.tzevaadomapi.listener;

import dte.tzevaadomapi.alert.Alert;

/**
 * Represents an action that should be executed <b>immediately</b> upon a Tzeva Adom.
 */
@FunctionalInterface
public interface TzevaAdomListener
{
	void onTzevaAdom(Alert alert);
}