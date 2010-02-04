/*
 * @(#) src/games/stendhal/client/events/FeatureChangeListener.java
 *
 * $Id$
 */

package games.stendhal.client.listener;

/**
 * A listener of feature changes.
 */
public interface FeatureChangeListener {
	/**
	 * A feature was disabled.
	 * 
	 * @param name
	 *            The name of the feature.
	 */
	void featureDisabled(String name);

	/**
	 * A feature was enabled.
	 * 
	 * @param name
	 *            The name of the feature.
	 * @param value
	 *            Optional feature specific data.
	 */
	void featureEnabled(String name, String value);
}
