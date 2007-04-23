package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import games.stendhal.client.GameObjects;
import marauroa.common.game.RPObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityFactoryTest {

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
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Carrotgrower now", CarrotGrower.class, en.getClass());

	}

	@Test
	public final void testCreateGrainfield() {
		RPObject rp = new MockRPObject("grain_field", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now", GrainField.class, en.getClass());
		
		rp = new MockRPObject("growing_entity_spawner", null, null);
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Grainfield now", GrainField.class, en.getClass());

	}

	@Test
	public final void testCreateApple() {
		RPObject rp = new MockRPObject("item", "food", "apple");
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created an item by now", "item", en.getType());

	}

	@Test
	public final void testCreateSign() {
		RPObject rp = new MockRPObject("blackboard", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a sign by now", Sign.class, en.getClass());

	}

	@Test
	public final void testCreateSheep() {
		RPObject rp = new MockRPObject("sheep", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a sheep by now", Sheep.class, en.getClass());

	}

	@Test
	public final void testCreateBox() {
		RPObject rp = new MockRPObject("item", "box");
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a box by now", Box.class, en.getClass());

	}
	@Test
	public final void testCreatePlayer() {
		RPObject rp = new MockRPObject("player", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a player by now", Player.class, en.getClass());

	}

	@Test
	public final void ResizeableCreature() {
		RPObject rp = new MockRPObject("creature",  null);
		rp.put("height", 1);
		rp.put("width", 1);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a ResizeableCreature by now", ResizeableCreature.class, en.getClass());

	}


	@Test
	public final void Sheep() {
		RPObject rp = new MockRPObject("sheep",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sheep by now", Sheep.class, en.getClass());


	}
	@Test
	public final void NPC() {
		RPObject rp = new MockRPObject("npc",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a NPC by now", NPC.class, en.getClass());


	}
	@Test
	public final void PlantGrower() {
		RPObject rp = new MockRPObject("plant_grower",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a PlantGrower by now", PlantGrower.class, en.getClass());


	}
	@Test
	public final void GoldSource() {
		RPObject rp = new MockRPObject("gold_source",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a GoldSource by now", GoldSource.class, en.getClass());
	}

	@Test
	public final void InvisibleEntity() {
		RPObject rp = new MockRPObject("walk_blocker",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now", InvisibleEntity.class, en.getClass());
		 rp = new MockRPObject("damaging_area",  null);
		 en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now", InvisibleEntity.class, en.getClass());
	}
	@Test
	public final void SheepFood() {
		RPObject rp = new MockRPObject("food",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a SheepFood by now", SheepFood.class, en.getClass());
	}
	@Test
	public final void Chest() {
		RPObject rp = new MockRPObject("chest",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Chest by now", Chest.class, en.getClass());
	}
	@Test
	public final void Corpse() {
		RPObject rp = new MockRPObject("corpse",  null);
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Corpse by now", Corpse.class, en.getClass());
	}
	@Test
	public final void Blood() {
		RPObject rp = new MockRPObject("blood",  null);
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Blood by now", Blood.class, en.getClass());
	}

	@Test
	public final void Sign() {
		RPObject rp = new MockRPObject("sign",  null);
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en.getClass());
	
		rp = new MockRPObject("blackboard",  null);
		
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en.getClass());
	}

	
	@Test
	public final void Item() {
		RPObject rp = new MockRPObject("item",  null);
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Item by now", Item.class, en.getClass());
	}
	
	@Test
	public final void StackableItem() {
		RPObject rp = new MockRPObject("item",  "drink");
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "drink");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "food");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "herb");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "misc");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "money");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "ammunition");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "projectiles");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "resource");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());
		rp = new MockRPObject("item",  "scroll");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now", StackableItem.class, en.getClass());

	}
	@Test
	public final void Portal() {
		RPObject rp = new MockRPObject("portal",  null);
		
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Portal by now", Portal.class, en.getClass());
	}
	@Test
	public final void Door() {
		RPObject rp = new MockRPObject("door",  null);
		rp.put("dir", 1);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Door.class, en.getClass());
	}
	@Test
	public final void Fire() {
		RPObject rp = new MockRPObject("fire",  null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Fire.class, en.getClass());
	}

}
