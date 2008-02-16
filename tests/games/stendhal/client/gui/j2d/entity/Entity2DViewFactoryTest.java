package games.stendhal.client.gui.j2d.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.BossCreature;
import games.stendhal.client.entity.Box;
import games.stendhal.client.entity.CarrotGrower;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.Fire;
import games.stendhal.client.entity.FishSource;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.GrainField;
import games.stendhal.client.entity.InvisibleEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.Pet;
import games.stendhal.client.entity.PlantGrower;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.Ring;
import games.stendhal.client.entity.Sheep;
import games.stendhal.client.entity.SheepFood;
import games.stendhal.client.entity.Sign;
import games.stendhal.client.entity.Spell;
import games.stendhal.client.entity.StackableItem;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.WellSource;
import marauroa.common.Log4J;

import org.junit.Before;
import org.junit.Test;

import utilities.MockScreen;

public class Entity2DViewFactoryTest {

	@Before
	public void setUp() throws Exception {
		Log4J.init();

		GameScreen.setDefaultScreen(new MockScreen());
	}

	@Test
	public final void testCreate() {
		assertEquals(Entity2DViewFactory.get().create(new Blood()).getClass(),
				Blood2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new BossCreature()).getClass(),
				BossCreature2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Box()).getClass(),
				Box2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new CarrotGrower()).getClass(),
				CarrotGrower2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Chest()).getClass(),
				Chest2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Corpse()).getClass(),
				Corpse2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new Creature()).getClass(),
				Creature2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Door()).getClass(),
				Door2DView.class);

		assertEquals(
				Entity2DViewFactory.get().create(new FishSource()).getClass(),
				FishSource2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new GoldSource()).getClass(),
				GoldSource2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new GrainField()).getClass(),
				GrainField2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new InvisibleEntity()).getClass(),
				InvisibleEntity2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Item()).getClass(),
				Item2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new NPC()).getClass(),
				NPC2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Pet()).getClass(),
				Pet2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new PlantGrower()).getClass(),
				PlantGrower2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Player()).getClass(),
				Player2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Portal()).getClass(),
				Portal2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Ring()).getClass(),
				Ring2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Sheep()).getClass(),
				Sheep2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new SheepFood()).getClass(),
				SheepFood2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Sign()).getClass(),
				Sign2DView.class);
		assertEquals(Entity2DViewFactory.get().create(new Spell()).getClass(),
				Spell2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new StackableItem()).getClass(),
				StackableItem2DView.class);
		assertEquals(
				Entity2DViewFactory.get().create(new WellSource()).getClass(),
				WellSource2DView.class);

	}

	@Test
	public final void testCreateUser2Dview() {

		assertNotNull(new User2DView(new User()));
		assertEquals(Entity2DViewFactory.get().create(new User()).getClass(),
				User2DView.class);
	}
	@Test
	public final void testCreateFire2Dview() {

	assertNotNull(Entity2DViewFactory.get().create(new Fire()));
	assertEquals(Entity2DViewFactory.get().create(new Fire()).getClass(),
			AnimatedLoopEntity2DView.class);
	}
}
