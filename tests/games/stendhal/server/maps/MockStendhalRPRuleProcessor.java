package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPRuleProcessor;

public class MockStendhalRPRuleProcessor extends StendhalRPRuleProcessor {

	public static StendhalRPRuleProcessor get() {
		if (!(instance instanceof MockStendhalRPRuleProcessor)) {
			instance = new MockStendhalRPRuleProcessor();
		}

		return instance;
	}

	@Override
	public void addGameEvent(String source, String event, String... params) {
		// do not log to database during test
	}

	@Override
	public int getTurn() {
		return 0;
	}
}
