package games.stendhal.server.actions.admin;

import static org.junit.Assert.*;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.CorpseTestHelper;

public class DestroyActionTest {

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		CorpseTestHelper.generateRPClasses();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}
	
	@Test
	public void testPerform() {
		DestroyAction destroyAction = new DestroyAction();
		Corpse corpse = new Corpse("rat", 0, 0);
		Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(corpse);
		zone.add(player);
		RPAction rpAction = new RPAction();
		rpAction.put("target", "#" + corpse.getID().getObjectID());
		destroyAction.perform(player , rpAction);
		assertEquals("Removed  corpse with ID null", player.events().get(0).get("text"));
	}

}
