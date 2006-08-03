package games.stendhal.server;

import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import games.stendhal.server.maps.quests.IQuest;

public class StendhalQuestSystem {
	/** the logger instance. */
	private static final Logger logger = Log4J
			.getLogger(StendhalQuestSystem.class);

	private StendhalRPWorld world;

	private StendhalRPRuleProcessor rules;

	public StendhalQuestSystem(StendhalRPWorld world,
			StendhalRPRuleProcessor rules) {
		this.world = world;
		this.rules = rules;

		loadQuest("SheepGrowing");
		loadQuest("OrcishHappyMeal");
		loadQuest("LookBookforCeryl");
		loadQuest("IntroducePlayers");
		loadQuest("SevenCherubs");
		loadQuest("MeetHackim");
		loadQuest("MeetHayunn");
		loadQuest("MeetIo");
		loadQuest("MeetMonogenes");
		loadQuest("MeetNomyr");
		loadQuest("MeetZynn");
		loadQuest("BeerForHayunn");
		loadQuest("ArmorForDagobert");
		loadQuest("HatForMonogenes");
		loadQuest("NewsFromHackim");
		loadQuest("MeetKetteh");
		loadQuest("CleanStorageSpace");
		loadQuest("WeaponsCollector");
		loadQuest("ZooFood");
		loadQuest("CloaksForBario");
		loadQuest("Campfire");
	}

	public static void main(String[] args) {
		new StendhalQuestSystem(null, null);
	}

	private boolean loadQuest(String name) {
		try {
			Class entityClass = Class
					.forName("games.stendhal.server.maps.quests." + name);

			boolean implementsIQuest = false;
			if (!IQuest.class.isAssignableFrom(entityClass)) { 
				logger.error("Class " + name + " doesn't implement IQuest interface.");
				return false;
			}

			logger.info("Loading Quest: " + name);
			java.lang.reflect.Constructor constr = entityClass.getConstructor(
					StendhalRPWorld.class, StendhalRPRuleProcessor.class);

			// simply creatre a new instance. The constructor creates all
			// additionally objects
			constr.newInstance(world, rules);
			return true;
		} catch (Exception e) {
			logger.warn("Quest(" + name + ") loading failed.", e);
			return false;
		}
	}
}
