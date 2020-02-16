/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.entity.RPEntity;

/**
 * A OneWayPortalDestination is an invisible point where players are placed when
 * they use a portal that leads there. One cannot interact with
 * OneWayPortalDestinations in any other way.
 */
public class OneWayPortalDestination extends Portal {

	/**
	 * Creates a OneWayPortalDestination.
	 */
	public OneWayPortalDestination() {
		setHidden(true);
		setResistance(0);
	}

	/**
	 * Cannot be used, as one way portal destinations are only destinations of
	 * other portals.
	 */
	@Override
	public void setDestination(final String zone, final Object number) {
		throw new IllegalArgumentException(
				"One way portal destinations are only destinations of other portals");
	}

	@Override
	public boolean loaded() {
		// Always loaded
		return true;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		return false;
	}
}
