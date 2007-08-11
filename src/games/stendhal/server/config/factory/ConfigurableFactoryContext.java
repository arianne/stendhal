package games.stendhal.server.config.factory;

import java.util.Map;

/**
 * A configuration context for general object factories
 */
public class ConfigurableFactoryContext {

	private Map<String, String> attributes;

	/**
	 * Create a configuration context using an attribute map.
	 * NOTE: The attributes are not copied.
	 *
	 * @param	attributes	The attributes.
	 */
	public ConfigurableFactoryContext(Map<String, String> attributes) {
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
