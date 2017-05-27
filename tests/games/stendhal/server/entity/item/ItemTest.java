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
package games.stendhal.server.entity.item;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class ItemTest {

	private static final String ZONE_NAME = "ITEMTESTZONE";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();

	}

	/**
	 * Tests for getName.
	 */
	@Test
	public void testGetName() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals("name1", mo.getName());
	}

	/**
	 * Tests for getAreaRectangle2DDoubleDouble.
	 */
	@Test
	public void testGetAreaRectangle2DDoubleDouble() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Rectangle2D rect = new Rectangle2D.Double();
		assertEquals(rect.getCenterX(), 0.0, 0.001);
		assertEquals(rect.getCenterY(), 0.0, 0.001);

		rect = mo.getArea(0.0, 0.0);
		assertEquals(rect.getMinX(), 0.0, 0.001);
		assertEquals(rect.getMinY(), 0.0, 0.001);
		assertEquals(rect.getMaxX(), 1.0, 0.001);
		assertEquals(rect.getMaxY(), 1.0, 0.001);

	}

	/**
	 * Tests for clone.
	 */
	@Test
	public void testClone() {
		Map<String, String> attribs = new HashMap<String, String>();
		attribs.put("att_1", "val_1");
		attribs.put("att_2", "val_2");
		Item it1 = new Item("name", "class", "subclass", attribs);
		Object it2 = it1.clone();
		assertFalse(it1 == it2);
		assertTrue(it2.getClass() == Item.class);
		assertTrue(it2.getClass() == it1.getClass());
		assertEquals(it1, it2);
		assertEquals("val_1", ((RPObject) it2).get("att_1"));
		assertEquals("val_2", ((RPObject) it2).get("att_2"));

	}

	/**
	 * Tests for getDescription.
	 */
	@Test
	public void testGetDescription() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals("", mo.getDescription());
	}


	/**
	 * Tests for describe.
	 */
	@Test
	public void testDescribe() {
		final Item item = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertThat(item.describe(), equalTo("You see a §'name1'."));

		item.setDescription("Description.");
		item.setBoundTo("hero");
		item.put("min_level", 1);
		item.put("atk", 2);
		item.put("def", 3);
		item.put("rate", 4);
		item.put("amount", 5);
		item.put("range", 6);
		item.put("lifesteal", 7);

		assertThat(item.describe(), equalTo("Description. It is a special reward for hero, and cannot be used by others. Stats are (ATK: 2 DEF: 3 RATE: 4 HP: 5 RANGE: 6 LIFESTEAL: 7 MIN-LEVEL: 1)."));

		item.setDamageType(Nature.FIRE);
		assertThat(item.describe(), equalTo("Description. It is a special reward for hero, and cannot be used by others. Stats are (ATK: 2 [FIRE] DEF: 3 RATE: 4 HP: 5 RANGE: 6 LIFESTEAL: 7 MIN-LEVEL: 1)."));
	}


	/**
	 * Tests for itemStringStringStringMapOfStringString.
	 */
	@Test
	public void testItemStringStringStringMapOfStringString() {
		final Map<String, String> attribs = new HashMap<String, String>();
		attribs.put("att_1", "val_1");
		attribs.put("att_2", "val_2");
		final Item mo = new Item("name1", "class", "subclass", attribs);
		assertEquals("val_1", mo.get("att_1"));
		assertEquals("val_2", mo.get("att_2"));
		assertNull(mo.get("Noexistant"));
	}

	/**
	 * Tests for itemItemwithAttributes.
	 */
	@Test
	public void testItemItemwithAttributes() {
		final Map<String, String> attribs = new HashMap<String, String>();

		attribs.put("att_1", "val_1");
		attribs.put("att_2", "val_2");
		final Item mo = new Item("name1", "class", "subclass", attribs);
		assertEquals("val_1", mo.get("att_1"));
		assertEquals("val_2", mo.get("att_2"));
		final Item itemcopy = new Item(mo);
		assertEquals("val_1", itemcopy.get("att_1"));
		assertEquals("val_2", itemcopy.get("att_2"));
	}

	@Test
	// slots are copied by copy constructor
	/**
	 * Tests for itemItem.
	 */
	public void testItemItem() {
		final LinkedList<String> slots = new LinkedList<String>();
		slots.add("slot_1");
		slots.add("slot_2");
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
	}

	/**
	 * Tests for setEquipableSlots.
	 */
	@Test
	public void testSetEquipableSlots() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		final LinkedList<String> slots = new LinkedList<String>();
		slots.add("one");
		slots.add("two");
		slots.add("three");
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
		slots.add("one");
		slots.add("one");
		slots.add("one");
		mo.setEquipableSlots(slots);
		assertEquals(slots, mo.getPossibleSlots());
	}

	/**
	 * Tests for getAttack.
	 */
	@Test
	public void testGetAttack() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals(0, mo.getAttack());
		mo.put("atk", 3);
		assertEquals(3, mo.getAttack());
		mo.put("atk", 2);
		assertEquals(2, mo.getAttack());
	}

	/**
	 * Test getting damage type.
	 */
	@Test
	public void testGetDamageType() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		// Default to cut
		assertEquals(Nature.CUT, mo.getDamageType());
		mo.setDamageType(Nature.ICE);
		assertEquals(Nature.ICE, mo.getDamageType());
		mo.setDamageType(Nature.FIRE);
		assertEquals(Nature.FIRE, mo.getDamageType());

		// Check that damage type gets copied
		Item copy = new Item(mo);
		assertEquals("Damage type should be copied", Nature.FIRE, copy.getDamageType());
	}

	/**
	 * Test getting susceptibility
	 */
	@Test
	public void testGetSusceptibility() {
		Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		for (Nature type : Nature.values()) {
			assertThat("Default susceptibility", mo.getSusceptibility(type), closeTo(1.0, 0.00001));
		}
		HashMap<Nature, Double> sus = new HashMap<Nature, Double>();
		mo.setSusceptibilities(sus);
		for (Nature type : Nature.values()) {
			sus.put(type, 0.42);
			for (Nature type2 : Nature.values()) {
				double expected = 1.0;
				if (type == type2) {
					expected = 0.42;
				}
				assertThat(mo.getSusceptibility(type2), closeTo(expected, 0.00001));
			}
			sus.remove(type);
		}
	}

	/**
	 * Tests for getDefense.
	 */
	@Test
	public void testGetDefense() {
		final Item mo = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		assertEquals(0, mo.getDefense());
		mo.put("def", 3);
		assertEquals(3, mo.getDefense());
		mo.put("def", 2);
		assertEquals(2, mo.getDefense());
	}

	/**
	 * Tests for isPersistent.
	 */
	@Test
	public void testIsPersistent() {
		final Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertFalse(mo.isPersistent());
		mo.put("persistent", 1);
		assertTrue(mo.isPersistent());
		mo.put("persistent", 2);
		assertFalse(mo.isPersistent());
	}

	/**
	 * Tests for isOfClass.
	 */
	@Test
	public void testIsOfClass() {
		final Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertTrue(mo.isOfClass("myClass"));
	}

	/**
	 * Tests for getItemClass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetItemClass() {
		final Item mo = new Item("name1", "myClass", "subclass",
				new HashMap<String, String>());
		assertEquals("myClass", mo.getItemClass());
		new Item("name1", null, "subclass", new HashMap<String, String>());

	}

	/**
	 * Tests for getItemSubclass.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetItemSubclass() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertEquals("mySubclass", mo.getItemSubclass());
		new Item("name1", "myClass", null, new HashMap<String, String>());
	}

	/**
	 * Tests for getQuantity.
	 */
	@Test
	public void testGetQuantity() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertEquals("defaultquantity", 1, mo.getQuantity());
	}

	/**
	 * Tests for getQuantityOneSureness.
	 */
	@Test
	public void testGetQuantityOneSureness() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("quantity", "0");
		final StackableItem mo = new StackableItem("name1", "myClass", "mySubclass",
				map);
		assertEquals("default", 1, mo.getQuantity());
	}

	/**
	 * Tests for getPossibleSlots.
	 */
	@Test
	public void testGetPossibleSlots() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertTrue(mo.getPossibleSlots().isEmpty());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		// ignore attribute listing because their sort order is not reliable
		assertTrue(mo.toString(), mo.toString().contains("Item, RPObject with Attributes of Class(item):"));
	}

	/**
	 * Tests for onPutOnGround.
	 */
	@Test
	public void testOnPutOnGround() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onPutOnGround(PlayerTestHelper.createPlayer("player"));
	}

	/**
	 * Tests for onRemoveFromGround.
	 */
	@Test
	public void testOnRemoveFromGround() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		mo.onRemoveFromGround();
	}

	/**
	 * Tests for onTurnReached.
	 */
	@Test
	public void testOnTurnReached() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));
		mo.onTurnReached(1);
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));
	}

	/**
	 * Tests for removeOne.
	 */
	@Test
	public void testRemoveOne() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));
		mo.removeOne();
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));


	}

	/**
	 * Tests for canBeEquippedIn.
	 */
	@Test
	public void testCanBeEquippedIn() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertTrue("ground is null", mo.canBeEquippedIn(null));
		final LinkedList<String> slots = new LinkedList<String>();
		slots.add("one");
		slots.add("two");
		slots.add("three");

		mo.setEquipableSlots(slots);
		assertTrue(mo.canBeEquippedIn("one"));
		assertTrue(mo.canBeEquippedIn("two"));
		assertTrue(mo.canBeEquippedIn("three"));
		assertFalse(mo.canBeEquippedIn("four"));
	}

	/**
	 * Tests for removeFromWorld.
	 */
	@Test
	public void testRemoveFromWorld() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);
		zone.add(mo);
		assertTrue(zone.has(mo.getID()));

		mo.removeFromWorld();
		assertNotNull(mo.getZone());
		assertFalse(zone.has(mo.getID()));
	}

	/**
	 * Tests for getBoundTo.
	 */
	@Test
	public void testGetBoundTo() {
		final Item mo = new Item("name1", "myClass", "mySubclass",
				new HashMap<String, String>());
		assertNull(mo.getBoundTo());
		mo.setBoundTo("bob");
		assertTrue(mo.isBound());
		assertTrue(PlayerTestHelper.createPlayer("bob").isBoundTo(mo));
		assertEquals("bob", mo.getBoundTo());

		mo.setBoundTo(null);
		assertFalse(mo.isBound());
		assertFalse(PlayerTestHelper.createPlayer("bob").isBoundTo(mo));
		assertThat(mo.getBoundTo(), not(is("bob")));
	}

	/**
	 * Test that all items that are storable to "content" have a copy
	 * constructor. Bank chests need those, and the items may otherwise seem to
	 * work correctly, but putting to them to bank fails.
	 */
	@Test
	public void testCopyConstructors() {
		EntityManager manager = SingletonRepository.getEntityManager();
		if (manager instanceof DefaultEntityManager) {
			for (String itemName : ((DefaultEntityManager) manager).getConfiguredItems()) {
				Item item = manager.getItem(itemName);
				// Only items that can be placed in chests need to be checked
				if (item.canBeEquippedIn("content")) {
					Object clone = null;
					try {
						Class<?> clazz = item.getClass();
						Constructor<?> ctor = clazz.getConstructor(clazz);
						clone = ctor.newInstance(item);
					} catch (Exception e) {
						fail("copying " + item.getName() + " failed: " + e.toString());
					}
					assertEquals(item, clone);
					assertFalse(item == clone);
				}
			}
		} else {
			fail("Unable to test copy constructors");
		}
	}
}
