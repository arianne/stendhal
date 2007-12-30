package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.npc.NPCList;
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
		NPCList.get().add(npc);
		SpeakerNPCFactory npcConf = new SellerNPC();
		npcConf.createDialog(npc);

		AbstractQuest quest = new LearnAboutKarma();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
		player.addKarma(-1 * player.getKarma());
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));
		en.step(player, "help");
		assertEquals("You can take one of my prepared medicines with you on your travels; just ask for an #offer.", npc.get("text"));
		en.step(player, "offer");
		assertEquals("I sell antidote, greater_antidote, potion, and greater_potion.", npc.get("text"));
		en.step(player, "task");
		assertEquals("Are you someone who likes to help others?", npc.get("text"));
		en.step(player, "no");
		assertEquals("I knew it ... you probably have bad #karma.", npc.get("text"));
		en.step(player, "karma");
		assertEquals("When you do a good thing you get good karma. Good karma means you're likely to do well in battle and when fishing or searching for something like gold. Do you want to know what your karma is now?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -10.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));
		en.step(player, "task");
		assertEquals("I don't need anything but I can tell you your #karma.", npc.get("text"));
		en.step(player, "karma");
		assertEquals("When you do a good thing you get good karma. Good karma means you're likely to do well in battle and when fishing or searching for something like gold. Do you want to know what your karma is now?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -10.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------
		// start quest again (clean)
		player.setQuest("learn_karma", null);

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));
		en.step(player, "job");
		assertEquals("I make potions and antidotes, to #offer to warriors.", npc.get("text"));
		en.step(player, "task");
		assertEquals("Are you someone who likes to help others?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Wonderful! You must have good #karma.", npc.get("text"));
		en.step(player, "karma");
		assertEquals("When you do a good thing you get good karma. Good karma means you're likely to do well in battle and when fishing or searching for something like gold. Do you want to know what your karma is now?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -5.", npc.get("text"));
		en.step(player, "karma");
		assertEquals("When you do a good thing you get good karma. Good karma means you're likely to do well in battle and when fishing or searching for something like gold. Do you want to know what your karma is now?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Your karma is roughly -5.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
	}
}
