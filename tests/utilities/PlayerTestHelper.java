package utilities;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

public class PlayerTestHelper  {

	/**
	 * create an unnamed player object
	 * @return player
	 */
	public static Player createPlayer() {
		PlayerHelper.generatePlayerRPClasses();
		Player pl = new Player(new RPObject());
		PlayerHelper.addEmptySlots(pl);
		pl.put("outfit", "01010101");
		return pl;
	}

	/**
	 * create a named player
	 * @param name
	 * @return player
	 */
	public static Player createPlayer(String name) {
		Player pl = createPlayer();
		pl.setName(name);
		return pl;
	}

	/**
	 * equip the player with the given amount of money
	 * @param player
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithMoney(Player player, int amount) {
		return equipWithStackableItem(player, "money", amount);
	}

	/**
	 * equip the player with the given amount of items
	 * @param player
	 * @param clazz
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithStackableItem(Player player, String clazz, int amount) {
		StendhalRPWorld world = StendhalRPWorld.get();

		StackableItem item = (StackableItem) world.getRuleManager().getEntityManager().getItem(clazz);
		item.setQuantity(amount);

		return player.equip(item);
	}
}
