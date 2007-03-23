/*
 * @(#) src/games/stendhal/common/ConfigurableFactoryContextImpl.java
 *
 * $Id$
 */

package games.stendhal.common;

//
//

import java.util.Map;

/**
 * A simple implementation of a configuration context for general object
 * factories.
 */
public class ConfigurableFactoryContextImpl implements ConfigurableFactoryContext {

	protected Map<String, String> attributes;

	/**
	 * Create a configuration context using an attribute map.
	 * NOTE: The attributes are not copied.
	 *
	 * @param	attributes	The attributes.
	 */
	public ConfigurableFactoryContextImpl(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	//
	// ConfigurableFactoryContext
	//

	/**
	 * Get an attribute.
	 *
	 * @param	name		The attribute name.
	 *
	 * @return	The value of the attribute, or <code>null</code> if
	 *		not set.
	 */
	public String getAttribute(String name) {
		return attributes.get(name);
	}
}
