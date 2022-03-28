package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.scroll.MarkedScroll;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class MarkScrollAction implements ActionListener {

	@Override
	public void onAction(Player player, RPAction action) {

		int count = 1;
		if (action.has("quantity")) {
			count = Integer.parseInt(action.get("quantity"));
		}

		// can't try to drop the scroll straight away as teleport may not be allowed
		if(player.isEquipped("empty scroll", count)) {

			final StendhalRPZone zone = player.getZone();
			final int x = player.getX();
			final int y = player.getY();

			if (zone.isTeleportInAllowed(x, y)) {

				player.drop("empty scroll", count);

				String infostring = zone.getName() + " " + x + " " + y;

				final MarkedScroll scroll = (MarkedScroll)
						SingletonRepository.getEntityManager().getItem("marked scroll");
				scroll.setQuantity(count);
				scroll.setInfoString(infostring);

				// add a description if the player wanted one
				if (action.has(TARGET)) {
					String description = action.get(TARGET) + " " + action.get("args");
					scroll.setDescription("You see a scroll marked by " + player.getName() + ". It says: \""+ description +"\". ");
				}

				player.equipOrPutOnGround(scroll);

			} else {
				player.sendPrivateText("The strong anti magic aura in this area prevents the scroll from working!");
			}
		} else {
			if (count > 1) {
				player.sendPrivateText("You don't have that many empty scrolls to mark.");
			} else {
				player.sendPrivateText("You don't have any empty scrolls to mark.");
			}
		}
	}

}
