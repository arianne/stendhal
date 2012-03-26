/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * A list of ArrestWarrants as frontend for the zone storage.
 *
 * @author hendrik
 */
public class ArrestWarrantList extends StorableEntityList<ArrestWarrant> {

	/**
	 * Creates a new ArrestWarrantList.
	 *
	 * @param zone
	 *            zone to store the ArrestWarrants in
	 */
	public ArrestWarrantList(final StendhalRPZone zone) {
		super(zone, ArrestWarrant.class);
	}

	@Override
	public String getName(final ArrestWarrant arrestWarrant) {
		return arrestWarrant.getCriminal();
	}

	@Override
	public String toString() {
		final StringBuilder who = new StringBuilder();
		for (final ArrestWarrant aw : getList()) {
			who.append(aw.getCriminal());
			who.append(": ");
			who.append(aw.getMinutes());
			who.append(" Minutes because: ");
			who.append(aw.getReason());
			who.append("\n");
		}
		return who.toString();
	}
}
