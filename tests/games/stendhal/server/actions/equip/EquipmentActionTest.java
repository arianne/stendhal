// $Id$
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DatabaseFactory;
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

	@Override
	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
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
	 * Tests for dropInvalidSourceSlot.
	 */
	@Test
	public void testDropInvalidSourceSlot() {
		final Player player = createTestPlayer("george");

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "nonExistingSlotXXXXXX");
		drop.put("baseitem", -1);

		final EquipmentAction action = new DropAction();
		action.onAction(player, drop);
		Assert.assertEquals("Source nonExistingSlotXXXXXX does not exist", player.events().get(0).get("text"));

		// same with source path
		player.clearEvents();
		drop = new RPAction();
		drop.put("type", "drop");
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "nonExistingSlotYYY");
		drop.put(EquipActionConsts.SOURCE_PATH, path);
		action.onAction(player, drop);
		Assert.assertEquals("Source nonExistingSlotYYY does not exist", player.events().get(0).get("text"));
	}

	/**
	 * Tests for dropNonExistingItem.
	 */
	@Test
	public void testDropNonExistingItem() {
		final Player player = createTestPlayer("bob");

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		drop.put("quantity", "1");
		drop.put("baseitem", -1);

		final EquipmentAction action = new DropAction();
		action.onAction(player, drop);
		// Assert.assertEquals("There is no such item in the bag of bob", player.events().get(0).get("text"));

		// same with source path
		drop = new RPAction();
		drop.put("type", "drop");
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag", "-1");
		drop.put(EquipActionConsts.SOURCE_PATH, path);
		action.onAction(player, drop);
	}

	/**
	 * Tests for onActioninJail.
	 */
	@Test
	public void testOnActioninJail() {
		final EquipmentAction action = new DropAction();
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("bla_jail");
		zone.setNoItemMoveMessage("For security reasons, items may not be moved around in jail.");
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
		RPAction drop = new RPAction();
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

		// same with source path
		localzone.remove(item);
		player.equip("bag", item);
		drop = new RPAction();
		drop.put("type", "drop");
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag", Integer.toString(item.getID().getObjectID()));
		drop.put(EquipActionConsts.SOURCE_PATH, path);
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		// sanity checks
		assertEquals(0, localzone.getItemsOnGround().size());
		assertTrue(player.isEquipped("cheese"));
		action.onAction(player, drop);
		Assert.assertEquals(0, player.events().size());
		assertFalse(player.isEquipped("cheese"));
		assertEquals(1, localzone.getItemsOnGround().size());
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

	/**
	 * Test picking up an item from ground.
	 */
	@Test
	public void testPicUp() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		item.setQuantity(5);
		localzone.add(item);
		localzone.add(player);
		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");
		equip.put(EquipActionConsts.QUANTITY, "2");


		final EquipmentAction action = new EquipAction();
		action.onAction(player, equip);

		Assert.assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 3));
		assertTrue(player.isEquipped("cheese", 2));

		// Continue the same, but use item path this time
		equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.SOURCE_PATH, Arrays.asList(Integer.toString(item.getID().getObjectID())));
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);
		equip.put(EquipActionConsts.QUANTITY, "2");

		action.onAction(player, equip);
		Assert.assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 5));
		assertTrue(player.isEquipped("cheese", 4));
	}

	/**
	 * Test picking up an item from ground, when the item is too far away.
	 */
	@Test
	public void testPicUpFromTooFar() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		item.setQuantity(5);
		item.setPosition(0, 2);
		localzone.add(item);
		localzone.add(player);
		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");
		equip.put(EquipActionConsts.QUANTITY, "2");


		final EquipmentAction action = new EquipAction();
		action.onAction(player, equip);

		assertEquals("You cannot reach that far.", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));

		// The same, but using paths
		equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.SOURCE_PATH, Arrays.asList(Integer.toString(item.getID().getObjectID())));
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);
		equip.put(EquipActionConsts.QUANTITY, "2");

		action.onAction(player, equip);
		assertEquals("You cannot reach that far.", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));
	}

	/**
	 * Test picking up an item from ground, when the item is bound to a player
	 */
	@Test
	public void testPicUpBoundItem() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		item.setQuantity(5);
		item.setBoundTo("croesus");
		localzone.add(item);
		localzone.add(player);
		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");
		equip.put(EquipActionConsts.QUANTITY, "2");


		final EquipmentAction action = new EquipAction();
		action.onAction(player, equip);

		assertEquals("This cheese is a special reward for croesus. You do not deserve to use it.", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));
		// Should work if we bind it to bob instead
		item.setBoundTo("bob");
		action.onAction(player, equip);
		assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 3));
		assertTrue(player.isEquipped("cheese", 2));

		// The same, but using paths
		equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.SOURCE_PATH, Arrays.asList(Integer.toString(item.getID().getObjectID())));
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);
		equip.put(EquipActionConsts.QUANTITY, "2");

		action.onAction(player, equip);
		assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 5));
		assertTrue(player.isEquipped("cheese", 4));

		// Bind it again to croesus. Should fail again.
		item.setBoundTo("croesus");
		action.onAction(player, equip);
		assertEquals("This cheese is a special reward for croesus. You do not deserve to use it.", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese", 5));
		assertTrue(player.isEquipped("cheese", 4));
		assertEquals(1, item.getQuantity());
	}

	/**
	 * Test picking up an item from ground, when the item is below another
	 * player.
	 */
	@Test
	public void testPicUpBelowAnotherPlayer() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Player player2 = PlayerTestHelper.createPlayer("blocker");
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		item.setQuantity(5);
		player2.setPosition(0, 1);
		item.setPosition(0, 1);
		localzone.add(item);
		localzone.add(player);
		localzone.add(player2);
		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");
		equip.put(EquipActionConsts.QUANTITY, "2");


		final EquipmentAction action = new EquipAction();
		action.onAction(player, equip);

		assertEquals("You cannot take items which are below other players", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));

		// The same, but using paths
		equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.SOURCE_PATH, Arrays.asList(Integer.toString(item.getID().getObjectID())));
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);
		equip.put(EquipActionConsts.QUANTITY, "2");

		action.onAction(player, equip);
		assertEquals("You cannot take items which are below other players", PlayerTestHelper.getPrivateReply(player));
		assertEquals(1, localzone.getItemsOnGround().size());
		assertFalse(player.isEquipped("cheese"));
	}

	/**
	 * Test moving an item from one player slot to another
	 */
	@Test
	public void testMoveBetweenSlots() {
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		final Player player = PlayerTestHelper.createPlayer("bob");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("rhand", item);
		localzone.add(player);
		assertTrue(player.isEquipped("cheese"));

		final EquipmentAction action = new EquipAction();
		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.BASE_SLOT, "rhand");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");

		action.onAction(player, equip);

		assertTrue(player.isEquipped("cheese"));
		assertTrue("Check the cheese is in bag, not in rhand anymore", player.getSlot("bag").getFirst() == item);
		assertEquals(1, player.getTotalNumberOf("cheese"));

		// The same, but using paths; move the cheese back to hand
		equip = new RPAction();
		equip.put("type", "equip");
		List<String> srcPath = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag", Integer.toString(item.getID().getObjectID()));
		equip.put(EquipActionConsts.SOURCE_PATH, srcPath);
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "rhand");
		equip.put(Actions.TARGET_PATH, path);

		action.onAction(player, equip);
		assertTrue(player.isEquipped("cheese"));
		assertTrue("Check the cheese is in back in rhand", player.getSlot("rhand").getFirst() == item);
		assertEquals(1, player.getTotalNumberOf("cheese"));
	}

	/**
	 * Test moving an item directly from a bank slot to the bag.
	 */
	@Test
	public void testPickupFromFarAwayFromBank() {
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		final Player player = PlayerTestHelper.createPlayer("bob");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bank", item);
		localzone.add(player);
		assertFalse(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));

		final EquipmentAction action = new EquipAction();

		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.BASE_SLOT, "bank");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");

		action.onAction(player, equip);

		// Should fail - the player is not at a chest
		assertFalse(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));

		// The same using item paths
		equip = new RPAction();
		equip.put("type", "equip");
		List<String> srcPath = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bank", Integer.toString(item.getID().getObjectID()));
		equip.put(EquipActionConsts.SOURCE_PATH, srcPath);
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);

		action.onAction(player, equip);
		// Should fail again
		assertFalse(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));
	}

	/**
	 * Test moving an item to bank without using a bank chest.
	 */
	@Test
	public void testPutToBankFarAwayFromBank() {
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		final Player player = PlayerTestHelper.createPlayer("bob");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		player.equip("bag", item);
		localzone.add(player);
		assertTrue(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));

		final EquipmentAction action = new EquipAction();

		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.BASE_SLOT, "bag");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bank");

		action.onAction(player, equip);

		// Should fail - the player is not at a chest
		assertTrue(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));

		// The same using item paths
		equip = new RPAction();
		equip.put("type", "equip");
		List<String> srcPath = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag", Integer.toString(item.getID().getObjectID()));
		equip.put(EquipActionConsts.SOURCE_PATH, srcPath);
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bank");
		equip.put(Actions.TARGET_PATH, path);

		action.onAction(player, equip);
		// Should fail again
		assertTrue(player.isEquipped("cheese"));
		assertEquals(1, player.getTotalNumberOf("cheese"));
	}


	/**
	 * Test grabbing items form the bag of another player
	 */
	@Test
	public void testPickPocketing() {
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Player player2 = PlayerTestHelper.createPlayer("croesus");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		player2.equip("bag", item);
		localzone.add(player);
		localzone.add(player2);
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));

		final EquipmentAction action = new EquipAction();

		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_OBJECT, player2.getID().getObjectID());
		equip.put(EquipActionConsts.BASE_SLOT, "bag");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");

		action.onAction(player, equip);

		// No stealing
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));

		// The same using item paths
		equip = new RPAction();
		equip.put("type", "equip");
		List<String> srcPath = Arrays.asList(Integer.toString(player2.getID().getObjectID()), "bag", Integer.toString(item.getID().getObjectID()));
		equip.put(EquipActionConsts.SOURCE_PATH, srcPath);
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);

		action.onAction(player, equip);

		// Should fail again
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));
	}

	/**
	 * Test moving items to a slot belonging to another player.
	 */
	@Test
	public void testCharity() {
		StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		SingletonRepository.getRPWorld().addRPZone(localzone);
		final Player player = PlayerTestHelper.createPlayer("bob");
		final Player player2 = PlayerTestHelper.createPlayer("croesus");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("cheese");
		player2.equip("bag", item);
		localzone.add(player);
		localzone.add(player2);
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));

		final EquipmentAction action = new EquipAction();

		RPAction equip = new RPAction();
		equip.put("type", "equip");
		equip.put(EquipActionConsts.BASE_OBJECT, player2.getID().getObjectID());
		equip.put(EquipActionConsts.BASE_SLOT, "bag");
		equip.put(EquipActionConsts.BASE_ITEM, item.getID().getObjectID());

		equip.put(EquipActionConsts.TARGET_OBJECT, player.getID().getObjectID());
		equip.put(EquipActionConsts.TARGET_SLOT, "bag");

		action.onAction(player2, equip);

		// No charity
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));

		// The same using item paths
		equip = new RPAction();
		equip.put("type", "equip");
		List<String> srcPath = Arrays.asList(Integer.toString(player2.getID().getObjectID()), "bag", Integer.toString(item.getID().getObjectID()));
		equip.put(EquipActionConsts.SOURCE_PATH, srcPath);
		List<String> path = Arrays.asList(Integer.toString(player.getID().getObjectID()), "bag");
		equip.put(Actions.TARGET_PATH, path);

		action.onAction(player2, equip);

		// Should fail again
		assertFalse(player.isEquipped("cheese"));
		assertTrue(player2.isEquipped("cheese"));
	}
}
