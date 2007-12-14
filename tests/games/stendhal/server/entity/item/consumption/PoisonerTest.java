package games.stendhal.server.entity.item.consumption;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PoisonerTest {

	@Before
	public void setUp() throws Exception {
		Log4J.init();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test

	public final void testFeed() {

		DefaultEntityManager.getInstance();
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Item.generateRPClass();
		Player.generateRPClass();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount", "1000");
		attributes.put("regen", "200");
		attributes.put("frequency", "1");
		attributes.put("id", "1");
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = new StendhalRPZone("test");
		world.addRPZone(zone);
		ConsumableItem c200_1 = new ConsumableItem("cheese", "", "", attributes);
		zone.add(c200_1);
		Poisoner poisoner = new Poisoner();
		Player bob = PlayerTestHelper.createPlayer();
		poisoner.feed(c200_1, bob);
		assertTrue(bob.isPoisoned());
	}

}
