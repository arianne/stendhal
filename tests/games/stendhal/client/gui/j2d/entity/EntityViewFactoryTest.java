/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.common.Triple;
import marauroa.common.Log4J;

public class EntityViewFactoryTest {

	private static final Logger logger = Logger.getLogger(EntityViewFactoryTest.class);

	/**
	 * Entity representation.
	 */
	private class EntityRep {
		private final String classname;
		private final String subclass;
		private final String name;
		private final Class<?> implementation;


		private EntityRep(final String classname, final String subclass, final String name,
				final Class<?> implementation) {
			this.classname = classname;
			this.subclass = subclass;
			this.name = name;
			this.implementation = implementation;
		}

		@SuppressWarnings("unused")
		private String getName() {
			return name != null ? name : subclass != null ? subclass : classname;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + EntityViewFactoryTest.this.hashCode();
			result = prime * result + Objects.hash(classname, implementation, name, subclass);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof EntityRep)) {
				return false;
			}
			final EntityRep other = (EntityRep) obj;
			boolean res = classname.equals(other.classname);
			res = res && (subclass == null ? other.subclass == null : subclass.equals(other.subclass));
			res = res && (name == null ? other.name == null : name.equals(other.name));
			//~ res = res && implementation.equals(other.implementation);
			return res;
		}

		@Override
		public String toString() {
			return classname + ", " + subclass + ", " + name + " (" + implementation.getSimpleName() + ")";
		}
	}

	// registered entity views
	private static Map<Triple<String, String, String>, Class<? extends EntityView>> entityViews;

	// registered entities representations to be tested
	private static List<EntityRep> entities;


	@Before
	public void setUp() throws Exception {
		Log4J.init();

		//GameScreen.setDefaultScreen(new MockScreen());

		// populate list of entities to test
		entityViews = EntityViewFactory.getViewMap();
		entities = new ArrayList<>();

		// entities configured in view factory
		for (final Map.Entry<Triple<String, String, String>, Class<? extends EntityView>> entry: entityViews.entrySet()) {
			final Triple<String, String, String> key = entry.getKey();
			entities.add(new EntityRep(key.getFirst(), key.getSecond(), key.getThird(), entry.getValue()));
		}
	}

	@SuppressWarnings("unused")
	private Class<?> getImplementation(final String type_name, final String class_name, final String name, final Class<?> def) {
		Class<?> implementation = def;
		final Triple<String, String, String> item_info = new Triple<>(type_name, class_name, name);
		// attempt to retrieve from factory
		if (entityViews.containsKey(item_info)) {
			implementation = entityViews.get(item_info);
		}
		return implementation;
	}

	/**
	 * Tests for all entity views.
	 */
	@Test
	public void testAll() {
		// item
		checkImplementation("item", null, null, Item2DView.class);
		checkImplementation("item", "book", "bestiary", UseableGenericItem2DView.class);
		checkImplementation("item", "box", null, Box2DView.class);
		checkImplementation("item", "club", "wizard_staff", UseableItem2DView.class);
		//~ checkImplementation("item", "crystal", null, Item2DView.class);
		checkImplementation("item", "drink", null, UseableItem2DView.class);
		checkImplementation("item", "food", null, UseableItem2DView.class);
		checkImplementation("item", "furniture", "picture in wooden frame", UseableItem2DView.class);
		checkImplementation("item", "misc", "bulb", UseableItem2DView.class);
		checkImplementation("item", "misc", "seed", UseableItem2DView.class);
		checkImplementation("item", "misc", "snowglobe", UseableGenericItem2DView.class);
		checkImplementation("item", "misc", "teddy", UseableGenericItem2DView.class);
		checkImplementation("item", "ring", null, Ring2DView.class);
		checkImplementation("item", "ring", "emerald ring", BreakableRing2DView.class);
		checkImplementation("item", "ring", "wedding", UseableRing2DView.class);
		checkImplementation("item", "scroll", null, UseableItem2DView.class);
		checkImplementation("item", "special", "mithril clasp", Item2DView.class);
		checkImplementation("item", "tool", "foodmill", UseableItem2DView.class);
		checkImplementation("item", "tool", "metal detector", UseableGenericItem2DView.class);
		checkImplementation("item", "tool", "rotary cutter", UseableGenericItem2DView.class);
		checkImplementation("item", "tool", "scroll eraser", UseableItem2DView.class);
		checkImplementation("item", "tool", "sugarmill", UseableItem2DView.class);

		// grower
		checkImplementation("growing_entity_spawner", null, null, GrainField2DView.class);
		checkImplementation("growing_entity_spawner", "items/grower/carrot_grower", null, CarrotGrower2DView.class);
		checkImplementation("growing_entity_spawner", "items/grower/wood_grower", null, CarrotGrower2DView.class);
		checkImplementation("plant_grower", null, null, PlantGrower2DView.class);

		// sign
		checkImplementation("blackboard", null, null, Sign2DView.class);
		checkImplementation("rented_sign", null, null, Sign2DView.class);
		checkImplementation("shop_sign", null, null, ShopSign2DView.class);
		checkImplementation("sign", null, null, Sign2DView.class);

		// portal & door
		checkImplementation("door", null, null, Door2DView.class);
		checkImplementation("gate", null, null, Gate2DView.class);
		checkImplementation("house_portal", null, null, HousePortal2DView.class);
		checkImplementation("portal", null, null, Portal2DView.class);

		// NPC
		checkImplementation("baby_dragon", null, null, Pet2DView.class);
		checkImplementation("cat", null, null, Pet2DView.class);
		checkImplementation("npc", null, null, NPC2DView.class);
		checkImplementation("pet", null, null, Pet2DView.class);
		checkImplementation("purple_dragon", null, null, Pet2DView.class);
		checkImplementation("sheep", null, null, Sheep2DView.class);
		checkImplementation("training_dummy", null, null, TrainingDummy2DView.class);

		// creature
		checkImplementation("creature", null, null, Creature2DView.class);
		checkImplementation("creature", "ent", null, BossCreature2DView.class);

		// resource sources
		checkImplementation("fish_source", null, null, UseableEntity2DView.class);
		checkImplementation("gold_source", null, null, UseableEntity2DView.class);
		checkImplementation("well_source", null, null, UseableEntity2DView.class);

		// misc
		checkImplementation("area", null, null, InvisibleEntity2DView.class);
		checkImplementation("block", null, null, LookableEntity2DView.class);
		checkImplementation("blood", null, null, Blood2DView.class);
		checkImplementation("chest", null, null, Chest2DView.class);
		checkImplementation("corpse", null, null, Corpse2DView.class);
		checkImplementation("fire", null, null, UseableEntity2DView.class);
		checkImplementation("flyover", null, null, FlyOverArea2DView.class);
		checkImplementation("food", null, null, SheepFood2DView.class);
		checkImplementation("game_board", null, null, GameBoard2DView.class);
		checkImplementation("player", null, null, Player2DView.class);
		checkImplementation("spell", null, null, Spell2DView.class);
		checkImplementation("useable_entity", null, null, UseableEntity2DView.class);
		checkImplementation("walkblocker", null, null, WalkBlocker2DView.class);
		checkImplementation("wall", null, null, Wall2DView.class);


		final int incomplete = entities.size();
		if (incomplete > 0) {
			String msg = "";
			for (final EntityRep erep: entities) {
				if (msg.length() > 0) {
					msg += "; ";
				}
				msg += erep.toString();
			}
			fail("the following " + incomplete + " entities were not tested: " + msg);
		}
	}

	private void checkImplementation(final String type_name, final String class_name, final String name,
			final Class<?> implementation) {
		final EntityRep erep = new EntityRep(type_name, class_name, name, implementation);
		if (!entities.contains(erep)) {
			fail("duplicate or not a registered entity representation: " + erep.toString());
		}
		logger.debug("testing entity: " + erep.toString());
		assertEquals(EntityViewFactory.getViewClass(erep.classname, erep.subclass, erep.name), erep.implementation);
		entities.remove(erep);
	}
}
