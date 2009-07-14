package utilities;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.BeforeClass;

import utilities.RPClass.ItemTestHelper;

/**
 * Helper methods for testing quests.
 *
 * @author hendrik
 */
public abstract class QuestHelper extends PlayerTestHelper  {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
		generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
		generateNPCRPClasses();

		MockStendhalRPRuleProcessor.get();
		// load item configurations to handle money and other items
		SingletonRepository.getEntityManager();

		SingletonRepository.getNPCList().clear();
	}

}
