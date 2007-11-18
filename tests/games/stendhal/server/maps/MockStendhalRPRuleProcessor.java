package games.stendhal.server.maps;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;

public class MockStendhalRPRuleProcessor extends StendhalRPRuleProcessor {

	public static StendhalRPRuleProcessor get() {
		if (instance == null) {
			instance = new MockStendhalRPRuleProcessor();
		} else {
			assertTrue("JUnit tests should only use MockStendhalRPRuleProcessor, not StendhalRPRuleProcessor",
						instance instanceof MockStendhalRPRuleProcessor);
		}

		return instance;
	}

	@Override
	public void removePlayerPrivateText(Player player) {
		// playersRmText.add(player);
	}

	@Override
	public void addGameEvent(String source, String event, String... params) {

	}

	@Override
	public int getTurn() {
		return 0;
	}
}
