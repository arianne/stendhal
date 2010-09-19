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


import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import marauroa.common.game.RPClass;

public class ArrestWarrentTestHelper {
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass(ArrestWarrant.RPCLASS_NAME)) {
			ArrestWarrant.generateRPClass();
		}

	}

}
