package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.common.Grammar;
import marauroa.common.game.RPAction;

/**
 * Summon an item (presumably) into an entity's slot.
 */
class SummonAtAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		final RPAction summon = new RPAction();

		summon.put("type", "summonat");
		summon.put("target", params[0]);
		summon.put("slot", params[1]);

		int amount;
		String itemName;

		// If there is a numeric expression, treat it as amount.
		//TODO refactor with same code in DropAction.execute()
		if (params[2].matches("[0-9].*")) {
    		try {
    			amount = Integer.parseInt(params[2]);
    		} catch (final NumberFormatException ex) {
    			j2DClient.get().addEventLine(new StandardEventLine("Invalid amount: " + params[2]));
    			return true;
    		}

    		itemName = remainder;
		} else {
			amount = 1;
			itemName = (params[2] + " " + remainder).trim();
		}

		final String singularName = Grammar.singular(itemName);

		summon.put("amount", amount);
		summon.put("item", singularName);

		ClientSingletonRepository.getClientFramework().send(summon);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 3;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 3;
	}
}
