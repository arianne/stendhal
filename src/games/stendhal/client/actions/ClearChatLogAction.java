package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;

public class ClearChatLogAction implements SlashAction {

	public boolean execute(final String[] params, final String remainder) {
		((j2DClient) j2DClient.get()).clearGameLog();
		return true;
	}

	public int getMaximumParameters() {
		return 0;
	}

	public int getMinimumParameters() {
		return 0;
	}

}
