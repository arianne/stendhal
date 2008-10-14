package games.stendhal.client.gui.chatlog;

import games.stendhal.common.NotificationType;

public class StandardEventLine extends EventLine {

	public StandardEventLine(final String text) {
		super("", text,  NotificationType.NORMAL);
	}

}
