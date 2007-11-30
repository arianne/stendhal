package games.stendhal.client.actions;

import games.stendhal.client.NotificationType;
import games.stendhal.client.StendhalUI;

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
		        "Here are the most-used commands:", 
		        "- /tell <player> <message> \tSends a private message to <player>",
		        "- /answer <message> \t\tSends a private message to the last player who sent a message to you",
		        "- // <message> \t\tSends a private message to the last player you sent a message to",
		        "- /support <message> \tAsk an administrator for help.",
		        "- /who \t\tList all players currently online",
		        "- /where <player> \t\tShow the current location of <player>",
		        "- /drop <quantity> <item>\tDrop a certain number of an item",
		        "- /add <player> \t\tAdd <player> to your buddy list",
		        "- /remove <player> \tRemove <player> from your buddy list",
		        "- /ignore <player> [<minutes>|*|- [<reason...>]] \tAdd <player> to your ignore list",
		        "- /unignore <player> \tRemove <player> from your ignore list",
		        "- /away <message> \tSet an away message",
		        "- /away  \tRemove status away",
		        "- /grumpy <message> \t Sets a message to ignore all non-buddies.",
		        "- /grumpy  \tRemove status grumpy",
		        "- /quit \t\tLeave the game. You will continue where you left off upon your return",
		        "- /sound mute <on|off> \tMute or unmute the sounds" };

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
