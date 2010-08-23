package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class ThePiedPiperTest implements ITPPQuestConstants{


	// private static String questSlot = "the_pied_piper";
	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static ThePiedPiper quest = new ThePiedPiper();
	private int rewardMoneys = 0;
	private int[] killedRats = {0,0,0,0,0,0};
	private static Logger logger = Logger.getLogger(ThePiedPiperTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone playerzone = new StendhalRPZone("int_semos_guard_house");
		SingletonRepository.getRPWorld().addRPZone(playerzone);
		final StendhalRPZone piperzone = new StendhalRPZone("0_ados_city_n");
		SingletonRepository.getRPWorld().addRPZone(piperzone);

		for(int i=0; i<RAT_ZONES.size();i++) {
			StendhalRPZone ratZone = new StendhalRPZone(RAT_ZONES.get(i),100,100);
			SingletonRepository.getRPWorld().addRPZone(ratZone);
		}
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MayorNPC().configureZone(zone, null);
		quest.addToWorld();
		npc = SingletonRepository.getNPCList().get("Mayor Chalmers");
		en = npc.getEngine();		
	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		PlayerTestHelper.registerPlayer(player);
		PlayerTestHelper.equipWithItem(player, "rod of the gm");
		player.setAdminLevel(1000);
		player.setATKXP(100000000);
		player.setDEFXP(100000000);
		player.setXP(100000000);
		player.setHP(10000);	
		player.addKarma(10000);
		player.setInvisible(true);
		player.setGhost(true);
	}
	
	/**
	 * switching quest to next available phase.
	 */
	private void switchToNextPhase() {
		ThePiedPiper.getPhaseClass(
				ThePiedPiper.getPhase()).phaseToNextPhase(
						ThePiedPiper.getNextPhaseClass(
								ThePiedPiper.getPhase()), new LinkedList<String>());
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
			if(player.isPoisoned()) {
				player.healPoison();
			};
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
	private void killRats(int numb) {
		int count=0;
		logger.info("number of rats to kill: "+numb);
		for (int i=0; i<numb;i++) {
			String name = quest.rats.get(0).getName();
			int kind = RAT_TYPES.indexOf(name);
			killRat(quest.rats.get(0),count);
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
	private String details() {
		final StringBuilder sb = new StringBuilder();
		int kills = 0;
		for(int i=0; i<RAT_TYPES.size(); i++) {
				kills=killedRats[i];
			// must add 'and' word before last creature in list
			if(i==(RAT_TYPES.size()-1)) {
				sb.append("and ");
			};

			sb.append(Grammar.quantityplnoun(kills, RAT_TYPES.get(i), "a"));
			sb.append(", ");
		}
		return(sb.toString());
	}
	
	/**
	 * Tests for quest.
	 */
	@Test
	public void testInactivePhase() {	
		ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
		assertTrue(quest.getHistory(player).isEmpty());
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during the #rats invasion. "+
				  "To get a #reward you have to kill at least "+
				  "one rat at that time.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertTrue(quest.getHistory(player).isEmpty());
	}

	@Test
	public void testInvasionPhase() {
		// [17:50] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
        switchToNextPhase();
		//quest.phaseInactiveToInvasion();
        en.step(player, "bye"); // in case if previous test was failed
        en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There " + Grammar.isare(quest.getRatsCount()) + 
				" still about "+ quest.getRatsCount() + " rats alive.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertTrue(quest.getHistory(player).isEmpty());
		killRats(quest.getRatsCount());
		// [17:58] Mayor Chalmers shouts: No rats in Ados now, exclude those who always lived in storage and haunted house. Rats hunters are welcome to get their reward.
		LinkedList<String> questHistory = new LinkedList<String>();
		questHistory.add("I have killed some rats in Ados city already, and trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");
		
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("Please take "+ rewardMoneys +" money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));		
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));		
	}
	
	@Test
	public void testAccumulatingRewards() {
		int tempReward = 0;
		LinkedList<String> questHistory = new LinkedList<String>();
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
		switchToNextPhase();
		//quest.phaseInactiveToInvasion();
        // [18:09] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		en.step(player, "bye"); // in case if previous test was failed
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There "+ Grammar.isare(quest.getRatsCount()) +
				" still about "+ quest.getRatsCount() +" rats alive.", getReply(npc));
		en.step(player, "details");
		assertEquals("Ados is being invaded by rats! I dont want to either reward you or "+
				  "explain details to you now, until all rats are dead.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is being invaded by rats! "+
				  "I dont want to reward you now, "+
  				  " until all rats are dead.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		killRats(quest.getRatsCount());
		questHistory.add("I have killed some rats in Ados city already, and trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));		
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");	
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		tempReward = rewardMoneys;
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		switchToNextPhase();
		//quest.phaseInactiveToInvasion();	
		killRats(quest.getRatsCount());
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");	
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		assertTrue("", (rewardMoneys > tempReward));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");		
		assertEquals("Please take "+ rewardMoneys +" money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));		
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}	
	
	/**
	 * Tests for quest2.
	 */
	@Test
	public void testAwaitingPhase() {	
		LinkedList<String> questHistory = new LinkedList<String>();
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
		switchToNextPhase();
		//quest.phaseInactiveToInvasion();		
		killRats(quest.getRatsCount()/2);
		questHistory.add("I have killed some rats in Ados city already, and trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));		
		// [18:19] Mayor Chalmers shouts: Saddanly, rats captured city, they are living now under all Ados buildings. I am now in need of call Piped Piper, rats exterminator. Thank to all who tryed to clean up Ados,  you are welcome to get your reward.
		
		switchToNextPhase();
		//quest.phaseInvasionToAwaiting();		
		en.step(player, "bye"); // in case if previous test was failed
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
	//	assertEquals("I called a rats exterminator. "+
		assertEquals("The rats are gone. "+
	    		"You can get #reward for your help now, ask about #details "+
				  "if you want to know more.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you " + rewardMoneys + " money as a #reward for that job.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("Please take " + rewardMoneys + " money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));		
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// [19:20] Mayor Chalmers shouts: Thanx gods, rats is gone now, Pied Piper hypnotized them and lead away to dungeons. Those of you, who helped to Ados city with rats problem, can get your reward now.
		ThePiedPiper.getPhaseClass(
				ThePiedPiper.getPhase()).phaseToDefaultPhase(new LinkedList<String>());
		//quest.phaseAwaitingToInactive();
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
				  "get a #reward for the last time you helped. You can ask for #details "+
				  "if you want.", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during the #rats invasion. "+
				  "To get a #reward you have to kill at least "+
				  "one rat at that time.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
}
