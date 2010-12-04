// $Id$
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
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
 * Test cases for DisplaceAction.
 */
public class DisplaceActionTest  extends ZoneAndPlayerTestImpl {

	private static final String ZONE_NAME = "0_semos_city";

	public DisplaceActionTest() {
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
	 * @return a Player where the privateTexts are captured
	 */
	private Player createTestPlayer(final String name) {
		final Player player = PlayerTestHelper.createPlayer(name);

		player.setPosition(10, 5);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).assignRPObjectID(player);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(player);

		return player;
	}

	/**
	 * Tests for displaceNonExistingItem.
	 */
	@Test
	public void testDisplaceNonExistingItem() {
		final Player player = createTestPlayer("bob");

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", -1);
		displace.put("quantity", "1");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
//		Assert.assertEquals("Text", player.events().get(0).get("text"));
	}

	/**
	 * Tests for displaceItem.
	 */
	@Test
	public void testDisplaceItem() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = PlayerTestHelper.createPlayer("bob");
		localzone.add(player);

		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("seed");
		localzone.add(player);

		// first put some seeds on the floor
		item.setQuantity(5);
		localzone.add(item);
		assertEquals(1, localzone.getItemsOnGround().size());

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
		Assert.assertEquals(0, player.events().size());
		assertEquals(2, localzone.getItemsOnGround().size());
	}

	/**
	 * Tests for dice in gambling zone.
	 */
	@Test
	public void testDisplaceDice() {
		final StendhalRPZone localzone = new StendhalRPZone("int_semos_tavern_0", 20, 20);
		final Player player = PlayerTestHelper.createPlayer("bob");
		localzone.add(player);

		Item item = SingletonRepository.getEntityManager().getItem("dice");
		localzone.add(player);

		localzone.add(item);
		assertEquals(1, localzone.getItemsOnGround().size());

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", item.getID().getObjectID());
		displace.put("quantity", "2");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		Assert.assertEquals(0, player.events().size());
		assertEquals(1, localzone.getItemsOnGround().size());
	}

	/**
	 * Tests for displacing non-item entities.
	 */
	@Test
	public void testDisplaceBlood() {
		final StendhalRPZone localzone = new StendhalRPZone("testzone", 20, 20);
		final Player player = PlayerTestHelper.createPlayer("bob");
		localzone.add(player);

		Entity entity = new Blood();
		localzone.add(entity);
		assertNotNull(localzone.getBlood(0, 0));

		final RPAction displace = new RPAction();
		displace.put("type", "displace");
		displace.put("baseitem", entity.getID().getObjectID());
		displace.put("quantity", "2");
		displace.put("x", player.getX());
		displace.put("y", player.getY() + 1);

		final DisplaceAction action = new DisplaceAction();
		action.onAction(player, displace);
		Assert.assertEquals(0, player.events().size());
		assertNull(localzone.getBlood(0, 0));
		assertNotNull(localzone.getBlood(0, 1));
	}

}
