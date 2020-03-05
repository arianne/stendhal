/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

public class TrainingDummyFactory {

	/**
	 * Creates a new TrainingDummy entity.
	 *
	 * @param type
	 * 		Entity class to use: 0) training dummy; 1) bullseye
	 */
	public static TrainingDummy create(final int type) {
		final TrainingDummy dummy;

		switch (type) {
		case 0:
			dummy = new TrainingDummy();
			dummy.setMeleeOnly(true);
			break;
		case 1:
			dummy = new TrainingDummy("other/bullseye", "You see a bullseye.");
			break;
		default:
			dummy = null;
			break;
		}

		return dummy;
	}
}
