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
package games.stendhal.server.maps;

import games.stendhal.server.core.engine.RPClassGenerator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import marauroa.common.game.RPObject;

public class MockStendlRPWorld extends StendhalRPWorld {

	@Override
	public void modify(final RPObject object) {
	}

	protected void createRPClasses() {
		new RPClassGenerator().createRPClasses();
	}

	public static StendhalRPWorld get() {
		if (!(instance instanceof MockStendlRPWorld)) {
			instance = new MockStendlRPWorld();
			((MockStendlRPWorld) instance).initialize();
		}
		return instance;
	}

	public static void  reset() {
		instance = null;
	}

}
