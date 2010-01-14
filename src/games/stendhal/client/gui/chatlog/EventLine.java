package games.stendhal.client.gui.chatlog;

import games.stendhal.common.NotificationType;

public class EventLine {
	private String header;
	private String text;
	private NotificationType type;

	public EventLine(final String header, final String text, final NotificationType type) {
		this.header = header;
		this.text = text;
		this.type = type;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(final String header) {
		this.header = header;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(final NotificationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type + ": " + header + text;
	}
}
