package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;
import games.stendhal.common.Rand;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;

public class KillBlordroughsTest {
	
	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static KillBlordroughs quest = new KillBlordroughs();
	private static StendhalRPZone playerzone;
	private final static int Xpos = 10;
	private final static int Ypos = 10;
	private static Logger logger = Logger.getLogger(KillBlordroughsTest.class);
	
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
		PlayerTestHelper.equipWithItem(player, "golden blade");
		PlayerTestHelper.equipWithItem(player, "mithril cloak");
		PlayerTestHelper.equipWithItem(player, "mithril boots");
		PlayerTestHelper.equipWithItem(player, "mithril legs");
		PlayerTestHelper.equipWithItem(player, "mithril armor");
		PlayerTestHelper.equipWithItem(player, "black helmet");
		
		player.setAdminLevel(1000);
		player.setATKXP(100000000);
		player.setDEFXP(100000000);
		player.setXP(100000000);
		player.setHP(10000);
		player.addKarma(10000);
		//player.setInvisible(true);
		player.teleport(playerzone, Xpos, Ypos, null, player);
	}
	
	/**
	 * function for emulating killing of blordrough soldiers by player.
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomBlordroughs(int numb) {
		final LinkedList<Creature> blrs = quest.getBlordroughs();
		if (blrs.size()<1) {
			logger.error("NO BLORDROUGHS LOADED!");
			return;
		}
		for(int i=0; i<numb; i++) {		
			Creature blr = blrs.get(Rand.rand(blrs.size()));	
			// cheat! :-)
			blr.setHP(1);
			StendhalRPAction.placeat(playerzone, blr, Xpos+1, Ypos);
			player.setTarget(blr);
			do {
				// prevent player killing
				player.setHP(10000);
				if(player.isPoisoned()) {
					player.healPoison();
				};
				player.teleport(blr.getZone(), blr.getX(), blr.getY(), null, player);
				player.setTarget(blr);
				MockStendlRPWorld.get().nextTurn();
				MockStendhalRPRuleProcessor.get().beginTurn();
				MockStendhalRPRuleProcessor.get().endTurn();

			} while (player.isAttacking());
			MockStendhalRPRuleProcessor.get().beginTurn();
			MockStendhalRPRuleProcessor.get().endTurn();
			//logger.info("killed "+i+" creature ("+blr.getName()+").");
		}
		logger.info("killed "+ numb + " creatures.");
	}
	
	@Test
	public void TestChatting() {
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
		
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I already explained to you what i need. Are you idiot, as you cant remember this simple thing about #blordroughs?", getReply(npc));		
		en.step(player, "blordrough");
		assertEquals("My Mithrilbourgh army have great losses in battles with Blordrough soldiers. They coming from side of Ados tunnels.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
	
	@Ignore
	public void TestKilling() {
		int killed;
		killed = quest.killsnumber-1;
		en.step(player, "bye");
		KillRandomBlordroughs(killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("You killed only "+killed+" blordrough soldiers.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));		
	}
	
}
