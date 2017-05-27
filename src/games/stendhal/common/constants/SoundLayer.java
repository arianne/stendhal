/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

/**
 * the layer on which a sound is played
 *
 * @author hendrik
 */
public enum SoundLayer {

	/** nice background music which is considered out-of-game */
	BACKGROUND_MUSIC("music"),

	/**
	 * sounds that are related to the zone and are in-game,
	 * like a waterfall, wind, the hammering at the blacksmith
	 */
	AMBIENT_SOUND("ambient"),

	/** noise made by creatures and NPCs */
	CREATURE_NOISE("creature"),

	/** noise made by weapons and armor */
	FIGHTING_NOISE("sfx"),

	/** user interface feedback, opening of windows, private message notification */
	USER_INTERFACE("gui");

	/** the internal name of the sound layer */
	public final String groupName;

	/**
	 * creates an instance of SoundLayer
	 *
	 * @param groupName name of group
	 */
	private SoundLayer(String groupName) {
		this.groupName = groupName;
	}
}
