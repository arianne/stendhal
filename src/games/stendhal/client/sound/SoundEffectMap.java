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

package games.stendhal.client.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectMap {

	/**
	 * the singleton instance initated by default
	 */
	private static final SoundEffectMap INSTANCE = new SoundEffectMap();

	/**
	 * @return the singleton instance
	 */
	static SoundEffectMap getInstance() {
		return INSTANCE;
	}

	/**
	 * stores the named sound effects
	 */
	private Map<String, Object> sfxmap = Collections.synchronizedMap(new HashMap<String, Object>(
			256));

	private Map<String, String> pathMap = Collections.synchronizedMap(new HashMap<String, String>(
			256));

	private Map<String, ClipRunner> clipRunnerMap = Collections.synchronizedMap(new HashMap<String, ClipRunner>(
			256));

	Object getByName(String name) {
		return sfxmap.get(name);
	}

	boolean containsKey(String key) {
		return sfxmap.containsKey(key);

	}

	Object put(String key, String value) {
		pathMap.put(key, value);
		return sfxmap.put(key, value);
	}

	Object put(String key, ClipRunner value) {
		clipRunnerMap.put(key, value);

		return sfxmap.put(key, value);

	}

	int size() {
		return sfxmap.size();
	}

	/**
	 * Returns a <code>ClipRunner</code> object ready to play a sound of the
	 * specified library sound name.
	 * 
	 * @param name
	 *            token of library sound
	 * @return <code>ClipRunner</code> or <b>null</b> if the sound is
	 *         undefined
	 */
	ClipRunner getSoundClip(String name) {

		// Object o = getByName(name);
		// if (o == null)
		// return null;
		//	
		// if (o instanceof ClipRunner) {
		// return (ClipRunner) o;
		// }
		//	
		// // load sounddata from soundfile
		// String path = (String) o;
		// return system.loadSoundDataFromFile(name, path);
		return (ClipRunner) getByName(name);
	}

}
