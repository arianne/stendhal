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
package utilities.RPClass;

import games.stendhal.server.entity.creature.BabyDragon;
import marauroa.common.game.RPClass;

public class BabyDragonTestHelper {


public static void generateRPClasses() {

		PetTestHelper.generateRPClasses();

		if (!RPClass.hasRPClass("baby_dragon")) {
			BabyDragon.generateRPClass();
		}

	}

}
