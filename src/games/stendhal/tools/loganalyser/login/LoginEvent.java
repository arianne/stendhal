package games.stendhal.tools.loganalyser.login;

/**
 * a login event
 *
 * @author hendriks
 */
public class LoginEvent {

	private String username;
	private String address;
	private String timestamp;

	public LoginEvent(String username, String address, String timestamp) {
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

}
