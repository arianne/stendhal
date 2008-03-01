package games.stendhal.client;

public class MockStendhalClient extends StendhalClient {

	protected MockStendhalClient(String loggingProperties) {
		super(loggingProperties);
		client = this;
	}

}
