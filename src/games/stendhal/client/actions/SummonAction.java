package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.User;
import games.stendhal.common.NameBuilder;
import marauroa.common.game.RPAction;

/**
 * Summon an entity.
 */
class SummonAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * We accept the following command syntaxes, coordinates are recognized from numeric parameters:
	 * /summon &lt;creature name&gt;
	 * /summon &lt;creature name&gt; x y
	 * /summon x y &lt;creature name&gt;
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		RPAction summon = new RPAction();

		NameBuilder nameBuilder = new NameBuilder();
		Integer x = null;
		Integer y = null;

		for (int i = 0; i < params.length; ++i) {
			String str = params[i];

			if (str != null) {
				if (str.matches("[0-9].*")) {
        			try {
        				Integer num = new Integer(str);

        				if (x == null) {
        					x = num;
        				} else if (y == null) {
        					y = num;
        				} else {
        					nameBuilder.append(str);
        				}
        			} catch (NumberFormatException e) {
        				StendhalUI.get().addEventLine("Invalid number: " + str);
        				return true;
        			}
    			} else {
    				nameBuilder.append(str);
    			}
			}
		}

		summon.put("type", "summon");
		summon.put("creature", nameBuilder.toString());

		if (x != null) {
			if (y != null) {
    			summon.put("x", x);
    			summon.put("y", y);
    		} else {
    			return false;
			}
		} else {
			summon.put("x", (int) User.get().getX());
			summon.put("y", (int) User.get().getY());
		}

		StendhalClient.get().send(summon);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 9;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
