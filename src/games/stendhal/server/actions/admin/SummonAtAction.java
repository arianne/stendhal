package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;
import marauroa.common.game.RPAction;

public class SummonAtAction extends AdministrationAction {
	public static void register() {
		CommandCenter.register("summonat", new SummonAtAction(), 800);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (action.has("target") && action.has("slot") && action.has("item")) {
			String name = action.get("target");
			Player changed = StendhalRPRuleProcessor.get().getPlayer(name);

			if (changed == null) {
				logger.debug("Player \"" + name + "\" not found.");
				player.sendPrivateText("Player \"" + name + "\" not found.");
				return;
			}

			String slotName = action.get("slot");
			if (!changed.hasSlot(slotName)) {
				logger.debug("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				player.sendPrivateText("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				return;
			}

			EntityManager manager = StendhalRPWorld.get().getRuleManager()
					.getEntityManager();
			String type = action.get("item");

			// Is the entity an item
			if (manager.isItem(type)) {
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"summonat", changed.getName(), slotName, type);
				Item item = manager.getItem(type);

				if (action.has("amount") && (item instanceof StackableItem)) {
					((StackableItem) item).setQuantity(action.getInt("amount"));
				}

				if (!changed.equip(slotName, item)) {
					player.sendPrivateText("The slot is full.");
				}
			} else {
				player.sendPrivateText("Not an item.");
			}
		}
	}

}
