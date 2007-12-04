package utilities;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

public class PlayerTestHelper  {

	public static Player createPlayer() {
		PlayerHelper.generatePlayerRPClasses();
		Player pl = new Player(new RPObject());
		PlayerHelper.addEmptySlots(pl);
		return pl;
	}
	public static Player createPlayer(String name) {
		Player pl = createPlayer();
		pl.setName(name);
		return pl;
	}

	public static boolean equipWithMoney(Player player, int amount) {
		StendhalRPWorld world = StendhalRPWorld.get();

		StackableItem money = (StackableItem) world.getRuleManager().getEntityManager().getItem("money");
		money.setQuantity(amount);

		return player.equip(money);
	}
}
