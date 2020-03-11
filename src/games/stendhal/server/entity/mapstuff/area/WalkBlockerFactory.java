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
package games.stendhal.server.entity.mapstuff.area;


public class WalkBlockerFactory {

	public static WalkBlocker create(final int type) {
		final WalkBlocker blocker;

		switch (type) {
		case 0:
			blocker = new WalkBlocker();
			break;
		case 1:
			blocker = new FlyOverArea();
			break;
		default:
			blocker = null;
			break;
		}

		return blocker;
	}
}
