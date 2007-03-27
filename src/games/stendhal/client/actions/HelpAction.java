package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;

import java.awt.Color;

/**
 * Display command usage.
 * Eventually replace this with ChatCommand.usage().
 */
class HelpAction implements SlashAction  {

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
		        "For a detailed reference, visit #http://arianne.sourceforge.net/wiki/index.php/StendhalManual",
		        "Here are the most-used commands:", "- /away [<message>] \tSet an away message",
		        "- /tell <player> <message> \tSends a private message to <player>",
		        "- /answer <message> \t\tSends a private message to the last player who sent a message to you",
		        "- // <message> \t\tSends a private message to the last player you sent a message to",
		        "- /support <message> \tAsk an administrator for help.",
		        "- /who \t\tList all players currently online",
		        "- /drop <quantity> <item>\tDrop a certain number of an item",
		        "- /add <player> \t\tAdd <player> to your buddy list",
		        "- /remove <player> \tRemove <player> from your buddy list",
		        "- /ignore <player> [<minutes>|*|- [<reason...>]] \tAdd <player> to your ignore list",
		        "- /unignore <player> \tRemove <player> from your ignore list",
		        "- /where <player> \t\tShow the current location of <player>",
		        "- /quit \t\tLeave the game. You will continue where you left off upon your return",
		        "- /sound volume <value> \tSet volume to a value from 0 to 100",
		        "- /sound mute <on|off> \tMute or unmute the sounds" };

		for (String line : lines) {
			StendhalClient.get().addEventLine(line, Color.gray);
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
