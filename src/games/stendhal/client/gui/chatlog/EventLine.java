package games.stendhal.client.gui.chatlog;

import games.stendhal.common.NotificationType;

public class EventLine {
	private String header;
	private String text;
	private NotificationType type;

	public EventLine(String header, String text, NotificationType type) {
		this.header = header;
		this.text = text;
		this.type = type;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
}