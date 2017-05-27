/*
 * @(#) src/games/stendhal/server/config/zone/ZonesXMLLoader.java
 *
 * $Id$
 */

package games.stendhal.server.core.config.zone;

import java.util.HashMap;
import java.util.Map;

//
//

import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * A base zone setup descriptor.
 */
public abstract class SetupDescriptor {
	/**
	 * The configuration parameters.
	 */
	protected HashMap<String, String> parameters;

	/**
	 * Create a base zone setup descriptor.
	 */
	public SetupDescriptor() {
		parameters = new HashMap<String, String>();
	}

	//
	// SetupDescriptor
	//

	/**
	 * Get the configuration parameters.
	 *
	 * @return A map of parameters.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Set a configuration parameter.
	 *
	 * @param name
	 *            A parameter name.
	 * @param value
	 *            A parameter value.
	 */
	public void setParameter(final String name, final String value) {
		parameters.put(name, value);
	}

	/**
	 * Do appropriate zone setup.
	 *
	 * @param zone
	 *            The zone to setup.
	 */
	public abstract void setup(final StendhalRPZone zone);
}
