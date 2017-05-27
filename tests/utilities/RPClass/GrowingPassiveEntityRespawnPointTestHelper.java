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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.entity.mapstuff.spawner.GrowingPassiveEntityRespawnPoint;
import marauroa.common.game.RPClass;

public class GrowingPassiveEntityRespawnPointTestHelper {

	public static void generateRPClasses() {

		if (!RPClass.hasRPClass("growing_entity_spawner")) {
			PassiveEntityRespawnPointTestHelper.generateRPClasses();
			GrowingPassiveEntityRespawnPoint.generateRPClass();
		}
	}

	@Test
	public void testname() throws Exception {
		generateRPClasses();
		assertTrue(RPClass.hasRPClass("growing_entity_spawner"));

	}
}
