package games.stendhal.tools.loganalyser.login;

/**
 * a login event.
 *
 * @author hendriks
 */
public class LoginEvent {

	private final String username;
	private final String address;
	private final String timestamp;

	public LoginEvent(final String username, final String address, final String timestamp) {
		this.username = username;
		this.address = address;
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public String getAddress() {
		return address;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(username);
		sb.append("\t");
		sb.append(address);
		sb.append("\t");
		sb.append(timestamp);
		return sb.toString();
	}
}
