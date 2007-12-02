package utilities;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;

/**
 * Helper methods for testing quests
 *
 * @author hendrik
 */
public class QuestHelper {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		PlayerHelper.generatePlayerRPClasses();
		PlayerHelper.generateItemRPClasses();
		PlayerHelper.generateNPCRPClasses();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		NPCList.get().clear();
	}

}
