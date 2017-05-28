// $Id$
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DatabaseFactory;
import utilities.ZoneAndPlayerTestImpl;

/**
 * Test cases for DisplaceAction.
 */
public class DisplaceActionTest extends ZoneAndPlayerTestImpl {

	private static final String ZONE_NAME = "0_semos_city";

	public DisplaceActionTest() {
	    super(ZONE_NAME);
    }

	/**
	 * Initialise the world.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		new DatabaseFactory().initializeDatabase();
		setupZone(ZONE_NAME);
	}

	/**
	 * Test for displacing non existing items.
	 */
	@Test
	public void testDisplaceNonExistingItem() {
		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", -1);
		displace.put("quantity", "1");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		assertEquals(0, player.events().size());
	}

	/**
	 * Test for displacing items that are too far away.
	 */
	@Test
	public void testDisplaceNotNearEnough() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);

		player.setPosition(10, 10);
		localzone.add(player);

		final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		localzone.add(item);

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "1");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		assertEquals(1, player.events().size());
		assertEquals("You must be next to something you wish to move.", player.events().get(0).get("text"));
	}

	/**
	 * Test for displaceItem.
	 */
	@Test
	public void testDisplaceItem() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = createPlayer("bob");
		localzone.add(player);

		// first put some seeds on the floor
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("seed");
		item.setQuantity(5);
		localzone.add(item);
		StackableItem[] items = localzone.getItemsOnGround().toArray(new StackableItem[0]);
		assertEquals(1, items.length);
		assertEquals(0, items[0].getX());
		assertEquals(0, items[0].getY());
		assertEquals(5, items[0].getQuantity());

		// now test the displacement action
		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "2");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		assertTrue(displace.has(EquipActionConsts.BASE_ITEM));

		action.onAction(player, displace);
		assertEquals(0, player.events().size());
		items = localzone.getItemsOnGround().toArray(new StackableItem[0]);
		assertEquals(2, items.length);
		assertEquals(0, items[0].getX());
		assertEquals(0, items[0].getY());
		assertEquals(3, items[0].getQuantity());
		assertEquals(0, items[1].getX());
		assertEquals(1, items[1].getY());
		assertEquals(2, items[1].getQuantity());
	}

	/**
	 * Test for displacing non-item entities.
	 */
	@Test
	public void testDisplaceBlood() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = createPlayer("bob");
		localzone.add(player);

		Entity entity = new Blood();
		localzone.add(entity);
		assertNotNull(localzone.getBlood(0, 0));

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", entity.getID().getObjectID());
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		new DisplaceAction().onAction(player, displace);
		assertEquals(0, player.events().size());
		assertNull(localzone.getBlood(0, 0));
		assertNotNull(localzone.getBlood(0, 1));
	}

	/**
	 * Test for displacing too far.
	 */
	@Test
	public void testDisplaceTooFar() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = createPlayer("bob");
		localzone.add(player);

		// first put some seeds on the floor
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("seed");
		item.setQuantity(5);
		localzone.add(item);
		assertEquals(1, localzone.getItemsOnGround().size());
		assertEquals(0, localzone.getItemsOnGround().toArray(new StackableItem[0])[0].getX());
		assertEquals(0, localzone.getItemsOnGround().toArray(new StackableItem[0])[0].getY());

		// now test the displacement action
		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "2");
		displace.put("x", 100);
		displace.put("y", 100);

		new DisplaceAction().onAction(player, displace);
		assertEquals(1, player.events().size());
		assertEquals("You cannot throw that far.", player.events().get(0).get("text"));
		assertEquals(1, localzone.getItemsOnGround().size());
	}

	/**
	 * Test for displacing to an occupied place.
	 * @throws IOException
	 */
	@Test
	public void testDisplaceOccupied() throws IOException {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = createPlayer("bob");
		localzone.add(player);

		// first put some seeds on the floor
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("seed");
		item.setQuantity(5);
		localzone.add(item);
		assertEquals(1, localzone.getItemsOnGround().size());
		assertEquals(0, localzone.getItemsOnGround().toArray(new StackableItem[0])[0].getX());
		assertEquals(0, localzone.getItemsOnGround().toArray(new StackableItem[0])[0].getY());

		LayerDefinition collisionLayer = new LayerDefinition(10, 10);
		collisionLayer.setName("collision");
		collisionLayer.build();
		collisionLayer.set(0, 1, 255);
		localzone.addCollisionLayer("collisionlayer", collisionLayer);

		// now test the displacement action
		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "2");
		displace.put("x", player.getX());
		displace.put("y", player.getY()+1);

		new DisplaceAction().onAction(player, displace);
		assertEquals(1, player.events().size());
		assertEquals("There is no space there.", player.events().get(0).get("text"));
		assertEquals(1, localzone.getItemsOnGround().size());
	}

	/**
	 * Test for displacing items below some other player.
	 */
	@Test
	public void testDisplaceOtherPlayer() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);

		final Player player = createPlayer("bob");
		player.setPosition(1, 1);
		localzone.add(player);

		final Player player2 = createPlayer("alice");
		player2.setPosition(0, 0);
		localzone.add(player2);

		final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		localzone.add(item);

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "1");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		assertEquals(1, player.events().size());
		assertEquals("You cannot take items which are below other players.", player.events().get(0).get("text"));
	}

	/**
	 * Test for displacing bound items below some other player.
	 */
	@Test
	public void testDisplaceBoundOtherPlayer() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);

		final Player player = createPlayer("bob");
		player.setPosition(1, 1);
		localzone.add(player);

		final Player player2 = createPlayer("alice");
		player2.setPosition(0, 0);
		localzone.add(player2);

		final StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		item.setBoundTo("alice");
		localzone.add(item);

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "1");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		assertEquals(item.getY(), 0);
		assertEquals(1, player.events().size());
		// bound to alice, under alice, bob trying to displace
		assertEquals("You cannot take items which are below other players.", player.events().get(0).get("text"));
		player.clearEvents();
		// bound to bob, under alice, bob trying to displace; should succeed
		item.setBoundTo("bob");
		action.onAction(player, displace);
		assertEquals(item.getY(), 2);
		assertEquals(0, player.events().size());
	}

	/**
	 * Test for displacing with wrong quantities.
	 */
	@Test
	public void testDisplaceWrongQuantity() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = createPlayer("bob");
		localzone.add(player);

		// first put some money on the floor
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(10);
		localzone.add(money);
		StackableItem[] items = localzone.getItemsOnGround().toArray(new StackableItem[0]);
		assertEquals(1, items.length);
		assertEquals(10, items[0].getQuantity());

		// now test the displacement action with a too high quantity
		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", money.getID().getObjectID());
		displace.put("quantity", "20");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		items = localzone.getItemsOnGround().toArray(new StackableItem[0]);
		assertEquals(1, items.length);
		assertEquals(10, items[0].getQuantity());
	}
}
