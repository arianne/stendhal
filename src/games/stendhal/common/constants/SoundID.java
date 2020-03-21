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
package games.stendhal.common.constants;


/**
 * IDs for sound events configured in the client.
 */
public enum SoundID {
	LEVEL_UP("level_up"),
	LEVEL_DOWN("level_down"),
	STAT_UP("stat_up"),
	STAT_DOWN("stat_down"),
	ACHIEVEMENT("achievement"),
	COMMERCE("commerce"),
	HEAL("heal");

	String label;

	private SoundID(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	public static SoundID getById(final String id) {
	    for(SoundID e : values()) {
	        if(e.label.equals(id)) return e;
	    }

	    return null;
	}
}
