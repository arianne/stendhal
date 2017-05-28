/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * read the configuration file for the client.
 *
 * @author hendrik
 */
public class ClientGameConfiguration {
	private static ClientGameConfiguration instance;

	private Properties gameConfig = getGameProperties();

	private ClientGameConfiguration() {
	}

	private Properties getGameProperties() {
		try {
			InputStream is =  ClientGameConfiguration.class
					.getResourceAsStream("game.properties");
			if (is == null) {
				is = ClientGameConfiguration.class
				.getResourceAsStream("game-default.properties");
			}
			if (is == null) {
				throw new FileNotFoundException("Cannot read either game.properties or game-default.properties from classpath.");
			}

			Properties config = new Properties();
			try {
				config.load(is);
			} finally {
				is.close();
			}
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static synchronized void init() {
		if (instance == null) {
			instance = new ClientGameConfiguration();
		}
	}

	/**
	 * gets a configuration value, in case it is undefined, the default of
	 * game-default.properties is returned. If this is undefined, too, the
	 * return value is null
	 *
	 * @param key
	 *            key
	 * @return configured value
	 */
	public static String get(final String key) {
		init();
		return instance.gameConfig.getProperty(key);
	}
}
