/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A singleton class handling configuration options.
 */
public final class TiledConfiguration {
	private static TiledConfiguration instance = null;
	private Properties settings;
	private boolean changed;

	private TiledConfiguration() {
		settings = new Properties();
		populateDefaults();
		try {
			parse("tiled.conf");
		} catch (Exception e) {
			System.out.println("Warning: could not load configuration file.");
		}
		changed = false;
	}

	/** returns changed state. */
	public boolean getChanged() {
		return changed;
	}

	/**
	 * Returns the tiled configuration class instance. Will create a new
	 * instance when it hasn't been created yet.
	 * 
	 * @return a reference to the singleton
	 */
	public static TiledConfiguration getInstance() {
		if (instance == null) {
			instance = new TiledConfiguration();
		}
		return instance;
	}

	/**
	 * Reads config settings from the given file.
	 * 
	 * @param filename
	 *            path of file to read configuration from
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void parse(String filename) throws FileNotFoundException, IOException {
		parse(new BufferedReader(new FileReader(filename)));
	}

	/**
	 * Reads config settings from the given buffered reader.
	 * 
	 * @param br
	 *            a
	 * @link{BufferedReader} opened on the config file
	 * @throws IOException
	 */
	public void parse(BufferedReader br) throws IOException {
		String line;
		while ((line = br.readLine()) != null) {
			// Make sure it isn't a comment
			if (!line.trim().startsWith("#") && line.trim().length() > 0) {
				String[] keyValue = line.split("[ ]*=[ ]*");
				if (keyValue.length > 1) {
					addConfigPair(keyValue[0], keyValue[1]);
				}
			}
		}
	}

	/**
	 * Returns wether the option is available in the config file.
	 * 
	 * @param name
	 *            the name of the option to check for
	 * @return <code>true</code> if the option has a non-<code>null</code>
	 *         value, <code>false</code> otherwise
	 */
	public boolean hasOption(String name) {
		return (settings.get(name) != null);
	}

	/**
	 * Returns the value of the given option.
	 * 
	 * @param option
	 * @return String The value of the specified option as a String
	 */
	public String getValue(String option) {
		return (String) settings.get(option);
	}

	/**
	 * Returns the integer value of the given option, or the given default when
	 * the option doesn't exist.
	 * 
	 * @param option
	 * @param def
	 * @return int The value of the specified option as an <code>int</code>
	 */
	public int getIntValue(String option, int def) {
		String str = getValue(option);
		if (str != null) {
			return Integer.parseInt(str);
		} else {
			return def;
		}
	}

	/**
	 * Returns wether a certain option equals a certain string, ignoring case.
	 */
	public boolean keyHasValue(String option, String comp) {
		String check = getValue(option);
		return (check != null && check.equalsIgnoreCase(comp));
	}

	/**
	 * Returns wether a certain option equals a certain integer.
	 */
	public boolean keyHasValue(String option, int comp) {
		return (hasOption(option) && getIntValue(option, 0) == comp);
	}

	/**
	 * Adds a config pair to the configuration.
	 */
	public void addConfigPair(String key, String value) {
		String prev = (String) settings.get(key);
		if (prev == null || !prev.equals(value)) {
			settings.put(key, value);
			changed = true;
		}
	}

	/**
	 * Removes a config pair from the configuration.
	 */
	public void remove(String key) {
		settings.remove(key);
	}

	/**
	 * Writes the current configuration to a file. Preserves comments and
	 * unknown options.
	 * 
	 * @param filename
	 *            the file to write the configuration to
	 */
	public void write(String filename) throws IOException, Exception {
		BufferedWriter bw;
		List<String> inputLines = new ArrayList<String>();
		Map<String, String> availableKeys = new HashMap<String, String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				inputLines.add(line);
			}

			br.close();
		} catch (IOException ioe) {
			// Although it's nice, it's not necessary to have a config file in
			// existence when we go to write the config
		}

		bw = new BufferedWriter(new FileWriter(filename));

		// Iterate through all existing lines in the file
		for(String line : inputLines) {
			// Make sure it isn't a comment
			if (!line.trim().startsWith("#") && line.trim().length() > 0) {
				String[] keyValue = line.split("[ ]*=[ ]*");
				availableKeys.put(keyValue[0], "Tiled is cool");
				if (hasOption(keyValue[0])) {
					bw.write(keyValue[0] + " = " + getValue(keyValue[0]));
					bw.newLine();
				} else {
					bw.write(line);
					bw.newLine();
				}
			} else {
				bw.write(line);
				bw.newLine();
			}
		}

		// Iterate through configuration options, saving the options that were
		// not yet in the file already.
		for(Object key : settings.keySet()) {
			if (!availableKeys.containsKey(key) && settings.get(key) != null) {
				bw.write(key.toString() + " = " + settings.get(key));
				bw.newLine();
			}
		}

		bw.close();
	}

	/**
	 * Sets the default values for pertinent properties.
	 */
	public void populateDefaults() {
		addConfigPair("tmx.save.embedImages", "1");
		addConfigPair("tmx.save.tileImagePrefix", "tile");
		addConfigPair("tmx.save.layerCompression", "1");
		addConfigPair("tmx.save.encodeLayerData", "1");
		addConfigPair("tmx.save.tileSetImages", "0");
		addConfigPair("tmx.save.embedtileSetImages", "0");
		addConfigPair("tiled.report.io", "0");
		addConfigPair("tiled.undo.depth", "30");
		addConfigPair("tiled.selection.color", "0x0000FF");
		addConfigPair("tiled.background.color", "0x404040");
		addConfigPair("tiled.cursorhighlight", "1");
		addConfigPair("tiled.grid.color", "0x000000");
		addConfigPair("tiled.grid.antialias", "1");
		addConfigPair("tiled.grid.opacity", "255");
		addConfigPair("tiled.plugins.dir", "plugins");
	}
}
