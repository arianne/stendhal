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
package games.stendhal.server.entity.npc.action;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class DropItemActionTest {

	@BeforeClass
	public static void beforeClass() {
		Log4J.init();
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	@Test
	public void testFire() {
		Player p = PlayerTestHelper.createPlayer("bob");
		PlayerTestHelper.equipWithItem(p, "axe");
		assertThat(Boolean.valueOf(p.isEquipped("axe")), is(Boolean.TRUE));
		DropItemAction action = new DropItemAction("axe");
		action.fire(p, null, null);
		assertThat(Boolean.valueOf(p.isEquipped("axe")), is(Boolean.FALSE));
	}

}
