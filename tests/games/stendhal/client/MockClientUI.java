package games.stendhal.client;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.common.ErrorBuffer;
import games.stendhal.common.NotificationType;

/**
 * Mock client as replacement for j2DClient.
 *
 * @author Martin Fuchs
 */
public class MockClientUI extends j2DClient {
	private ErrorBuffer buffer = new ErrorBuffer();

	@Override
	public void addEventLine(final String header, final String text, final NotificationType type) {
		buffer.setError(text);
	}

	public String getEventBuffer() {
		if (buffer.hasError()) {
			return buffer.getErrorString();
		} else {
			return "";
		}
	}
}
