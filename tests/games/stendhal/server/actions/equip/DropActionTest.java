// $Id$
package games.stendhal.server.actions.equip;

import static org.junit.Assert.assertEquals;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.ZoneAndPlayerTestImpl;

/**
 * Test cases for DisplaceAction.
 */
public class DropActionTest extends ZoneAndPlayerTestImpl {

	private static final String ZONE_NAME = "int_semos_tavern_0";

	public DropActionTest() {
	    super(ZONE_NAME);
    }

	/**
	 * Initialise the world.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		// initialise world
		SingletonRepository.getRPWorld();

		setupZone(ZONE_NAME);
	}

	/**
	 * Test for dice in gambling zone.
	 */
	@Test
	public void testDropDice() {
		final StendhalRPZone localzone = new StendhalRPZone(ZONE_NAME, 20, 20); // zone with disabled collision detection
		final Player player = PlayerTestHelper.createPlayer("bob");
		localzone.add(player);

		Item item = SingletonRepository.getEntityManager().getItem("dice");
		player.equip(item, 1);

		assertEquals(0, localzone.getItemsOnGround().size());

		item = player.getFirstEquipped("dice");
		RPObject parent = item.getContainer();
		final RPAction action = new RPAction();
		action.put("type", "drop");
		action.put("baseitem", item.getID().getObjectID());
		action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT, item.getContainerSlot().getName());
		action.put("x", player.getX());
		action.put("y", player.getY() + 1);

		new DropAction().onAction(player, action);
		assertEquals(0, player.events().size());
		Item[] items = localzone.getItemsOnGround().toArray(new Item[0]);
		assertEquals(1, items.length);
		assertEquals(0, items[0].getX());
		assertEquals(1, items[0].getY());
	}
}
