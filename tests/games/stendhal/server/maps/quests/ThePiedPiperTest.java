package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.townhall.MayorNPC;

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
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
	
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MayorNPC().configureZone(zone, null);

				
		final AbstractQuest quest = new ThePiedPiper();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Mayor Chalmers");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't now under rats invasion. You still can get #reward for last time you helped. You can ask #details if you want", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during #rats invasion. For get #reward you have to kill at least one rat at that time.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
	@Ignore
	public void testQuest2() {
		// [17:50] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There is still about 27 rats alive.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

	
		// [17:58] Mayor Chalmers shouts: No rats in Ados now, exclude those who always lived in storage and haunted house. Rats hunters are welcome to get their reward.
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't now under rats invasion. You still can get reward for last time you helped. You can ask details if you want", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from last reward, you killed 19 rats, 3 caverats, 0 venomrats, 5 razorrats, 0 giantrats,  and 0 archrats, so i have to give you 1050 moneys as reward for that job.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Here is your 1050 moneys, thank you very much for your help.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// [18:09] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There is still about 30 rats alive.", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during rats invasion. For get reward you have to kill at least one rat at that time.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is under rats invasion. I dont want reward you now,  until all rats will die.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		// [18:14] rat has been killed by helga
		// [18:14] helga earns 5 experience points.
	    
		// [18:19] Mayor Chalmers shouts: Saddanly, rats captured city, they are living now under all Ados buildings. I am now in need of call Piped Piper, rats exterminator. Thank to all who tryed to clean up Ados,  you are welcome to get your reward.
		
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("I called rats exterminator. You can get reward for your help now, ask about details if you want to know it.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from last reward, you killed a rat, 0 caverats, 0 venomrats, 0 razorrats, 0 giantrats,  and 0 archrats, so i have to give you 10 moneys as reward for that job.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Here is your 10 moneys, thank you very much for your help.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// [19:20] Mayor Chalmers shouts: Thanx gods, rats is gone now, Pied Piper hypnotized them and lead away to dungeons. Those of you, who helped to Ados city with rats problem, can get your reward now.
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't now under rats invasion. You still can get reward for last time you helped. You can ask details if you want", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during rats invasion. For get reward you have to kill at least one rat at that time.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't now under rats invasion. You still can get reward for last time you helped. You can ask details if you want", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// [19:31] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is under rats invasion. I dont want reward you now,  until all rats will die.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There is still about 2 rats alive.", getReply(npc));
		assertEquals("Good day to you.", getReply(npc));

		// [19:42] Mayor Chalmers shouts: No rats in Ados now, exclude those who always lived in storage and haunted house. Rats hunters are welcome to get their reward.
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't now under rats invasion. You still can get reward for last time you helped. You can ask details if you want", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from last reward, you killed 23 rats, 3 caverats, 3 venomrats, a razorrat, a giantrat,  and 3 archrats, so i have to give you 3510 moneys as reward for that job.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// NOTE: this is testing a different quest
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "quest");
		assertEquals("Ados is in need of supplies. Go fetch a tuna sandwich and say complete, once you've brought it.", getReply(npc));
		en.step(player, "done");
		assertEquals("You didn't fetch a tuna sandwich yet. Go and get it and say complete only once you're done.", getReply(npc));
		en.step(player, "done");
		assertEquals("Good work! Let me thank you on behalf of the people of Ados!", getReply(npc));
		// [19:51] helga earns 1281887 experience points.
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		
		// [19:52] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		// [19:52] caverat has been killed by helga
		// [19:52] helga earns 10 experience points.
		
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There is still about 37 rats alive.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from last reward, you killed 23 rats, 4 caverats, 3 venomrats, a razorrat, a giantrat,  and 3 archrats, so i have to give you 3530 moneys as reward for that job.", getReply(npc));
		assertEquals("Good day to you.", getReply(npc));
		
		// [19:52] razorrat has been killed by helga
		// [19:52] helga earns 80 experience points.
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from last reward, you killed 23 rats, 4 caverats, 3 venomrats, 2 razorrats, a giantrat,  and 3 archrats, so i have to give you 3690 moneys as reward for that job.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is under rats invasion. I dont want reward you now,  until all rats will die.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		// -----------------------------------------------

	}
}
