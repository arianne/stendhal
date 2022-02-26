/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;


/**
 * Exposes Java system properties to Lua.
 */
public class LuaPropertiesHelper {

	/** The singleton instance. */
	private static LuaPropertiesHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static PropertiesHelper instance.
	 */
	public static LuaPropertiesHelper get() {
		if (instance == null) {
			instance = new LuaPropertiesHelper();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaPropertiesHelper() {
		// singleton
	}

	/**
	 * Retrievies the value of a property.
	 *
	 * @param p
	 * 		The property of which the value should be returned.
	 * @return
	 * 		Value of the property or <code>null</code> if not set.
	 */
	public String getValue(final String p) {
		if (p == null) {
			return null;
		}

		return System.getProperty(p);
	}

	/**
	 * Checks if a property is set.
	 *
	 * @param p
	 * 		The property string to check.
	 * @return
	 * 		<code>true</code> if the property is not <code>null</code>.
	 */
	public boolean enabled(final String p) {
		if (p == null) {
			return false;
		}

		return System.getProperty(p) != null;
	}

	/**
	 * Compares a property value to a string.
	 *
	 * @param p
	 * 		The property string to check.
	 * @param v
	 * 		The value to check the property against.
	 * @return
	 * 		<code>true</code> if the property value is equal to v.
	 */
	public boolean equals(final String p, final String v) {
		if (p == null || v == null) {
			return false;
		}

		final String property = System.getProperty(p);
		return property != null && property.equals(v);
	}
}
