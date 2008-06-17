package games.stendhal.client.entity.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.Box;
import games.stendhal.client.entity.CarrotGrower;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Fire;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.GrainField;
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
import marauroa.common.Log4J;
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
			if (eclass != null) {
				super.put("class", eclass);
			}
		}

		public MockRPObject(String type, String eclass, String subclass) {
			this(type, eclass);
			if (subclass != null) {
				super.put("subclass", subclass);
			}
		}

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameObjects.createInstance(null);
		Log4J.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void carrot() {
		RPObject rp = new MockRPObject("growing_entity_spawner",
				"items/grower/carrot_grower", "carrot");
		rp.put("max_ripeness", 1);
		rp.put("width", 1);
		rp.put("height", 1);
		Entity en = EntityFactory.createEntity(rp);
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
		Entity en = EntityFactory.createEntity(rp);
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
		RPObject rp = new MockRPObject("item", "food", "apple");
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created an item by now", "item", en
				.getType());

	}

	@Test
	public final void box() {
		RPObject rp = new MockRPObject("item", "box");
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a box by now", Box.class, en
				.getClass());
	}

	@Test
	public final void player() {
		RPObject rp = new MockRPObject("player", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a player by now", Player.class, en
				.getClass());
	}

	@Test
	public final void creature() {
		RPObject rp = new MockRPObject("creature", null);
		rp.put("height", 1);
		rp.put("width", 1);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Creature by now",
				Creature.class, en.getClass());
	}

	@Test
	public final void sheep() {
		RPObject rp = new MockRPObject("sheep", null);
		rp.put("weight", 0);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sheep by now", Sheep.class, en
				.getClass());
	}

	@Test
	public final void nPC() {
		RPObject rp = new MockRPObject("npc", null);
		rp.put("name", "bob");
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a NPC by now", NPC.class, en
				.getClass());
	}

	@Test
	public final void plantGrower() {
		RPObject rp = new MockRPObject("plant_grower", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a PlantGrower by now",
				PlantGrower.class, en.getClass());

	}

	@Test
	public final void goldSource() {
		RPObject rp = new MockRPObject("gold_source", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a GoldSource by now",
				GoldSource.class, en.getClass());
	}

	@Test
	public final void invisibleEntity() {
		RPObject rp = new MockRPObject("area", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a InvisibleEntity by now",
				InvisibleEntity.class, en.getClass());
	}

	@Test
	public final void sheepFood() {
		RPObject rp = new MockRPObject("food", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a SheepFood by now",
				SheepFood.class, en.getClass());
	}

	@Test
	public final void chest() {
		RPObject rp = new MockRPObject("chest", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Chest by now", Chest.class, en
				.getClass());
	}

	@Test
	public final void corpse() {
		RPObject rp = new MockRPObject("corpse", null);

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Corpse by now", Corpse.class, en
				.getClass());
	}

	@Test
	public final void blood() {
		RPObject rp = new MockRPObject("blood", null);

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Blood by now", Blood.class, en
				.getClass());
	}

	@Test
	public final void sign() {
		RPObject rp = new MockRPObject("sign", null);

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en
				.getClass());

		rp = new MockRPObject("blackboard", null);

		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Sign by now", Sign.class, en
				.getClass());
	}

	@Test
	public final void item() {
		RPObject rp = new MockRPObject("item", null);

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Item by now", Item.class, en
				.getClass());
	}

	@Test
	public final void stackableItem() {
		RPObject rp = new MockRPObject("item", "drink");

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "drink");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "food");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
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
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());
		rp = new MockRPObject("item", "jewellery");
		en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a StackableItem by now",
				StackableItem.class, en.getClass());

	}

	@Test
	public final void portal() {
		RPObject rp = new MockRPObject("portal", null);

		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Portal by now", Portal.class, en
				.getClass());
	}

	@Test
	public final void door() {
		RPObject rp = new MockRPObject("door", null);
		rp.put("dir", 1);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Door.class, en
				.getClass());
	}

	@Test
	public final void fire() {
		RPObject rp = new MockRPObject("fire", null);
		Entity en = EntityFactory.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a Door by now", Fire.class, en
				.getClass());
	}

}
