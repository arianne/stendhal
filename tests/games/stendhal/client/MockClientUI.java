package games.stendhal.client;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.ErrorBuffer;

/**
 * Mock client as replacement for j2DClient.
 *
 * @author Martin Fuchs
 */
public class MockClientUI extends j2DClient {
	private final ErrorBuffer buffer = new ErrorBuffer();

	@Override
	public void addEventLine(EventLine line) {
		buffer.setError(line.getText());
	}

	public String getEventBuffer() {
		if (buffer.hasError()) {
			return buffer.getErrorString();
		} else {
			return "";
		}
	}
}
