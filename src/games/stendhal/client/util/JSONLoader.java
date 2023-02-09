/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

import games.stendhal.client.sprite.DataLoader;


public class JSONLoader extends DataLoader {

	private static final Logger logger = Logger.getLogger(JSONLoader.class);

	public Object data;
	public Runnable onDataReady;


	public JSONLoader() {
		super();
	}

	public JSONLoader(final Runnable onDataReady) {
		super();
		this.onDataReady = onDataReady;
	}

	/**
	 * Loads JSON data from file.
	 *
	 * @param path
	 *     Path to JSON file.
	 */
	public void load(final String path) {
		final URL url = getResource(path);
		if (url != null) {
			try {
				final InputStreamReader isr = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
				data = JSONValue.parse(isr);
				if (data == null) {
					logger.error("Failed to read emoji data file: " + path);
				} else {
					if (onDataReady != null) {
						onDataReady.run();
					}
				}
			} catch (final IOException e) {
				logger.error("Error loading JSON data from file: " + path, e);
			}
		}
	}
}
