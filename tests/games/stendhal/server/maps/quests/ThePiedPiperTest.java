package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.client.entity.Creature;
import games.stendhal.common.Grammar;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.KillNotificationCreature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;

public class ThePiedPiperTest {


	// private static String questSlot = "the_pied_piper";
	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static ThePiedPiper quest = new ThePiedPiper();
	private int rewardMoneys = 0;
	private int[] killedRats = {0,0,0,0,0,0};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone playerzone = new StendhalRPZone("int_semos_guard_house");
		SingletonRepository.getRPWorld().addRPZone(playerzone);

		for(int i=0; i<ThePiedPiper.RAT_ZONES.size();i++) {
			StendhalRPZone ratZone = new StendhalRPZone(ThePiedPiper.RAT_ZONES.get(i),100,100);
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
		PlayerTestHelper.equipWithItem(player, "golden blade");
		player.setAdminLevel(600);
		player.setATKXP(100000000);
		player.setDEFXP(100000000);
		player.setXP(100000000);
		player.setHP(10000);	
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testInactivePhase() {
		
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
	}
	/**
	 * Tests for quest2.
	 */
	@Test
	public void testInvasionPhaseStart() {
		// [17:50] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		quest.PhaseInactiveToInvasion();
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There are still about "+quest.getRatsCount()+" rats alive.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
	
	private void killRat(KillNotificationCreature rat, int count) {
		do {
			MockStendhalRPRuleProcessor.get().beginTurn();
			// prevent player killing
			player.setHP(10000);
			if(player.isPoisoned()) {
				player.healPoison();
			};
			player.teleport(rat.getZone(), rat.getX()+1, rat.getY(), null, player);
			player.setTarget(rat);
			player.attack();
			MockStendhalRPRuleProcessor.get().endTurn();
		} while (player.isAttacking());
		Logger.getLogger("ThePiedPiperTest").info(
				"killed "+rat.getName()+". #"+count);
		MockStendhalRPRuleProcessor.get().beginTurn();
		MockStendhalRPRuleProcessor.get().endTurn();
	}
	
	private void killRats(int numb) {
		int count=0;
		Logger.getLogger("ThePiedPiperTest").info("number of rats to kill: "+numb);
		for (int i=0; i<numb;i++) {
			String name = quest.rats.get(0).getName();
			int kind = ThePiedPiper.RAT_TYPES.indexOf(name);
			killRat(quest.rats.get(0),count);
			count++;			
			killedRats[kind]++;
			rewardMoneys = rewardMoneys + ThePiedPiper.RAT_REWARDS.get(kind);
			//Logger.getLogger("ThePiedPiperTest").info("quest slot: "+player.getQuest("the_pied_piper"));
		}		
	}
	
	private String details() {
		final StringBuilder sb = new StringBuilder();
		int kills = 0;
		for(int i=0; i<ThePiedPiper.RAT_TYPES.size(); i++) {
				kills=killedRats[i];
			// must add 'and' word before last creature in list
			if(i==(ThePiedPiper.RAT_TYPES.size()-1)) {
				sb.append("and ");
			};

			sb.append(Grammar.quantityplnoun(kills, ThePiedPiper.RAT_TYPES.get(i)));
			sb.append(", ");
		}
		return(sb.toString());
	}
	
	@Test
	public void testInvasionPhaseEnd() {
		killRats(quest.getRatsCount());
		// [17:58] Mayor Chalmers shouts: No rats in Ados now, exclude those who always lived in storage and haunted house. Rats hunters are welcome to get their reward.
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
		en.step(player, "reward");
		
		assertEquals("Here is your "+ rewardMoneys +" money, thank you very much for your help.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
	
	@Ignore
	public void testInvasionPhase2() {
		// [18:09] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There is still about 30 rats alive.", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during the #rats invasion. "+
				  "To get a #reward you have to kill at least "+
				  "one rat at that time.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is being invaded by rats! "+
				  "I dont want to reward you now, "+
  				  " until all rats are dead.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		// [18:14] rat has been killed by helga
		// [18:14] helga earns 5 experience points.
	}
	
	@Ignore
	public void testAwaitingPhaseStart() {	
		// [18:19] Mayor Chalmers shouts: Saddanly, rats captured city, they are living now under all Ados buildings. I am now in need of call Piped Piper, rats exterminator. Thank to all who tryed to clean up Ados,  you are welcome to get your reward.

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("I called a rats exterminator. "+
	    		"You can get #reward for your help now, ask about #details "+
				  "if you want to know more.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from the last reward, you killed a rat, 0 caverats, 0 venomrats, 0 razorrats, 0 giantrats, and 0 archrats, so I will give you 10 money as a #reward for that job.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Here is your 10 money, thank you very much for your help.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
	
	@Ignore
	public void testAwaitingPhaseEnd() {
		// [19:20] Mayor Chalmers shouts: Thanx gods, rats is gone now, Pied Piper hypnotized them and lead away to dungeons. Those of you, who helped to Ados city with rats problem, can get your reward now.
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

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}

}
