package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.fado.hut.SellerNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class LearnAboutKarmaTest {

	private static final String KARMA_ANSWER = "When you do a good thing like a #task for someone else, you get good karma. Good karma means you're likely to do well in battle and when fishing or searching for something like gold. Do you want to know what your karma is now?";
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		npc = new SpeakerNPC("Sarzina");
		SingletonRepository.getNPCList().add(npc);
		final SpeakerNPCFactory npcConf = new SellerNPC();
		npcConf.createDialog(npc);

		final AbstractQuest quest = new LearnAboutKarma();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
		player.addKarma(-1 * player.getKarma());
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "help");
		assertEquals("You can take one of my prepared medicines with you on your travels; just ask for an #offer.", getReply(npc));
		en.step(player, "offer");
		assertEquals("I sell antidote, greater antidote, potion, and greater potion.", getReply(npc));
		en.step(player, "task");
		assertEquals("Are you someone who likes to help others?", getReply(npc));
		en.step(player, "no");
		assertEquals("I knew it ... you probably have bad #karma.", getReply(npc));
		en.step(player, "karma");
		assertEquals(KARMA_ANSWER, getReply(npc));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -10.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "task");
		assertEquals("If you want to get good #karma all you have to do is be helpful to others. I know a hunter girl called Sally who needs wood, and I know another girl called Annie who loves icecream, well, I know many people who needs tasks doing for them regularly and I'm sure if you help them you will be rewarded, that's how karma works after all.", getReply(npc));
		en.step(player, "karma");
		assertEquals(KARMA_ANSWER, getReply(npc));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -10.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		// start quest again (clean)
		player.setQuest("learn_karma", null);

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "job");
		assertEquals("I make potions and antidotes, to #offer to warriors.", getReply(npc));
		en.step(player, "task");
		assertEquals("Are you someone who likes to help others?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Wonderful! You must have good #karma.", getReply(npc));
		en.step(player, "karma");
		assertEquals(KARMA_ANSWER, getReply(npc));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -5.", getReply(npc));
		en.step(player, "karma");
		assertEquals(KARMA_ANSWER, getReply(npc));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -5.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
