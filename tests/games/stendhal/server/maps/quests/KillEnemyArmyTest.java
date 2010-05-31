package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.mithrilbourgh.throne_room.BuyerNPC;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

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
	 * @param player - killer
	 * @param numb - number of creatures for killing
	 */
	public void KillRandomMonsters(final Player player, int numb) {
		List<String> monsters = quest.enemys.get(player.getQuest(QUEST_SLOT, 1));
		for(int i=0; i<numb; i++) {
			if(Rand.throwCoin()==0) {
				player.setSoloKill(monsters.get(Rand.rand((monsters.size()-1))));
			} else {
				player.setSharedKill(monsters.get(Rand.rand((monsters.size()-1))));
			};
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
		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();
		final String expectingAnswer = quest.enemyForces.get(monstersType).second();

		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I already explained to you what I need. Are you an idiot, as you can't remember this simple thing about the #enemy "+monstersType+" armies?", getReply(npc));
		en.step(player, "enemy");
		assertEquals(expectingAnswer, getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestKilling() {
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");

		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();
		//final String expectingAnswer = quest.enemyForces.get(monstersType).second();

		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));


        killed=1;
		KillRandomMonsters(player, killed);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "quest");

		assertEquals("You killed only "+killed+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1))+
		". You have to kill at least "+killsnumb+" "+Grammar.plnoun(killed, player.getQuest(QUEST_SLOT, 1)), getReply(npc));

		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// make it full number.
		KillRandomMonsters(player, killsnumb-1);
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		int tempxp = player.getXP();
		int tempmoneys = player.getEquippedItemClass("bag", "money").getQuantity();
		double tempkarma = player.getKarma();
		en.step(player, "quest");
        assertEquals("Good work! Take these coins. And if you need an assassin job again, ask me in one week. My advisors tell me they may try to fight me again.", getReply(npc));
        assertEquals(tempxp, player.getXP()-500000);
        assertEquals(tempmoneys, player.getEquippedItemClass("bag", "money").getQuantity()-50000);
        assertEquals(tempkarma, player.getKarma()-5, 0.000001);
        en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void TestExtraKilling() {
		int killed=0;
		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well state what you want then!", getReply(npc));
		en.step(player, "quest");
		// we have to write here which enemy type player got.
		final String monstersType=player.getQuest(QUEST_SLOT, 1);
		final int killsnumb=quest.enemyForces.get(monstersType).first();

		assertEquals("I need help to defeat #enemy "+monstersType+
				" armies. They are a grave concern. Kill at least "+killsnumb+
				" of any "+monstersType+
				" soldiers and I will reward you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// will kill 2x monsters for get 15 karma in total
		killed=killsnumb*2;

		double tempkarma = player.getKarma();
		KillRandomMonsters(player, killed);

		en.step(player, "hi");
		assertEquals("I hope you have disturbed me for a good reason?", getReply(npc));

		en.step(player, "quest");
		assertEquals("Pretty good! You killed "+(killed-killsnumb)+
				" extra " +	Grammar.plnoun(killed-killsnumb, "soldier")+
				"! Take these coins, and remember, I may wish you to do this job again in one week!", getReply(npc));

		assertEquals(tempkarma, player.getKarma()-15.0, 0.000001);
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
