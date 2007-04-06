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

		
		MockRPObject() {

		}

		MockRPObject(String type, String eclass) {
			super.put("type", type);
			super.put("class",eclass);
		}

		public MockRPObject(String type, String eclass, String subclass) {
			this(type,eclass);
			super.put("subclass",subclass);
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
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Carrotgrower now", CarrotGrower.class, en.getClass());

	}

	@Test
	public final void testCreateGrainfield() {
		RPObject rp = new MockRPObject("grain_field", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now", GrainField.class, en.getClass());
		
		rp = new MockRPObject("growing_entity_spawner", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		en = EntityFabric.createEntity(rp);
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
	@Test
	public final void testCreatePlayer() {
		RPObject rp = new MockRPObject("player", null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a player by now", Player.class, en.getClass());

	}

	@Test
	public final void smallCreature() {
		RPObject rp = new MockRPObject("creature","small_animal");
	
		
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a SmallCreature by now", SmallCreature.class, en.getClass());

	}
	@Test
	public final void BigCreature() {
		RPObject rp = new MockRPObject("creature", "giant_animal");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a BigCreature by now", BigCreature.class, en.getClass());

	}
	@Test
	public final void HugeCreature() {
		RPObject rp = new MockRPObject("creature", "huge_animal");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a HugeCreature by now", HugeCreature.class, en.getClass());

	}
	@Test
	public final void MythicalCreature() {
		RPObject rp = new MockRPObject("creature", "mythical_animal");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a MythicalCreature by now", MythicalCreature.class, en.getClass());

	}
	@Test
	public final void NormalCreature() {
		RPObject rp = new MockRPObject("creature", null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a NormalCreature by now", NormalCreature.class, en.getClass());

	}
	@Test
	public final void ResizeableCreature() {
		RPObject rp = new MockRPObject("creature",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertFalse("we should have created a ResizeableCreature by now", ResizeableCreature.class.equals(en.getClass()));

	}


	@Test
	public final void Sheep() {
		RPObject rp = new MockRPObject("sheep",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sheep by now", Sheep.class, en.getClass());


	}
	@Test
	public final void NPC() {
		RPObject rp = new MockRPObject("npc",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a NPC by now", NPC.class, en.getClass());


	}
	@Test
	public final void PlantGrower() {
		RPObject rp = new MockRPObject("plant_grower",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a PlantGrower by now", PlantGrower.class, en.getClass());


	}
	@Test
	public final void GoldSource() {
		RPObject rp = new MockRPObject("gold_source",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a GoldSource by now", GoldSource.class, en.getClass());
	}

	@Test
	public final void InvisibleEntity() {
		RPObject rp = new MockRPObject("walk_blocker",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now", InvisibleEntity.class, en.getClass());
		 rp = new MockRPObject("damaging_area",  null);
		 en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now", InvisibleEntity.class, en.getClass());
	}
	@Test
	public final void SheepFood() {
		RPObject rp = new MockRPObject("food",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a SheepFood by now", SheepFood.class, en.getClass());
	}
	@Test
	public final void Chest() {
		RPObject rp = new MockRPObject("chest",  null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Chest by now", Chest.class, en.getClass());
	}
	@Test
	public final void Corpse() {
		// class must not be null otherwise creation fails because of image 
		RPObject rp = new MockRPObject("corpse",  null);
		
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Corpse by now", Corpse.class, en.getClass());
	}

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
