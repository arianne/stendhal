package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;

import java.awt.Color;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Drop a player item.
 */
class DropAction implements SlashAction  {
	// TODO: find a way to not have this redundand at server and client
	private static final String[] CARRYING_SLOTS = { "bag", "head", "rhand", "lhand", "armor", "cloak", "legs", "feet" };


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
			StendhalUI.get().addEventLine("Invalid quantity");
			return true;
		}

		RPObject player = StendhalClient.get().getPlayer();
		String itemName = params[1];
		int itemID = findItem(itemName);

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
			StendhalUI.get().addEventLine("You don't have any " + itemName, Color.black);
		}

		return true;
	}

	/**
	 * returns the objectid for the named item
	 * 
	 * @param itemName name of item
	 * @return objectid or <code>-1</code> in case there is no such item
	 */
	private int findItem(String itemName) {
		RPObject player = StendhalClient.get().getPlayer();
		for (String slotName : CARRYING_SLOTS) { 
			for (RPObject item : player.getSlot(slotName)) {
				if (item.get("name").equals(itemName)) {
					int itemID = item.getID().getObjectID();
					return itemID;
				}
			}
		}
		return -1;
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
