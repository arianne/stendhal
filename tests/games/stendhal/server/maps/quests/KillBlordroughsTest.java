package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class KillBlordroughsTest {
	
	private Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;
	final static KillBlordroughs quest = new KillBlordroughs();
	private int rewardMoneys = 0;
	private static Logger logger = Logger.getLogger(KillBlordroughsTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone playerzone = new StendhalRPZone("int_semos_guard_house");
		SingletonRepository.getRPWorld().addRPZone(playerzone);
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new BuyerNPC().configureZone(zone, null);
		quest.addToWorld();
		npc = SingletonRepository.getNPCList().get("Despot Halb Errvl");
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
	}
	

	/**
	 * function for emulating killing of blordrough soldiers by player.
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomBlordroughs(int numb) {
		
		Creature blr = new Creature();
		
		for(int i=0; i<numb; i++) {
			do {
				MockStendlRPWorld.get().nextTurn();
				MockStendhalRPRuleProcessor.get().beginTurn();

				// prevent player killing
				player.setHP(10000);
				if(player.isPoisoned()) {
					player.healPoison();
				};
				player.teleport(blr.getZone(), blr.getX()+1, blr.getY(), null, player);
				player.setTarget(blr);
				//player.attack();
				MockStendhalRPRuleProcessor.get().endTurn();

			} while (player.isAttacking());
			MockStendhalRPRuleProcessor.get().beginTurn();
			MockStendhalRPRuleProcessor.get().endTurn();			
		}
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
		en.step(player, "quest");
		assertEquals("I already explained to you what i need. Are you idiot, as you cant remember this simple thing about #blordroughs?", getReply(npc));		
		en.step(player, "blordrough");
		assertEquals("My Mithrilbourgh army have great losses in battles with Blordrough soldiers. They coming from side of Ados tunnels.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
	
	@Test
	public void TestKilling() {
		
	}
	
}