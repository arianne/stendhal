package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Display command usage. Eventually replace this with
 * ChatCommand.usage("alter", true).
 */
class GMHelpAlterAction implements SlashAction {

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
				"/alter <player> <attrib> <mode> <value> \tAlter stat <attrib> of <player> by the given amount; <mode> can be ADD, SUB, or SET",
				"Examples of <attrib>: atk, def, base_hp, hp, atk_xp, def_xp, xp, outfit",
				"When modifying 'outfit', you should use SET mode and provide an 8-digit number; the first 2 digits are the 'hair' setting, then 'head', 'outfit', then 'body'",
				"For example: #'/alter testplayer outfit set 12109901'",
				"This will make <testplayer> look like danter" };

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
