package games.stendhal.server.entity.item.consumption;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.ConsumableTestHelper;

public class ImmunizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Test
	public void testFeed() {
		int startTurn = TurnNotifier.get().getCurrentTurnForDebugging();
		if (startTurn < 0) {
			startTurn = 0;
		}
	
		TurnNotifier.get().logic(startTurn + 1);
		assertEquals(startTurn + 1, TurnNotifier.get().getCurrentTurnForDebugging());
		
		
		Immunizer immu = new Immunizer();
		
		ConsumableItem item = ConsumableTestHelper.createImmunizer("antidote");
		item.put("id", 1);
		Player player = PlayerTestHelper.createPlayer("herrkules");
		assertFalse(player.isImmune());
		assertTrue(immu.feed(item, player));
		assertTrue(player.isImmune());
		ConsumableItem item2 = ConsumableTestHelper.createImmunizer("antidote");
		item2.put("id", 2);

		assertEquals(2, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		
		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		
		assertEquals(1, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		assertThat(player.events().size(), is(0));
		assertTrue(immu.feed(item2, player));
		
		assertThat(player.events().size(), is(0));
		assertTrue(player.isImmune());
		assertEquals(2, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		
		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertTrue(player.isImmune());
		assertEquals(1, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		
		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertFalse(player.isImmune());
		assertEquals(-1, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		assertThat(player.events().size(), is(1));
		assertThat(player.events().get(0).get("text"), is("You are not immune from poison anymore."));
		
		TurnNotifier.get().logic(TurnNotifier.get().getCurrentTurnForDebugging() + 1);
		assertFalse(player.isImmune());
		assertEquals(-1, TurnNotifier.get().getRemainingTurns(new AntidoteEater(player)));
		
	}

}
