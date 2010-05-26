package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;
import games.stendhal.common.Grammar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;

public class KillEnemyArmyTest {
	
	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static KillEnemyArmy quest = new KillEnemyArmy();
	private static StendhalRPZone playerzone;
	private final static int Xpos = 10;
	private final static int Ypos = 10;
	private static Logger logger = Logger.getLogger(KillEnemyArmyTest.class);
	private final String QUEST_SLOT = quest.getSlotName();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		playerzone = new StendhalRPZone("int_semos_guard_house",100,100);
		SingletonRepository.getRPWorld().addRPZone(playerzone);
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new BuyerNPC().configureZone(zone, null);
		npc = SingletonRepository.getNPCList().get("Despot Halb Errvl");
		en = npc.getEngine();		
		quest.addToWorld();
	}
	
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		PlayerTestHelper.registerPlayer(player);
		PlayerTestHelper.equipWithItem(player, "money");
		
		player.teleport(playerzone, Xpos, Ypos, null, player);
	}

	/**
	 * function for emulating killing of quest monsters by player.
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomMonsters(int numb) {
		for(int i=0; i<numb; i++) {		
			
		}
		logger.debug("killed "+ numb + " creatures.");
	}
	
	@Test
	public void TestChatting() {
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();
		assertEquals("I need help in battles with #enemy "+monstersType+
				" armies. They really annoying me. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I already explained to you what i need. Are you an idiot, as you cant remember this simple thing about #"+monstersType+"?", getReply(npc));		
		en.step(player, "enemy");
		final String expectingAnswer = quest.enemyForces.get(monstersType).second();
		assertEquals(expectingAnswer, getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));		
	}
	
	@Ignore
	public void TestKilling() {
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need help in battles with #Blordrough warriors. "+
				"They really annoying me. Kill at least 100 of any "+
				"blordrough soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

//		killed = quest.killsnumber-1;
		KillRandomMonsters(killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("You killed only "+killed+" blordrough "+
				Grammar.plnoun(killed, "soldier")+".", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// make it full number.
		KillRandomMonsters(1);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		int tempxp = player.getXP();
		int tempmoneys = player.getEquippedItemClass("bag", "money").getQuantity();
		double tempkarma = player.getKarma();
		en.step(player, "quest");
		assertEquals("Good work! Take this moneys. And if you will need assassin job again, ask me in one week. I think they will try to fight me again.", getReply(npc));
        assertEquals(tempxp, player.getXP()-500000);
        assertEquals(tempmoneys, player.getEquippedItemClass("bag", "money").getQuantity()-50000);
        assertEquals(tempkarma, player.getKarma()-5, 0.000001);
        en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}	
	
	@Ignore
	public void TestExtraKilling() {
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));		
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		assertEquals("I need help in battles with #Blordrough warriors. "+
				"They really annoying me. Kill at least 100 of any "+
				"blordrough soldiers and i will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// killing 351 creature
		//killed = quest.killsnumber*3+quest.killsnumber/2+1;
		KillRandomMonsters(killed);	
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));		
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		double tempkarma = player.getKarma();
		en.step(player, "quest");		
//		assertEquals("Pretty good! You killed "+(killed-quest.killsnumber)+" extra "+
//				Grammar.plnoun(killed-quest.killsnumber, "soldier")+
//				"! Take this moneys, and remember, i may wish you to do this job again in one week!", 
//				getReply(npc));
		assertEquals(tempkarma, player.getKarma()-30, 0.000001);
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));	
	}
}
