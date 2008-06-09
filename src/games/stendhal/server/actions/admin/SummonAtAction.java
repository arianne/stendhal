package games.stendhal.server.actions.admin;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SummonAtAction extends AdministrationAction {
	private static final String _AMOUNT = "amount";
	private static final String _ITEM = "item";
	private static final String _SLOT = "slot";

	private static final String _SUMMONAT = "summonat";

	public static void register() {
		CommandCenter.register(_SUMMONAT, new SummonAtAction(), 800);
	}

	@Override
	public void perform(Player player, RPAction action) {

		if (action.has(TARGET) && action.has(_SLOT) && action.has(_ITEM)) {
			String name = action.get(TARGET);
			Player changed = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (changed == null) {
				logger.debug("Player \"" + name + "\" not found.");
				player.sendPrivateText("Player \"" + name + "\" not found.");
				return;
			}

			String slotName = action.get(_SLOT);
			if (!changed.hasSlot(slotName)) {
				logger.debug("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				player.sendPrivateText("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				return;
			}

			EntityManager manager = SingletonRepository.getEntityManager();
			String type = action.get(_ITEM);

			// Is the entity an item
			if (manager.isItem(type)) {
				SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
						_SUMMONAT, changed.getName(), slotName, type);
				Item item = manager.getItem(type);

				if (action.has(_AMOUNT) && (item instanceof StackableItem)) {
					((StackableItem) item).setQuantity(action.getInt(_AMOUNT));
				}

				if (!changed.equip(slotName, item)) {
					player.sendPrivateText("The slot is full.");
				}
			} else {
				player.sendPrivateText(type + "is not an item.");
			}
		}
	}

}
