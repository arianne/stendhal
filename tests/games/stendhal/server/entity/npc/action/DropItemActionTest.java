package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import utilities.PlayerTestHelper;

public class DropItemActionTest {
	
	@BeforeClass
	public static void beforeClass() {
		Log4J.init();
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	@Test
	public void testFire() {
		Player p = PlayerTestHelper.createPlayer("bob");
		PlayerTestHelper.equipWithItem(p, "axe");
		assertThat(Boolean.valueOf(p.isEquipped("axe")), is(Boolean.TRUE));
		DropItemAction action = new DropItemAction("axe");
		action.fire(p, null, null);
		assertThat(Boolean.valueOf(p.isEquipped("axe")), is(Boolean.FALSE));
	}

}
