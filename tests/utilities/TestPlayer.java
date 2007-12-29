package utilities;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * A mock player used to catch private message texts.
 */
public class TestPlayer extends Player {
	private String privateText;

	/**
	 * Creates a new mock player without name.
	 */
	public TestPlayer(RPObject obj) {
		super(obj);
	}

	/**
	 * Creates a new mock player with name.
	 */
	public TestPlayer(RPObject obj, String name) {
		this(obj);
		setName(name);
	}

	/**
	 * Catch private message texts.
	 */
	@Override
	public void sendPrivateText(NotificationType type, String text) {
		sendPrivateText(text);
	}

	/**
	 * Catch private message texts.
	 */
	@Override
	public void sendPrivateText(String text) {
		if (this.privateText != null) {
			this.privateText += "\r\n" + text;
		} else {
			this.privateText = text;
		}
	}

	/**
	 * Gets the concatenated private message texts
	 * since the last resetPrivateText() call.
	 *
	 * @return last private message
	 */
	@Override
	public String getPrivateText() {
		return privateText;
	}

	/**
	 * Reset private message text.
	 */
	public void resetPrivateText() {
		privateText = null;
	}
}
