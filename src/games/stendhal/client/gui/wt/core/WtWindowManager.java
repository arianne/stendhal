/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.wt.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import games.stendhal.client.stendhal;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.common.MathHelper;
import marauroa.common.io.Persistence;

/**
 * This manager keeps track of all the windows and their positions/ minimized
 * state.
 *
 * @author mtotz
 */
public final class WtWindowManager {

	/** the logger instance. */
	private static final Logger logger = Logger
			.getLogger(WtWindowManager.class);

	/** filename for the settings persistence. */
	private static final String FILE_NAME = "windows.properties";

	/** the saved window positions. */
	private Properties properties;

	/** the instance. */
	private static WtWindowManager instance;

	/** maps the window names to their configs. */
	private final Map<String, WindowConfiguration> configs = new HashMap<String, WindowConfiguration>();

	/** Change listeners. */
	private final Map<String, List<SettingChangeListener>> listeners = new HashMap<String, List<SettingChangeListener>>();

	/** no public constructor. */
	private WtWindowManager() {
		// try to read the configurations from disk
		read();
	}

	/** @return the windowmanagers instance. */
	public static synchronized WtWindowManager getInstance() {
		if (instance == null) {
			instance = new WtWindowManager();
		}
		return instance;
	}

	/**
	 * Sets default window properties. These are used only when there are no
	 * properties known for this panel.
	 *
	 * @param name window identifier
	 * @param minimized <code>true</code> if the window is minimized
	 * @param x window x coordinate
	 * @param y window y coordinate
	 */
	public void setDefaultProperties(final String name, final boolean minimized, final int x,
			final int y) {
		if (!configs.containsKey(name)) {
			final WindowConfiguration config = new WindowConfiguration(name);
			config.readFromProperties(properties, minimized, x, y, true);
			configs.put(name, config);
		}
	}

	/** saves the current settings to a file. */
	public void save() {
		final StringBuilder buf = new StringBuilder();
		for (final WindowConfiguration config : configs.values()) {
			buf.append(config.writeToPropertyString());
		}
		for (final Object key : properties.keySet()) {
			if (key.toString().startsWith("config.")) {
				buf.append(key.toString() + "=" + properties.get(key) + "\n");
			}
		}

		// ISO-8859-1 is the charset that Properties.load() wants.
		try (OutputStream os = Persistence.get().getOutputStream(false,
				stendhal.getGameFolder(), FILE_NAME);
				OutputStreamWriter writer = new OutputStreamWriter(os, "ISO-8859-1")) {
			writer.append(buf.toString());
		} catch (final IOException e) {
			// ignore exception
			logger.error("Can't write " + stendhal.getGameFolder() + FILE_NAME, e);
		}
	}

	/** Reads the current settings from a file. */
	private void read() {
		properties = new Properties();
		try {
			final InputStream is = Persistence.get().getInputStream(false, stendhal.getGameFolder(),
					FILE_NAME);
			properties.load(is);
			is.close();
		} catch (final IOException e) {
			// ignore exception
		}
	}

	/**
	 * Get the configuration of a window.
	 *
	 * @param window the window whose configuration is wanted
	 * @return the configuration. If it does not exist yet, a new one is created.
	 */
	private WindowConfiguration getConfig(final ManagedWindow window) {
		final String name = window.getName();
		WindowConfiguration winC = configs.get(name);
		if (winC == null) {
			winC = new WindowConfiguration(name);
			winC.readFromProperties(properties, window);
			configs.put(name, winC);
		}
		return winC;
	}

	/**
	 * Returns a property.
	 *
	 * @param key
	 *            Key to look up
	 * @param defaultValue
	 *            default value which is returned if the key is not in the
	 *            configuration file
	 * @return value
	 */
	public String getProperty(final String key, final String defaultValue) {
		return properties.getProperty("config." + key, defaultValue);
	}

	/**
	 * Returns an integer property.
	 *
	 * @param key
	 *            Key to look up
	 * @param defaultValue
	 *            default value which is returned if the key is not in the
	 *            configuration file or not a valid integer
	 * @return value
	 */
	public int getPropertyInt(String key, int defaultValue) {
		String value = getProperty(key, null);
		if (value == null) {
			return defaultValue;
		}

		return MathHelper.parseIntDefault(value, defaultValue);
	}

	/**
	 * Returns a boolean property.
	 *
	 * @param key
	 *            Key to look up
	 * @param defaultValue
	 *            default value which is returned if the key is not in the
	 *            configuration file or not a valid boolean
	 * @return value
	 */
	public boolean getPropertyBoolean(String key, boolean defaultValue) {
		String value = getProperty(key, null);
		if (value == null) {
			return defaultValue;
		}

		return Boolean.parseBoolean(value);
	}

