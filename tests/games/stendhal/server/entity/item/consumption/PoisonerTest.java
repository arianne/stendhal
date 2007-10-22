package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PoisonerTest {

	@Before
	public void setUp() throws Exception {
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

		ConsumableItem c200_1 = new ConsumableItem("cheese", "", "", attributes);
		Poisoner poisoner = new Poisoner();
		Player bob = new Player(new RPObject());
		poisoner.feed(c200_1, bob);
	}

}
