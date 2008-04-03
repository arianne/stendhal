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
package games.stendhal.client.gui;

import games.stendhal.client.stendhal;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import marauroa.common.io.Persistence;

import org.apache.log4j.Logger;

/**
 * This manager keeps track of all the windows and their positions/minimized
 * state.
 * 
 * @author mtotz
 */
public class PropertyManager {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(PropertyManager.class);

	/** filename for the settings persistence. */
	private static final String FILE_NAME = "stendhal.properties";

	/** the saved window positions. */
	private Properties properties;

	/** the instance. */
	private static PropertyManager instance;

	/** maps the window names to their configs. */
	private Map<String, WindowConfiguration> configs = new HashMap<String, WindowConfiguration>();

	/** no public constructor. */
	private PropertyManager() {
		// try to read the configurations from disk
		read();
	}

	/** returns the ProperyManager instance. */
	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}

		return instance;
	}

	/**
	 * Sets default window properties. These are used only when there are no
	 * properties known for this panel.
	 */
	public void setDefaultProperties(String name, boolean minimized, int x, int y) {
		if (!configs.containsKey(name)) {
			WindowConfiguration config = new WindowConfiguration(name);
			config.readFromProperties(properties, minimized, x, y, true);
			configs.put(name, config);
		}
	}

	/** saves the current settings to a file. */
	public void save() {
		StringBuilder buf = new StringBuilder();

		for (WindowConfiguration config : configs.values()) {
			buf.append(config.writeToPropertyString());
		}

		for (Object key : properties.keySet()) {
			if (key.toString().startsWith("config.")) {
				buf.append(key.toString() + "=" + properties.get(key) + "\n");
			}
		}

		try {
			OutputStream os = Persistence.get().getOutputStream(true, "stendhal", FILE_NAME);
			OutputStreamWriter writer = new OutputStreamWriter(os);
			writer.append(buf.toString());
			writer.close();
		} catch (IOException e) {
			// ignore exception
			logger.error("Can't write " + stendhal.STENDHAL_FOLDER + FILE_NAME, e);
		}
	}

	/** Reads the current settings from a file. */
	public void read() {
		properties = new Properties();

		try {
			InputStream is = Persistence.get().getInputStream(true, "stendhal", FILE_NAME);
			properties.load(is);
			is.close();
		} catch (IOException e) {
			// ignore exception
		}
	}

	/** Returns the config. If it does not exist yet, a new one is created. */
	private WindowConfiguration getConfig(ClientPanel panel) {
		String name = panel.getName();
		WindowConfiguration winC = configs.get(name);

		if (winC == null) {
			winC = new WindowConfiguration(name);
			winC.readFromProperties(properties, panel);
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
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty("config." + key, defaultValue);
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
	public String setProperty(String key, String defaultValue) {
		return properties.getProperty("config." + key, defaultValue);
	}

	/**
	 * Formats the window with the saved config. Nothing happens if this
	 * window config is not known.
	 */
	public void formatWindow(ClientPanel panel) {
		WindowConfiguration config = getConfig(panel);
		if (config == null) {
			// window not supervised
			return;
		}

		panel.setLocation(config.x, config.y);

		try {
            panel.setIcon(config.minimized);
        } catch(PropertyVetoException e) {
        }

		panel.setVisible(config.visible);
	}

	/** the panel was moved, so update the internal representation. */
	public void moveTo(ClientPanel panel, int x, int y) {
		WindowConfiguration config = getConfig(panel);

		config.x = x;
		config.y = y;
	}

	/** the panels minimized state changed, update the internal representation. */
	public void setMinimized(ClientPanel panel, boolean state) {
		WindowConfiguration config = getConfig(panel);

		config.minimized = state;
	}

	public void setVisible(ClientPanel panel, boolean state) {
		WindowConfiguration config = getConfig(panel);

		config.visible = state;
	}

}
