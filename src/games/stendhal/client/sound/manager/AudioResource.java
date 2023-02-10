/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound.manager;

import java.io.InputStream;

import games.stendhal.client.sprite.DataLoader;

/**
 * the last remaining piece of the very complicated resource framework, which has been replaced by DataLoader.
 *
 * This class is a helper for refactoring, it should be made obsolate.
 */
public class AudioResource {
	private String name = null;

	/**
	 * creates a new AudioResource
	 *
	 * @param name name
	 */
	public AudioResource(String name) {
		this.name = name;
	}

	/**
	 * gets the input stream
	 *
	 * @return input stream
	 */
	public InputStream getInputStream() {
		InputStream is = DataLoader.getResourceAsStream("/data/sounds/" + name);
		if (is == null) {
			is = DataLoader.getResourceAsStream("/data/music/" + name);
		}
		return is;
	}

	/**
	 * gets the name.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}
}
