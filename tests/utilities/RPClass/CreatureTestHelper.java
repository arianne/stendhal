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

import games.stendhal.server.entity.creature.Creature;
import marauroa.common.game.RPClass;
import utilities.PlayerTestHelper;

public class CreatureTestHelper {

	public static void generateRPClasses() {

		PlayerTestHelper.generateNPCRPClasses();

		if (!RPClass.hasRPClass("creature")) {
			Creature.generateRPClass();
		}

	}

}
