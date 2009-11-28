package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Display command usage. Eventually replace this with ChatCommand.usage().
 */
class HelpAction implements SlashAction {

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
		final String[] lines = {
				"For a detailed reference, visit #http://arianne.sourceforge.net/wiki/index.php/StendhalManual",
				"Here are the most-used commands:",
				"- /tell <player> <message> \tSends a private message to <player>",
				"- /answer <message> \tSends a private message to the last player who sent a message to you",
				"- // <message> \t\tSends a private message to the last player you sent a message to",
				"- /support <message> \tAsk an administrator for help.",
				"- /who \t\tList all players currently online",
				"- /where <player> \t\tShow the current location of <player>",
				"- /sentence <text> \t\tWrites the sentence that appears on Website.",
				"- /drop [quantity] <item>\tDrop a certain number of an item",
				"- /add <player> \t\tAdd <player> to your buddy list",
				"- /remove <player> \tRemove <player> from your buddy list",
				"- /ignore <player> [<minutes>|*|- [<reason...>]] \tAdd <player> to your ignore list",
				"- /ignore \t\t Find out who is on your ignore list",
				"- /unignore <player> \tRemove <player> from your ignore list",
				"- /away <message> \tSet an away message",
				"- /away \t\tRemove status away",
				"- /grumpy <message> \tSets a message to ignore all non-buddies.",
				"- /grumpy \t\tRemove status grumpy",
				"- /look  <name> \t\tLook at the named character",
				"- /attack <name> \t\tAttack the named character",
				"- /me <action> \t\tShow a message about what you are doing",
				"- /name <pet> <name> \t\tGive a name to your pet",
				"- /quit \t\t\tLeave the game. You will continue where you left off upon your return",
				"- /sound mute\t\t\tMute or unmute the sounds",
				"- /info \t\tFind out what the current server time is"
		};

		for (final String line : lines) {
			j2DClient.get().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
