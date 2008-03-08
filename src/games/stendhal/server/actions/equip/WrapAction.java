package games.stendhal.server.actions.equip;

import games.stendhal.common.Grammar;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Present;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * @author Martin Fuchs
 */
public class WrapAction implements ActionListener {

	static final Logger logger = Logger.getLogger(WrapAction.class);

	/**
	 * Registers the "wrap" action handler.
	 */
	public static void register() {
		WrapAction wrap = new WrapAction();
		CommandCenter.register("wrap", wrap);
	}

	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("wrap")) {
    		onWrap(player, action);
		}
	}

	private void onWrap(Player player, RPAction action) {
		String itemName = action.get("target");
		String args = action.get("args");

		if (args.length() > 0) {
			itemName += ' ';
			itemName += args;
		}

		itemName = Grammar.singular(itemName);

		Item item = player.getFirstEquipped(itemName);

		if (item != null) {
			final String slot = "bag";
    	    String type = item.get("subclass");

    	    Present present = (Present) SingletonRepository.getEntityManager().getItem("present");
    	    present.setContent(type);
    	    player.equip(slot, present);

    	    player.drop(itemName);

    	    SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "wrap", itemName, slot);

    	    player.updateItemAtkDef();
	    } else {
			player.sendPrivateText("You don't have any " + itemName);
	    }
    }

}
