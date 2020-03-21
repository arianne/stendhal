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
package games.stendhal.client.sound;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.constants.SoundID;


public class ConfiguredSounds {

	private static final Map<SoundID, String> sounds = new HashMap<SoundID, String>() {{
		put(SoundID.LEVEL_UP, "player/level_up-01");
		put(SoundID.STAT_UP, "player/stat_up-01");
		put(SoundID.ACHIEVEMENT, "yay-1");
		put(SoundID.COMMERCE, "coins-01");
		put(SoundID.HEAL, "heal-01");
	}};


	public static String get(final SoundID id) {
		return sounds.get(id);
	}
}
