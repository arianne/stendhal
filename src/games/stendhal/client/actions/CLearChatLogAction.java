package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;

public class CLearChatLogAction implements SlashAction {

	public boolean execute(String[] params, String remainder) {
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
