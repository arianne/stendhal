package games.stendhal.client.entity;

import static org.junit.Assert.*;
import games.stendhal.client.GameObjects;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityFabricTest {

	private class MockRPObject extends RPObject {

		private String _type;

		private String _eclass;

		private String _subclass;

		MockRPObject() {

		}

		MockRPObject(String type, String eclass) {
			_type = type;
			_eclass = eclass;
		}

		public MockRPObject(String type, String eclass, String subclass) {
			_type = type;

			_eclass = eclass;
			_subclass = subclass;
		}

		@Override
		public boolean has(String attribute) {
			if (attribute.equals("subtype")) {
	            return _subclass == null;
            }
			return true;
		}

		@Override
		public String get(String attribute) throws AttributeNotFoundException {
			if (attribute.equals("type")) {
				return _type;
			} else if (attribute.equals("class")) {
				return _eclass;
			} else {//if (attribute.equals("subtype")){
				return _subclass;
			}

		}

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameObjects.createInstance(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testCreateCarrot() {
		RPObject rp = new MockRPObject("growing_entity_spawner", "items/grower/carrot_grower", "carrot");
		rp.add("max_ripeness", 1);
		rp.add("width", 1);
		rp.add("height", 1);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Carrotgrower now", CarrotGrower.class, en.getClass());

	}

	@Test
	public final void testCreateGrainfield() {
		RPObject rp = new MockRPObject("grain_field", null, null);
		rp.add("max_ripeness", 1);
		rp.add("width", 1);
		rp.add("height", 1);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now", GrainField.class, en.getClass());

	}

	@Test
	public final void testCreateApple() {
		RPObject rp = new MockRPObject("item", "food", "apple");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created an item by now", "item", en.getType());

	}

	@Test
	public final void testCreateSign() {
		RPObject rp = new MockRPObject("blackboard", null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a sign by now", Sign.class, en.getClass());

	}

	@Test
	public final void testCreateSheep() {
		RPObject rp = new MockRPObject("sheep", null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a sheep by now", Sheep.class, en.getClass());

	}

	@Test
	public final void testCreateBox() {
		RPObject rp = new MockRPObject("item", "box");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a box by now", Box.class, en.getClass());

	}
//	register("player", null, "Player");
//
//	register("creature", "small_animal", "SmallCreature");
//	register("creature", "giant_animal", "BigCreature");
//	register("creature", "huge_animal", "HugeCreature");
//	register("creature", "mythical_animal", "MythicalCreature");
//	register("creature", null, "NormalCreature");
//	// TODO: deactivate compatibility code in EntityFabric after release of 0.59
//	// and use this: register("creature", null, "ResizeableCreature");
//
//	register("sheep", null, "Sheep");
//
//	register("npc", null, "NPC");
//
//	register("plant_grower", null, "PlantGrower");
//	register("growing_entity_spawner", "items/grower/carrot_grower", "CarrotGrower");
//	register("growing_entity_spawner", null, "GrainField");
//	register("grain_field", null, "GrainField"); // compatibility with server <= 0.56
//	register("gold_source", null, "GoldSource");
//
//	register("walk_blocker", null, "InvisibleEntity");
//	register("damaging_area", null, "InvisibleEntity");
//
//	register("food", null, "SheepFood");
//	register("chest", null, "Chest");
//
//	register("corpse", null, "Corpse");
//
//	register("blood", null, "Blood");
//	register("sign", null, "Sign");
//	register("blackboard", null, "Sign");
//
//	register("item", null, "Item");

//	register("item", "drink", "StackableItem");
//	register("item", "food", "StackableItem");
//	register("item", "herb", "StackableItem");
//	register("item", "misc", "StackableItem");
//	register("item", "money", "StackableItem");
//	register("item", "ammunition", "StackableItem");
//	// TODO: remove this after release of 0.59
//	register("item", "projectiles", "StackableItem");
//	register("item", "resource", "StackableItem");
//	register("item", "scroll", "StackableItem");
//
//	register("portal", null, "Portal");
//	register("door", null, "Door");

}
