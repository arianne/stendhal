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
package games.stendhal.server.entity.creature.impl;

public class DropItem {

	public String name;

	public double probability;

	public int min;

	public int max;

	public DropItem(final String name, final double probability, final int min, final int max) {
		this.name = name;
		this.probability = probability;
		this.min = min;
		this.max = max;
	}

	public DropItem(final String name, final double probability, final int amount) {
		this.name = name;
		this.probability = probability;
		this.min = amount;
		this.max = amount;
	}
}
