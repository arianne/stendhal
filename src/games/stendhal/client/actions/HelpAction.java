package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
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
				"For a detailed reference, visit #http://stendhalgame.org/wiki/Stendhal_Manual",
				"Here are the most-used commands:",
				"* Chatting: ",
				"- /me <action> \t\tShow a message about what you are doing",
				"- /tell <player> <message> Sends a private message to <player>",
				"- /answer <message> \tSends a private message to the last player who sent a message to you",
				"- // <message> \tSends a private message to the last player you sent a message to",
				"- /support <message> \tAsk an administrator for help.",
				"- /who \t\tList all players currently online",
				"- /where <player> \tShow the current location of <player>",
				"- /sentence <text> \tWrites the sentence that appears on Website.",
				"* Items: ",
				"- /drop [quantity] <item>\tDrop a certain number of an item",
				"- /listproducers\tFind out what items are being produced for you",
				"* Buddies and Enemies: ",
				"- /add <player> \tAdd <player> to your buddy list",
				"- /remove <player> \tRemove <player> from your buddy list",
				"- /ignore <player> [<minutes>|*|- [<reason...>]] \tAdd <player> to your ignore list",
				"- /ignore \t\tFind out who is on your ignore list",
				"- /unignore <player> \tRemove <player> from your ignore list",
				"* Status: ",
				"- /away <message> \tSet an away message",
				"- /away \t\tRemove status away",
				"- /grumpy <message> \tSets a message to ignore all non-buddies.",
				"- /grumpy \t\tRemove status grumpy",
				"- /name <pet> <name> \tGive a name to your pet",
				"* Misc: ", 
				"- /clickmode \t\t switches between single click mode and double click mode",
				"- /info \t\tFind out what the current server time is",
				"- /mute\t\tMute or unmute the sounds",
				"- /volume\t\tLists or sets the volume for sound and music"
		};

		for (final String line : lines) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
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
