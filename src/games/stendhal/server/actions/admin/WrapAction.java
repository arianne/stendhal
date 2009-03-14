package games.stendhal.server.actions.admin;

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
		final WrapAction wrap = new WrapAction();
		CommandCenter.register("wrap", wrap, 800);
	}

	public void onAction(final Player player, final RPAction action) {
		if (action.get("type").equals("wrap")) {
    		onWrap(player, action);
		}
	}

	private void onWrap(final Player player, final RPAction action) {
		String itemName = action.get("target");
		final String args = action.get("args");

		if ((args != null) && (args.length() > 0)) {
			itemName += ' ';
			itemName += args;
		}

		itemName = Grammar.singular(itemName);

		final Item item = player.getFirstEquipped(itemName);

		if (item != null) {
    	  
    	    final Present present = (Present) SingletonRepository.getEntityManager().getItem("present");
    	    present.setContent(itemName);
    	    player.drop(itemName);
    	    player.equipToInventoryOnly(present);
	    

    	    SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "wrap", itemName);

    	    player.updateItemAtkDef();
	    } else {
			player.sendPrivateText("You don't have any " + itemName);
	    }
    }

}
