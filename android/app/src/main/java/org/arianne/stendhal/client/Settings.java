/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client;

import static org.arianne.stendhal.client.DebugLog.DebugLevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.Pair;


/**
 * Manages reading & writing settings.
 */
public class Settings {

	private static boolean initialized = false;
	private static File dataDir;
	private static String settingsPath;
	private static final Map<String, Pair<String, String>> settingsStore = new HashMap<>();


	/**
	 * Initializes default values for recognized settings.
	 */
	public static void init(final File dir) {
		if (initialized) {
			DebugLog.writeLine("cannot re-initialize settings", DebugLevel.WARN);
			return;
		}

		dataDir = dir;
		settingsPath = dataDir.getPath() + "/settings.txt";

		create("custom_splash", null); // custom background image for start page
		create("storage_requested", false); // checks if the app has requested permission to read storage

		load();
		initialized = true;

		DebugLog.writeLine("initialized settings");
	}

	/**
	 * creates a setting.
	 *
	 * @param key
	 * @param defValue
	 */
	private static void create(final String key, final Object defValue) {
		final Pair<String, String> setting = new Pair<String, String>(null, null);
		setting.setFirst(String.valueOf(defValue));

		settingsStore.put(key, setting);
	}

	/**
	 * Loads settings from file.
	 */
	private static void load() {
		try {
			final BufferedReader buffer = new BufferedReader(new FileReader(settingsPath));
			if (buffer != null) {
				String line;
				while ((line = buffer.readLine()) != null) {
					if (line.contains("=")) {
						final String[] tmp = line.split("=");
						set(tmp[0], tmp[1]);
					}
				}

				buffer.close();
			}

			DebugLog.writeLine("settings read from file: " + settingsPath);
		} catch (final IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Writes custom settings to file.
	 */
	public static void commitToFile() {
		final StringBuilder sb = new StringBuilder();

		for (String key: settingsStore.keySet()) {
			String value = settingsStore.get(key).second();
			if (value != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(key + "=" + value.trim());
			}
		}

		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}

		try {
			final BufferedWriter buffer = new BufferedWriter(new FileWriter(settingsPath, false));
			if (buffer != null) {
				buffer.write(sb.toString());
				buffer.close();

				DebugLog.debug("settings written to file: " + settingsPath);
			}
		} catch (final IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Sets the custom value for a setting.
	 *
	 * @param key
	 * @param value
	 */
	public static void set(String key, final Object value) {
		key = key.trim();
		if (!settingsStore.containsKey(key)) {
			DebugLog.writeLine("Settings.set: unrecognized setting: " + key, DebugLevel.WARN);
			return;
		}

		final Pair<String, String> setting = settingsStore.get(key);
		setting.setSecond(String.valueOf(value).trim());
		settingsStore.put(key, setting);
	}

	/**
	 * Removes custom setting from settings store.
	 *
	 * @param key
	 */
	public static void unset(final String key) {
		if (!settingsStore.containsKey(key)) {
			DebugLog.writeLine("Settings.unset: unrecognized setting: " + key, DebugLevel.WARN);
			return;
		}

		final Pair<String, String> setting = settingsStore.get(key);
		setting.setSecond(null);
		settingsStore.put(key, setting);
	}

	/**
	 * Retrieves the string value of a setting.
	 *
	 * @param key
	 * @return
	 */
	public static String get(final String key) {
		if (!settingsStore.containsKey(key)) {
			return null;
		}

		final Pair<String, String> setting = settingsStore.get(key);
		String value = setting.second();
		if (value == null) {
			value = setting.second(); // default value
		}

		return value;
	}

	/**
	 * Alias for {ref get(String)}.
	 *
	 * @param key
	 */
	public static String getString(final String key) {
		return get(key);
	}

	/**
	 * Retrieves the integer value of a setting.
	 *
	 * @param key
	 * @return
	 */
	public static Integer getInteger(final String key) {
		return Integer.parseInt(get(key));
	}

	/**
	 * Retrieves the long value of a setting.
	 *
	 * @param key
	 * @return
	 */
	public static Long getLong(final String key) {
		return Long.parseLong(get(key));
	}

	/**
	 * Retrieves the double value of a setting.
	 *
	 * @param key
	 * @return
	 */
	public static Double getDouble(final String key) {
		return Double.parseDouble(get(key));
	}

	/**
	 * Retrieves the boolean value of a setting.
	 *
	 * @param key
	 * @return
	 */
	public static Boolean getBoolean(final String key) {
		return key != null && ((String) get(key)).toLowerCase().equals("true");
	}
}
