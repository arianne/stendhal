package games.stendhal.server.core.engine;

public class ChatMessage {
	public final String source;
	public final String message;
	public final String timestamp;
	
	/**
	 * a chat message from a source at a time.
	 *
	 * @param source - who it is from
	 * @param message
	 * @param timestamp - when the message was created
	 */
	public ChatMessage(final String source, final String message, final String timestamp) {
		this.source = source;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
}