	/**
	 * Register a change listener for a specific configuration change.
	 *
	 * @param key configuration key to be watched
	 * @param listener listener for the changes
	 */
	public void registerSettingChangeListener(String key, SettingChangeListener listener) {
		String realKey = "config." + key;
		List<SettingChangeListener> list = listeners.get(realKey);
		if (list == null) {
			list = new ArrayList<SettingChangeListener>();
			listeners.put(realKey, list);
		}
		list.add(listener);
	}

	/**
	 * Deregister a change listener.
	 *
	 * @param key the key the listener was registered for
	 * @param listener listener to be removed
	 */
	public void deregisterSettingChangeListener(String key, SettingChangeListener listener) {
		List<SettingChangeListener> list = listeners.get("config. " + key);
		if (list != null) {
			list.remove(listener);
		}
	}

	/**
	 * Sets a property.
	 *
	 * @param key key
	 * @param value value
	 */
	public void setProperty(final String key, final String value) {
		String realKey = "config." + key;
		properties.setProperty(realKey, value);
		List<SettingChangeListener> list = listeners.get(realKey);
		if (list != null) {
			for (SettingChangeListener listener : list) {
				listener.changed(value);
			}
		}
	}

	/**
	 * Apply a saved configuration to a window. Nothing happens when this
	 * windows configuration is not known.
	 *
	 * @param window the window
	 */
	public void formatWindow(final ManagedWindow window) {
		final WindowConfiguration config = getConfig(window);

		window.moveTo(config.x, config.y);
		window.setMinimized(config.minimized);
		window.setVisible(config.visible);
	}

	/**
	 * Notify that a window has moved.
	 *
	 * @param window the window that moved
	 * @param x new x coordinate
	 * @param y new y coordinate
	 */
	public void moveTo(final ManagedWindow window, final int x, final int y) {
		final WindowConfiguration config = getConfig(window);
		config.x = x;
		config.y = y;
	}

	/**
	 * Notify a window's minimized state has changed.
	 *
	 * @param window changed window
	 * @param state new minimization state. <code>true</code> if minimized,
	 * 	<code>false</code> otherwise
	 */
	public void setMinimized(final ManagedWindow window, final boolean state) {
		final WindowConfiguration config = getConfig(window);

		config.minimized = state;
	}

	/** encapsulates the configuration of a window. */
	private static final class WindowConfiguration {
		/** name of the window. */
		private String name;
		/** minimized state of the window. */
		private boolean minimized;
		/** is the window visible? */
		private boolean visible;
		/** x-pos. */
		private int x;
		/** y-pos. */
		private int y;

		/**
		 * Create configuration for a window.
		 *
		 * @param name window identifier
		 */
		private WindowConfiguration(final String name) {
			this.name = name;
		}

		/**
		 * @return string to be stored as property
		 */
		private String writeToPropertyString() {
			return "window." + name + ".minimized=" + minimized + "\n"
					+ "window." + name + ".visible=" + visible + "\n"
					+ "window." + name + ".x=" + x + "\n" + "window." + name
					+ ".y=" + y + "\n";
		}

		@Override
		public String toString() {
			return writeToPropertyString();
		}

		/**
		 * Read window configuration from properties.
		 *
		 * @param props properties
		 * @param defaultMinimized default minimization state <code>true</code>
		 * 	for minimized windows, <code>false</code> for others
		 * @param defaultX default x coordinate
		 * @param defaultY default y coordinate
		 * @param defaultVisible default visibility state <code>true</code> for
		 * 	visible windows, <code>false</code> for others
		 */
		private void readFromProperties(final Properties props,
				final boolean defaultMinimized, final int defaultX, final int defaultY,
				final boolean defaultVisible) {
			minimized = Boolean.parseBoolean(props.getProperty("window." + name
					+ ".minimized", Boolean.toString(defaultMinimized)));
			visible = Boolean.parseBoolean(props.getProperty("window." + name
					+ ".visible", Boolean.toString(defaultVisible)));
			x = Integer.parseInt(props.getProperty("window." + name + ".x",
					Integer.toString(defaultX)));
			y = Integer.parseInt(props.getProperty("window." + name + ".y",
					Integer.toString(defaultY)));
		}

		/**
		 * Read window configuration from properties.
		 *
		 * @param props properties
		 * @param defaults default properties
		 */
		private void readFromProperties(final Properties props, final ManagedWindow defaults) {
			readFromProperties(props, defaults.isMinimized(), defaults.getX(),
					defaults.getY(), defaults.isVisible());
		}
	}
}
