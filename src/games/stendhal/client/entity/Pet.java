/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import marauroa.common.game.RPObject;

/** A Pet entity. */
public class Pet extends DomesticAnimal {
	
	@Override
	public void initialize(RPObject object) {
		super.initialize(object);
	}

	@Override
	protected void probableChat(final int chance) {
	}
}
