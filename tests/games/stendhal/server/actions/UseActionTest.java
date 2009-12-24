package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.BASEOBJECT;
import static games.stendhal.common.constants.Actions.BASESLOT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
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

	/**
	 * Tests for onActionItemInBag.
	 */
	@Test
	public void testOnActionItemInBag() {
		MockStendlRPWorld.get();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bag", cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		final RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASESLOT, "bag");
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertFalse(player.isEquipped("cheese"));
	}

	/**
	 * Tests for onActionItemInBagWithTwoCheese.
	 */
	@Test
	public void testOnActionItemInBagWithTwoCheese() {
		MockStendlRPWorld.get();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final StackableItem cheese = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		cheese.setQuantity(2);
		player.equip("bag", cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		final RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, player.getID().getObjectID());
		action.put(BASESLOT, "bag");
		assertTrue(player.isEquipped("cheese"));
		ua.onAction(player, action);
		assertTrue(player.isEquipped("cheese"));
	}

	/**
	 * Tests for onActionIteminChest.
	 */
	@Test
	public void testOnActionIteminChest() {
		MockStendlRPWorld.get();
		ChestTestHelper.generateRPClasses();
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Chest chest = new Chest();
		final Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
		chest.add(cheese);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		zone.add(chest);
		final RPAction action = new RPAction();
		action.put(BASEITEM, cheese.getID().getObjectID());
		action.put(BASEOBJECT, chest.getID().getObjectID());
		action.put(BASESLOT, "content");
		assertFalse(player.has("eating"));
		ua.onAction(player, action);
		assertTrue(player.has("eating"));
	}

	/**
	 * Tests for isItemBoundToOtherPlayer.
	 */
	@Test
	public void testIsItemBoundToOtherPlayer() {
		final UseAction ua = new UseAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Item cheese = SingletonRepository.getEntityManager().getItem("cheese");
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
