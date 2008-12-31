package games.stendhal.client;

public class MockStendhalClient extends StendhalClient {

	protected MockStendhalClient(final String loggingProperties) {
		super(new UserContext(), new PerceptionDispatcher());
		client = this;
	}

}
