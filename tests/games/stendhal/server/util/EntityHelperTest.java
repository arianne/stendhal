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
package games.stendhal.server.util;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.BASEOBJECT;
import static games.stendhal.common.constants.Actions.BASESLOT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class EntityHelperTest {

	private static final String TEST_ENTITY_HELPER = "test_entity_helper";

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testEntityFromZoneByID() {
		int idRPO1 = 1;
		int idRPO2 = 2;
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		RPObject rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		zone.add(rpo1);
		RPObject rpo2 = new Entity() {};
		rpo2.setID(new RPObject.ID(idRPO2, zone.getID()));
		zone.add(rpo2);
		Entity entityFromZoneByID = EntityHelper.entityFromZoneByID(idRPO1, zone);
		assertThat(entityFromZoneByID, is(rpo1));
		assertThat(entityFromZoneByID, not((is(rpo2))));
	}

	@Test
	public void testEntityFromTargetName() {
		Entity player = new Entity() {};
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		zone.add(player);
		Entity entityFromTargetName = EntityHelper.entityFromTargetName("#3", player);
		assertThat(entityFromTargetName, not(notNullValue()));
		int idRPO1 = 1;
		Entity rpo1 = new Entity() {};
		RPObject rpo2 = new Entity() {};
		rpo2.put("test","test");
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		rpo1.addSlot("test");
		rpo1.getSlot("test").add(rpo2);
		zone.add(rpo1);
		zone.add(rpo2);
		Entity entityFromTargetName2 = EntityHelper.entityFromTargetName("#3", player);
		assertThat(entityFromTargetName2, is(rpo2));
	}

	@Test
	public void testEntityFromTargetNameAnyZone() {
		Entity player = new Entity() {};
		Entity entityFromTargetName = EntityHelper.entityFromTargetNameAnyZone("1", player);
		assertThat(entityFromTargetName, not(notNullValue()));
		int idRPO1 = 1;
		int idRPO2 = 2;
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		Entity rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(idRPO1, zone.getID()));
		zone.add(rpo1);
		RPObject rpo2 = new Entity() {};
		rpo2.setID(new RPObject.ID(idRPO2, zone.getID()));
		zone.add(rpo2);
		rpo1.addSlot("test");
		rpo1.getSlot("test").add(rpo2);
		Entity entityFromTargetName2 = EntityHelper.entityFromTargetNameAnyZone("#2", rpo1);
		assertThat(entityFromTargetName2, is(rpo2));
	}

	@Test
	public void testEntityFromSlot() {
		StendhalRPZone zone = new StendhalRPZone(TEST_ENTITY_HELPER);
		Player player = PlayerTestHelper.createPlayer("helpertester");
		player.setID(new RPObject.ID(2, zone.getID()));
		RPAction action = new RPAction();
		assertNull(EntityHelper.entityFromSlot(player, action));
		Entity rpo1 = new Entity() {};
		rpo1.setID(new RPObject.ID(1, TEST_ENTITY_HELPER));
		zone.add(rpo1);
		assertNull(EntityHelper.entityFromSlot(player, action));
		zone.add(player);
		player.getSlot("bag").add(rpo1);
		action.put(BASESLOT, "bag");
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASEITEM, rpo1.getID().getObjectID());
		assertThat(EntityHelper.entityFromSlot(player, action), is(rpo1));
	}

}
