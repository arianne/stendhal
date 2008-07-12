package games.stendhal.client;

public class MockStendhalClient extends StendhalClient {

	protected MockStendhalClient(final String loggingProperties) {
		super(loggingProperties);
		client = this;
	}

}
