package games.stendhal.server.maps.quests.thepiedpiper;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.PoisonStatus;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants;
import games.stendhal.server.maps.quests.piedpiper.TPPQuestHelperFunctions;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class TPPTestHelper implements ITPPQuestConstants {


	// private static String questSlot = "the_pied_piper";
	protected static Player player = null;
	protected static SpeakerNPC npc = null;
	protected static Engine en = null;
	protected final static ThePiedPiper quest = new ThePiedPiper();
	protected int rewardMoneys = 0;
	protected int[] killedRats = {0,0,0,0,0,0};
	private static Logger logger = Logger.getLogger(TPPTestHelper.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone playerzone = new StendhalRPZone("int_semos_guard_house");
		SingletonRepository.getRPWorld().addRPZone(playerzone);
		final StendhalRPZone piperzone = new StendhalRPZone("0_ados_city_n",100,100);
		SingletonRepository.getRPWorld().addRPZone(piperzone);

		// this is for piper movements
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("0_ados_wall_n2",100,100));
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("0_ados_city_n2",100,100));
		//SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("0_ados_city_n",100,100));
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("0_ados_city",100,100));
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("int_ados_town_hall",100,100));
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("int_ados_town_hall_1",100,100));
		SingletonRepository.getRPWorld().addRPZone(new StendhalRPZone("int_ados_town_hall_2",100,100));

		for(int i=0; i<RAT_ZONES.size();i++) {
			StendhalRPZone ratZone = new StendhalRPZone(RAT_ZONES.get(i),100,100);
			SingletonRepository.getRPWorld().addRPZone(ratZone);
		}

		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MayorNPC().configureZone(zone, null);
		quest.addToWorld();
		npc = SingletonRepository.getNPCList().get("Mayor Chalmers");
		en = npc.getEngine();

		/*
		 * creating player
		 */
		player = PlayerTestHelper.createPlayer("player");
		PlayerTestHelper.registerPlayer(player);
		PlayerTestHelper.equipWithItem(player, "rod of the gm");
		player.setAdminLevel(1000);
		player.setAtkXP(100000000);
		player.setDefXP(100000000);
		player.setXP(100000000);
		// according to E+08 xp
		player.setLevel(216);
		player.setHP(10000);
		player.addKarma(10000);
		player.setInvisible(true);
		player.setGhost(true);
	}

	/**
	 * function for emulating killing of rat by player.
	 * @param rat - creature for killing
	 * @param count - number of creature for logger
	 */
	private void killRat(Creature rat, int count) {
		do {
			// prevent player killing
			player.setHP(10000);
			if(player.hasStatus(StatusType.POISONED)) {
				player.getStatusList().removeAll(PoisonStatus.class);
			}
			player.teleport(rat.getZone(), rat.getX()+1, rat.getY(), null, player);
			player.setTarget(rat);
			//player.attack();

			MockStendlRPWorld.get().nextTurn();
			MockStendhalRPRuleProcessor.get().beginTurn();
			MockStendhalRPRuleProcessor.get().endTurn();

		} while (player.isAttacking());
		MockStendhalRPRuleProcessor.get().beginTurn();
		MockStendhalRPRuleProcessor.get().endTurn();
		logger.debug("killed "+rat.getName()+". #"+count);
	}

	/**
	 * function for killing creatures.
	 * @param numb - number of creatures to kill.
	 */
	protected void killRats(int numb) {
		int count=0;
		logger.info("number of rats to kill: "+numb);
		for (int i=0; i<numb;i++) {
			String name = TPPQuestHelperFunctions.getRats().get(0).getName();
			int kind = RAT_TYPES.indexOf(name);
			killRat(TPPQuestHelperFunctions.getRats().get(0),count);
			count++;
			killedRats[kind]++;
			rewardMoneys = rewardMoneys + RAT_REWARDS.get(kind);
			//logger.debug("player's quest slot: "+player.getQuest("the_pied_piper"));
		}
	}

	/**
	 * function for build npc's answer string based on killed creatures
	 * @return - npc's answer about details of killing to player
	 */
	protected String details() {
		final StringBuilder sb = new StringBuilder();
		int kills = 0;
		for(int i=0; i<RAT_TYPES.size(); i++) {
				kills=killedRats[i];
			// must add 'and' word before last creature in list
			if(i==(RAT_TYPES.size()-1)) {
				sb.append("and ");
			}

			sb.append(Grammar.quantityplnoun(kills, RAT_TYPES.get(i), "a"));
			sb.append(", ");
		}
		return(sb.toString());
	}

}
