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
package games.stendhal.server.entity.player;

import java.awt.Point;

class Cell {
	private final Point entry;
	private String inmateName = "";

	protected Cell(final Point entry) {
		this.entry = entry;
	}

	protected boolean remove(final String name) {
		if (this.inmateName.equalsIgnoreCase(name)) {
			this.inmateName = "";
			return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return "".equals(inmateName);

	}

	public Point getEntry() {
		return entry;
	}

	protected boolean add(final String string) {
		if (isEmpty()) {
			inmateName = string;
			return true;
		}
		return false;
	}
}
