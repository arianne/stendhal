/***************************************************************************
 *                  (C) Copyright 2013 - Faiumoni e. V.                    *
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


/**
 * prevents teleporting out of the specified area
 *
 * @author hendrik
 */
public class NoTeleportOutBehaviour implements AreaBehaviour {

	@Override
	public void addToWorld(AreaEntity area) {
		area.getZone().disallowOut(area.getX(), area.getY(), (int) area.getWidth(), (int) area.getHeight());
	}

	@Override
	public void removeFromWorld() {
		// TODO: implement me
	}
}
