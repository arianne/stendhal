package games.stendhal.client.actions;

import games.stendhal.client.StendhalUI;
import games.stendhal.common.NotificationType;

/**
 * Display command usage.
 * Eventually replace this with ChatCommand.usage("alter", true).
 */
class GMHelpAlterAction implements SlashAction  {

	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		String[] lines = {
		        "/alter <player> <attrib> <mode> <value> \tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, or SET",
		        "Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
		        "When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
		        "For example: #/alter #testplayer #outfit #set #12109901",
		        "This will make <testplayer> look like danter" };

		for (String line : lines) {
			StendhalUI.get().addEventLine(line, NotificationType.CLIENT);
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
