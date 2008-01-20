package utilities;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

/**
 * Helper methods for testing quests.
 *
 * @author hendrik
 */
public abstract class QuestHelper extends PlayerTestHelper  {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		generatePlayerRPClasses();
		generateItemRPClasses();
		generateNPCRPClasses();

		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		StendhalRPWorld world = MockStendlRPWorld.get();

		// load item configurations to handle money and other items
		world.getRuleManager().getEntityManager();

		NPCList.get().clear();
	}

}
