/***************************************************************************
 *                (C) Copyright 2003-2012 - Faiumoni e. V.                 *
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
 * hides the area from the client
 *
 * @author hendrik
 */
public class HideAreaBehaviour implements AreaBehaviour {

	@Override
	public void addToWorld(AreaEntity parentAreaEntity) {
		parentAreaEntity.hide();
	}

	@Override
	public void removeFromWorld() {
		return;
	}

}
