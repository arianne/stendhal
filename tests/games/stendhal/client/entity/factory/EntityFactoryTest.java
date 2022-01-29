/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.Box;
import games.stendhal.client.entity.CarrotGrower;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.Fire;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.InvisibleEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.PlantGrower;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.Sheep;
import games.stendhal.client.entity.SheepFood;
import games.stendhal.client.entity.Sign;
import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.entity.StatefulEntity;
import games.stendhal.client.entity.UseableItem;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

public class EntityFactoryTest {

	private static class MockRPObject extends RPObject {

		MockRPObject(final String type, final String eclass) {
			super.put("type", type);
			super.setRPClass(type);
			if (eclass != null) {
				super.put("class", eclass);
			}
		}

		public MockRPObject(final String type, final String eclass, final String subclass) {
			this(type, eclass);
			if (subclass != null) {
				super.put("subclass", subclass);
			}
		}

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		GameObjects.createInstance(null);
		MockStendlRPWorld.get();
	}


	@Test
	public final void carrot() {
		final RPObject rp = new MockRPObject("growing_entity_spawner",
				"items/grower/carrot_grower", "carrot");
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Carrotgrower now",
				CarrotGrower.class, en.getClass());
	}

	@Test
	public final void grainfield() {
		RPObject rp = new MockRPObject("growing_entity_spawner", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now",
				GrainField.class, en.getClass());

		rp = new MockRPObject("growing_entity_spawner", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now",
				GrainField.class, en.getClass());
	}

	@Test
	public final void apple() {
		final RPObject rp = new MockRPObject("item", "food", "apple");
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created an item by now", UseableItem.class, en
				.getClass());

	}

	@Test
	public final void seed() {
		final RPObject rp = new MockRPObject("item", "misc", "seed");
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Useableitem by now", UseableItem.class, en
				.getClass());

	}

	@Test
	public final void box() {
		final RPObject rp = new MockRPObject("item", "box");
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a box by now", Box.class, en
				.getClass());
	}

	@Test
	public final void player() {
		final RPObject rp = new MockRPObject("player", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a player by now", Player.class, en
				.getClass());
	}

	@Test
	public final void creature() {
		final RPObject rp = new MockRPObject("creature", null);
		rp.put("height", 1);
		rp.put("width", 1);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Creature by now",
				Creature.class, en.getClass());
	}

	@Test
	public final void sheep() {
		final RPObject rp = new MockRPObject("sheep", null);
		rp.put("weight", 0);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sheep by now", Sheep.class, en
				.getClass());
	}

	@Test
	public final void nPC() {
		final RPObject rp = new MockRPObject("npc", null);
		rp.put("name", "bob");
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a NPC by now", NPC.class, en
				.getClass());
	}

	@Test
	public final void plantGrower() {
		final RPObject rp = new MockRPObject("plant_grower", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a PlantGrower by now",
				PlantGrower.class, en.getClass());

	}

	@Test
	public final void goldSource() {
		final RPObject rp = new MockRPObject("gold_source", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a GoldSource by now",
				StatefulEntity.class, en.getClass());
	}

	@Test
	public final void invisibleEntity() {
		final RPObject rp = new MockRPObject("area", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now",
				InvisibleEntity.class, en.getClass());
	}

	@Test
	public final void sheepFood() {
		final RPObject rp = new MockRPObject("food", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a SheepFood by now",
				SheepFood.class, en.getClass());
	}

	@Test
	public final void chest() {
		final RPObject rp = new MockRPObject("chest", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Chest by now", Chest.class, en
				.getClass());
	}

	@Test
	public final void corpse() {
		final RPObject rp = new MockRPObject("corpse", null);

		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Corpse by now", Corpse.class, en
				.getClass());
	}

	@Test
	public final void blood() {
		final RPObject rp = new MockRPObject("blood", null);

		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Blood by now", Blood.class, en
				.getClass());
	}

	@Test
	public final void sign() {
		RPObject rp = new MockRPObject("sign", null);

		IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en
				.getClass());

		rp = new MockRPObject("rented_sign", null);

		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en
				.getClass());
	}

	@Test
	public final void item() {
		final RPObject rp = new MockRPObject("item", null);

		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Item by now", Item.class, en
				.getClass());
	}

	@Test
	public final void stackableItem() {
		RPObject rp = new MockRPObject("item", "drink");

		IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a UseableItem by now",
				UseableItem.class, en.getClass());
		rp = new MockRPObject("item", "drink");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a UseableItem by now",
				UseableItem.class, en.getClass());
		rp = new MockRPObject("item", "food");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a UseableItem by now",
				UseableItem.class, en.getClass());
		rp = new MockRPObject("item", "herb");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "misc");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "money");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "ammunition");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "resource");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "scroll");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a UseableItem by now",
				UseableItem.class, en.getClass());
		rp = new MockRPObject("item", "jewellery");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());

	}

	@Test
	public final void portal() {
		final RPObject rp = new MockRPObject("portal", null);

		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Portal by now", Portal.class, en
				.getClass());
	}

	@Test
	public final void door() {
		final RPObject rp = new MockRPObject("door", null);
		rp.put("dir", 1);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Door.class, en
				.getClass());
	}

	@Test
	public final void fire() {
		final RPObject rp = new MockRPObject("fire", null);
		final IEntity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Fire.class, en
				.getClass());
	}

}
