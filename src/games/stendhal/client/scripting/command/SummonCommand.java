package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Summon an entity.
 */
class SummonCommand implements SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String [] params, String remainder) {
		RPAction summon = new RPAction();

		summon.put("type", "summon");
		summon.put("creature", params[0]);

		if(params[2] != null) {
			summon.put("x", params[1]);
			summon.put("y", params[2]);
		} else if(params[1] != null) {
			return false;
		} else {
			RPObject player = StendhalClient.get().getPlayer();

			summon.put("x", player.getInt("x"));
			summon.put("y", player.getInt("y") + 1);
		}

		StendhalClient.get().send(summon);

		return true;
	}


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 3;
	}


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
