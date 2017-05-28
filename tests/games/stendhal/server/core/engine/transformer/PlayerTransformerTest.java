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
package games.stendhal.server.core.engine.transformer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerTransformerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!RPClass.hasRPClass("entity")){
			Entity.generateRPClass();
		}
		if (!RPClass.hasRPClass("active_entity")){
			ActiveEntity.generateRPClass();
		}
		if (!RPClass.hasRPClass("rpentity")){
			RPEntity.generateRPClass();
		}
		if (!RPClass.hasRPClass("player")){
			Player.generateRPClass();
		}
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for transformUnderscore.
	 */
	@Test
	public void testTransformUnderscore() {
		RPObject obje = new RPObject();
		obje.put("name", "bob");
		obje.setID(new RPObject.ID(1,"testzone"));
		RPSlot slot = new RPSlot("bag");

		obje.addSlot(slot);
		slot.add(new Item("leather_armor","clazz","subclass",null));
		RPObject transObj = new PlayerTransformer().transform(obje);
		assertTrue(transObj.hasSlot("bag"));

		assertThat(transObj.getSlot("bag").getFirst().get("name"), is("leather armor"));
	}
	/**
	 * Tests for transformBind.
	 */
	@Test
	public void testTransformBind() {
		RPObject obje = new RPObject();
		obje.put("name", "bob");
		obje.setID(new RPObject.ID(1,"testzone"));
		RPSlot slot = new RPSlot("bag");

		obje.addSlot(slot);
		Item item = new Item("lich gold key","clazz","subclass",null);
		 slot.add(item);

		assertFalse(item.isBound());

		RPObject transObj = new PlayerTransformer().transform(obje);
		assertTrue(transObj.hasSlot("bag"));
		RPSlot bag = transObj.getSlot("bag");
		RPObject transItem = bag.getFirst();
		assertTrue(((Item) transItem).isBound());
	}
	/**
	 * Tests for transformUnBind.
	 */
	@Test
	public void testTransformUnBind() {
		RPObject obje = new RPObject();
		obje.put("name", "bob");
		obje.setID(new RPObject.ID(1,"testzone"));
		RPSlot slot = new RPSlot("bag");

		obje.addSlot(slot);

		Item item = new Item("marked scroll","clazz","subclass",null);
		item.setBoundTo("bob");
		 slot.add(item);
		assertTrue(item.isBound());

		RPObject transObj = new PlayerTransformer().transform(obje);
		assertTrue(transObj.hasSlot("bag"));
		RPSlot bag = transObj.getSlot("bag");
		RPObject transItem = bag.getFirst();
		assertFalse(((Item) transItem).isBound());
	}


}
