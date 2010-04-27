// $Id$
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;


/**
 * Test cases for drop.
 *
 * @author hendrik
 */
public class EquipmentActionTest  extends ZoneAndPlayerTestImpl {

	private static final String ZONE_NAME = "0_semos_city";

	public EquipmentActionTest() {
	    super(ZONE_NAME);
    }

	/**
	 * initialize the world.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		new DatabaseFactory().initializeDatabase();
		setupZone(ZONE_NAME);
	}

	/**
	 * Create player and put it into the world.
	 * @param name
	 * @return a Player where the  privateTexts are captured
	 */
	private Player createTestPlayer(final String name) {
		final Player player = PlayerTestHelper.createPlayer(name);

		player.setPosition(10, 5);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).assignRPObjectID(player);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(player);

		return player;
	}


	/**
	 * Tests for dropNonExistingItem.
	 */
	@Test
	public void testDropNonExistingItem() {
		final Player player = createTestPlayer("bob");

		final RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		drop.put("quantity", "1");
		drop.put("baseitem", -1);

		final EquipmentAction action = new DropAction();
		action.onAction(player, drop);
		Assert.assertEquals("There is no such item in the bag of bob", player.events().get(0).get("text"));
	}

	/**
	 * Tests for onActioninJail.
	 */
	@Test
	public void testOnActioninJail() {
		final EquipmentAction action = new DropAction();
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("bla_jail");
		zone.add(bob);
		action.onAction(bob, new RPAction());
		assertFalse(bob.events().isEmpty());
		assertEquals("For security reasons, items may not be moved around in jail.", bob.events().get(0).get("text"));

		
		bob = PlayerTestHelper.createPlayer("bobby");
		
		zone = new StendhalRPZone("bla_jail_not");
		
		zone.add(bob);
		action.onAction(bob, new RPAction());
		assertTrue(bob.events().isEmpty());
	}
	
	/**
	 * Tests for dropItem.
	 */
	@Test
	public void testDropItem() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		Item item = SingletonRepository.getEntityManager().getItem("cheese");

		player.equip("bag", item);
		assertTrue(player.isEquipped("cheese"));
		localzone.add(player);
		final RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		drop.put("quantity", "1");
		drop.put("baseitem", item.getID().getObjectID());

		final EquipmentAction action = new DropAction();
		assertEquals(0, localzone.getItemsOnGround().size());
		assertTrue(drop.has(EquipActionConsts.BASE_ITEM));
		
		action.onAction(player, drop);
		Assert.assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));
		
	}
	
	/**
	 * Tests for dropSomeOfItem.
	 */
	@Test
	public void testDropSomeOfItem() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		item.setQuantity(5);
		player.equip("bag", item);
		assertTrue(player.isEquipped("cheese", 5));
		localzone.add(player);
		final RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		drop.put("quantity", "2");
		drop.put("baseitem", item.getID().getObjectID());

		final EquipmentAction action = new DropAction();
		assertEquals(0, localzone.getItemsOnGround().size());
		assertTrue(drop.has(EquipActionConsts.BASE_ITEM));
		
		action.onAction(player, drop);
		Assert.assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 4));
		assertTrue(player.isEquipped("cheese", 3));
	}
	
}
