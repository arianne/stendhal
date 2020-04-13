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
package games.stendhal.client.gui.j2d.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import marauroa.common.Log4J;

public class EntityViewFactoryTest {

	@Before
	public void setUp() throws Exception {
		Log4J.init();

		//GameScreen.setDefaultScreen(new MockScreen());
	}

	/**
	 * Tests for create.
	 */
	@Test
	public final void testCreate() {
		assertEquals(EntityViewFactory.getViewClass("blood", null, null), Blood2DView.class);
		assertEquals(EntityViewFactory.getViewClass("creature", "ent", null), BossCreature2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "box", null), Box2DView.class);

		assertEquals(EntityViewFactory.getViewClass("growing_entity_spawner", "items/grower/wood_grower", null), CarrotGrower2DView.class);
		assertEquals(EntityViewFactory.getViewClass("growing_entity_spawner", "items/grower/carrot_grower", null), CarrotGrower2DView.class);
		assertEquals(EntityViewFactory.getViewClass("chest", null, null), Chest2DView.class);
		assertEquals(EntityViewFactory.getViewClass("corpse", null, null), Corpse2DView.class);

		assertEquals(EntityViewFactory.getViewClass("creature", null, null), Creature2DView.class);

		assertEquals(EntityViewFactory.getViewClass("door", null, null), Door2DView.class);

		assertEquals(EntityViewFactory.getViewClass("fire", null, null), UseableEntity2DView.class);


		assertEquals(EntityViewFactory.getViewClass("gate", null, null), Gate2DView.class);

		assertEquals(EntityViewFactory.getViewClass("growing_entity_spawner", null, null), GrainField2DView.class);

		assertEquals(EntityViewFactory.getViewClass("house_portal", null, null), HousePortal2DView.class);

		assertEquals(EntityViewFactory.getViewClass("area", null, null),  InvisibleEntity2DView.class);

	    assertEquals(EntityViewFactory.getViewClass("item", "special", "mithril clasp"), Item2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "crystal", null),  Item2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", null, null),  Item2DView.class);
		assertEquals(EntityViewFactory.getViewClass("npc", null, null), NPC2DView.class);

		assertEquals(EntityViewFactory.getViewClass("cat", null, null), Pet2DView.class);
		assertEquals(EntityViewFactory.getViewClass("pet", null, null), Pet2DView.class);
		assertEquals(EntityViewFactory.getViewClass("baby_dragon", null, null), Pet2DView.class);

		assertEquals(EntityViewFactory.getViewClass("plant_grower", null, null), PlantGrower2DView.class);

		assertEquals(EntityViewFactory.getViewClass("player", null, null), Player2DView.class);

		assertEquals(EntityViewFactory.getViewClass("portal", null, null), Portal2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "ring", "emerald-ring"), BreakableRing2DView.class);

		assertEquals(EntityViewFactory.getViewClass("sheep", null, null),  Sheep2DView.class);
		assertEquals(EntityViewFactory.getViewClass("food", null, null), SheepFood2DView.class);

		assertEquals(EntityViewFactory.getViewClass("sign", null, null),  Sign2DView.class);
		assertEquals(EntityViewFactory.getViewClass("blackboard", null, null),  Sign2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "jewellery", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "flower", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "resource", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "herb", null), StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "misc", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "money", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "missile", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "ammunition", null),  StackableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "container", null),  StackableItem2DView.class);
        assertEquals(EntityViewFactory.getViewClass("item", "special", null),  StackableItem2DView.class);


		assertEquals(EntityViewFactory.getViewClass("item", "club", "wizard_staff"),  UseableItem2DView.class);
        assertEquals(EntityViewFactory.getViewClass("item", "misc", "seed"), UseableItem2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "scroll", null),  UseableItem2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "food", null), UseableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "drink", null),  UseableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "tool", "foodmill"),  UseableItem2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "tool", "sugarmill"),  UseableItem2DView.class);

		assertEquals(EntityViewFactory.getViewClass("item", "ring", null), Ring2DView.class);
		assertEquals(EntityViewFactory.getViewClass("item", "ring", "wedding"), UseableRing2DView.class);

		assertEquals(EntityViewFactory.getViewClass("walkblocker", null, null), WalkBlocker2DView.class);

	}
}
