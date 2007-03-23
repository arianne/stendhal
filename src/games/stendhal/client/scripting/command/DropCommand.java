package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;

import java.awt.Color;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Drop a player item.
 */
class DropCommand implements SlashCommand {

	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if command was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		int quantity;

		try {
			quantity = Integer.parseInt(params[0]);
		} catch (NumberFormatException ex) {
			StendhalClient.get().addEventLine("Invalid quantity");
			return true;
		}

		String itemName = params[1];

		RPObject player = StendhalClient.get().getPlayer();

		int itemID = -1;

		for (RPObject item : player.getSlot("bag")) {
			if (item.get("name").equals(itemName)) {
				itemID = item.getID().getObjectID();
				break;
			}
		}

		if (itemID != -1) {
			RPAction drop = new RPAction();

			drop.put("type", "drop");
			drop.put("baseobject", player.getID().getObjectID());

			drop.put("baseslot", "bag");
			drop.put("x", player.getInt("x"));
			drop.put("y", player.getInt("y") + 1);
			drop.put("quantity", quantity);
			drop.put("baseitem", itemID);

			StendhalClient.get().send(drop);
		} else {
			StendhalClient.get().addEventLine("You don't have any " + itemName, Color.black);
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 2;
	}
}
