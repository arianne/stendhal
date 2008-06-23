package games.stendhal.server.actions;

import static games.stendhal.server.actions.WellKnownActionConstants._BASEITEM;
import static games.stendhal.server.actions.WellKnownActionConstants._BASEOBJECT;
import static games.stendhal.server.actions.WellKnownActionConstants._BASESLOT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.ChestTestHelper;

public class UseActionTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		Log4J.init();
	}

	@Test
	public void testOnActionItemInBag() {
		MockStendlRPWorld.get();
		UseAction ua = new UseAction();
		Player player = PlayerTestHelper.createPlayer("bob");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bag", cheese);
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		RPAction action = new RPAction();
		action.put(_BASEITEM, cheese.getID().getObjectID());
		action.put(_BASEOBJECT, player.getID().getObjectID());
		action.put(_BASESLOT, "bag");
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertFalse(player.isEquipped("cheese"));
	}

	@Test
	public void testOnActionIteminChest() {
		MockStendlRPWorld.get();
		ChestTestHelper.generateRPClasses();
		UseAction ua = new UseAction();
		Player player = PlayerTestHelper.createPlayer("bob");
		Chest chest = new Chest();
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		chest.add(cheese);
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		zone.add(chest);
		RPAction action = new RPAction();
		action.put(_BASEITEM, cheese.getID().getObjectID());
		action.put(_BASEOBJECT, chest.getID().getObjectID());
		action.put(_BASESLOT, "content");
		assertFalse(player.has("eating"));
		ua.onAction(player, action);
		assertTrue(player.has("eating"));
	}
	@Test
	public void testIsItemBoundToOtherPlayer() {
		UseAction ua = new UseAction();
		Player player = PlayerTestHelper.createPlayer("bob");
		Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertFalse(ua.isItemBoundToOtherPlayer(player, cheese));
		cheese.setBoundTo("jack");
		
		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertTrue(ua.isItemBoundToOtherPlayer(player, cheese));
		
		cheese.setBoundTo("bob");
		
		assertFalse(ua.isItemBoundToOtherPlayer(player, null));
		assertFalse(ua.isItemBoundToOtherPlayer(player, cheese));
		
		
	}

}
