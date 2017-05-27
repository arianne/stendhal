/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.factory;

import java.util.Map;

/**
 * A configuration context for general object factories.
 */
public class ConfigurableFactoryContext {

	private final Map<String, String> attributes;

	/**
	 * Create a configuration context using an attribute map. NOTE: The
	 * attributes are not copied.
	 *
	 * @param attributes
	 *            The attributes.
	 */
	public ConfigurableFactoryContext(final Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Extracts a boolean value from a string.
	 *
	 * @param name
	 *            name of the attribute (only used for error handling)
	 * @param value
	 *            value to parse
	 * @return the parsed value
	 * @throws IllegalArgumentException
	 *             in case the value is not a valid boolean
	 */
	private static boolean extractBooleanFromString(final String name, final String value) {
		if ("true".equals(value)) {
			return true;
		}

		if ("false".equals(value)) {
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
	 *             in case the value is not a valid boolean
	 */
	public boolean getBoolean(final String name, final boolean defaultValue) {
		final String value = attributes.get(name);
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
	 *             in case the value is not a valid boolean or is missing
	 */
	public boolean getRequiredBoolean(final String name) {
		final String value = this.getRequiredString(name);
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
	public int getInt(final String name, final int defaultValue) {
		final String value = attributes.get(name);
		if (value == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException ex) {
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
	public int getRequiredInt(final String name) {
		final String value = this.getRequiredString(name);
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException ex) {
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
	public String getString(final String name, final String defaultValue) {
		final String value = attributes.get(name);
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
	public String getRequiredString(final String name) {
		final String value = attributes.get(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing required attribute "
					+ name);
		}
		return value;
	}
}
