package games.stendhal.server.maps;

import static org.junit.Assert.*;
import games.stendhal.server.entity.player.Player;

import static org.hamcrest.core.Is.*;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class MockStendhalRPRuleProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.removePlayer("bob");
	}

	@Test
	public void testGetTurn() {
		assertThat(MockStendhalRPRuleProcessor.get().getTurn(), is(0));
	}

	@Test
	public void testGet() {
		assertSame(MockStendhalRPRuleProcessor.get(),
				MockStendhalRPRuleProcessor.get());

	}

	@Test
	public void testAddPlayer() {
		MockStendhalRPRuleProcessor processor = MockStendhalRPRuleProcessor.get();
		assertThat(MockStendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(0));

		Player bob = PlayerTestHelper.createPlayer("bob");
		processor.addPlayer(bob);
		assertThat(MockStendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(1));
		assertSame(bob, processor.getPlayer("bob"));
		Player bob2 = PlayerTestHelper.createPlayer("bob");
		processor.addPlayer(bob2);
		assertThat(MockStendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(1));
		assertSame(bob2, processor.getPlayer("bob"));
		assertNotSame(bob, processor.getPlayer("bob"));

	}

}
