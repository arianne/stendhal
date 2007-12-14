package games.stendhal.server.core.config.factory;

import java.util.Map;

/**
 * A configuration context for general object factories
 */
public class ConfigurableFactoryContext {

	private Map<String, String> attributes;

	/**
	 * Create a configuration context using an attribute map. NOTE: The
	 * attributes are not copied.
	 * 
	 * @param attributes
	 *            The attributes.
	 */
	public ConfigurableFactoryContext(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Get an attribute.
	 * 
	 * @param name
	 *            The attribute name.
	 * 
	 * @return The value of the attribute, or <code>null</code> if not set.
	 * @deprecated use type safe methods
	 */
	@Deprecated
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Extracts a boolean value from a string
	 * 
	 * @param name
	 *            name of the attribute (only used for error handling)
	 * @param value
	 *            value to parse
	 * @return the parsed value
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid boolean
	 */
	private static boolean extractBooleanFromString(String name, String value)
			throws IllegalArgumentException {
		if (value.equals("true")) {
			return true;
		}

		if (value.equals("false")) {
			return false;
		}
		throw new IllegalArgumentException("Invalid '" + name
				+ "' attribute value: '" + value
				+ "' should be 'true' or 'false'");
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @param defaultValue
	 *            the default value it case it is not defined
	 * @return the value of the attribute
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid integer
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		String value = attributes.get(name);
		if (value == null) {
			return defaultValue;
		}

		return extractBooleanFromString(name, value);
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @return the value of the attribute
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid integer or is missing
	 */
	public boolean getRequiredBoolean(String name) {
		String value = this.getRequiredString(name);
		return extractBooleanFromString(name, value);
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @param defaultValue
	 *            the default value it case it is not defined
	 * @return the value of the attribute
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid integer
	 */
	public int getInt(String name, int defaultValue) {
		String value = attributes.get(name);
		if (value == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid '" + name
					+ "' attribute value: " + value
					+ " is not a valid integer.");
		}
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @return the value of the attribute
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid integer or is missing
	 */
	public int getRequiredInt(String name) throws IllegalArgumentException {
		String value = this.getRequiredString(name);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid '" + name
					+ "' attribute value: " + value
					+ " is not a valid integer.");
		}
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @param defaultValue
	 *            the default value it case it is not defined
	 * @return the value of the attribute
	 */
	public String getString(String name, String defaultValue) {
		String value = attributes.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * gets an attribute.
	 * 
	 * @param name
	 *            the attribute name.
	 * @return the value of the attribute
	 * @throws IllegalArgumentException
	 *             in case is missing
	 */
	public String getRequiredString(String name)
			throws IllegalArgumentException {
		String value = attributes.get(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing required attribute "
					+ name);
		}
		return value;
	}
}
