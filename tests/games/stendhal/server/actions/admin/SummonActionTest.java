package games.stendhal.server.actions.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class SummonActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SummonAction.register();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().getPlayers().clear();
		Log4J.init();
	}

	@Test
	public final void testSummonRat() {

		Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		StendhalRPZone zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(Entity entity, double x,
					double y) {

				return false;
			}
		};
		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "rat");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		Creature rat = (Creature) zone.getEntityAt(0, 0);
		assertEquals("rat", rat.get("subclass"));

	}

	@Test
	public final void testSummonDagger() {

		Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		StendhalRPZone zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(Entity entity, double x,
					double y) {

				return false;
			}
		};
		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "dagger");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		Item item = (Item) zone.getEntityAt(0, 0);
		assertEquals("dagger", item.get("subclass"));

	}

	@Test
	public final void testSummonUnKnown() {

		Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		StendhalRPZone zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(Entity entity, double x,
					double y) {

				return false;
			}
		};
		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "unknown");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		assertNull(zone.getEntityAt(0, 0));

	}

}
