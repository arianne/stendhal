package utilities;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

/**
 * Helper methods for testing quests.
 *
 * @author hendrik
 */
public class QuestHelper {
	
	public static void setUpBeforeClass() {
		Log4J.init();
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateItemRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		NPCList.get().clear();
	}

}
