package games.stendhal.server.actions.admin;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class AlterQuestActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		AlterQuestAction.register();
	}

	@Test
	public void alterQuestActionPerform() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		MockStendhalRPRuleProcessor rules = MockStendhalRPRuleProcessor.get();
		rules.addPlayer(bob);
		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		AlterQuestAction aq = new AlterQuestAction();
		aq.perform(bob, action);
		assertTrue(bob.hasQuest("questname"));
		assertEquals("queststate", bob.getQuest("questname"));

	}

	@Test
	public void alterQuestActionPerformTarget() throws Exception {

		Player bob = PlayerTestHelper.createPlayer("bob");
		Player james = PlayerTestHelper.createPlayer("james");
		MockStendhalRPRuleProcessor rules = MockStendhalRPRuleProcessor.get();
		rules.addPlayer(james);
		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "james");
		action.put("name", "questname");
		action.put("state", "queststate");
		AlterQuestAction aq = new AlterQuestAction();
		aq.perform(bob, action);
		assertTrue(james.hasQuest("questname"));
		assertEquals("queststate", james.getQuest("questname"));
	}

	@Test
	public void alterQuestActionPerformthroughCommandcenter() throws Exception {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		pl.put("adminlevel", 5000);

		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		CommandCenter.execute(pl, action);
		assertTrue(bob.hasQuest("questname"));
		assertEquals("queststate", bob.getQuest("questname"));
		action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		
		CommandCenter.execute(pl, action);
		assertFalse(bob.hasQuest("questname"));
	
	}
	@Test
	public void alterQuestActionCastersLeveltoLow() throws Exception {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		pl.put("adminlevel", 0);

		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", "bob");
		action.put("name", "questname");
		action.put("state", "queststate");
		CommandCenter.execute(pl, action);
		assertFalse(bob.hasQuest("questname"));
	}
}
