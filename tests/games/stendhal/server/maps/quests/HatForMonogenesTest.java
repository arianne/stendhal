package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.city.GreeterNPC;
import games.stendhal.server.maps.semos.tavern.TraderNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class HatForMonogenesTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private SpeakerNPC npcXin = null;
	private Engine enXin = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		npc = new SpeakerNPC("Monogenes");
		NPCList.get().add(npc);
		SpeakerNPCFactory npcConf = new GreeterNPC();
		npcConf.createDialog(npc);
		en = npc.getEngine();

		ZoneConfigurator zoneConf = new TraderNPC();
		zoneConf.configureZone(new StendhalRPZone("int_semos_tavern"), null);
		npcXin = NPCList.get().get("Xin Blanca");
		enXin = npcXin.getEngine();

		AbstractQuest quest = new MeetMonogenes();
		quest.addToWorld();
		quest = new HatForMonogenes();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer();
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Hello there, stranger! Don't be too intimidated if people are quiet and reserved... the fear of Blordrough and his forces has spread all over the country, and we're all a bit concerned. I can offer a few tips on socializing though, would you like to hear them?", npc.get("text"));
		en.step(player, "no");
		assertEquals("And how are you supposed to know what's happening? By reading the Semos Tribune? Hah! Bye, then.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", npc.get("text"));
		en.step(player, "task");
		assertEquals("Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...", npc.get("text"));
		en.step(player, "hat");
		assertEquals("You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?", npc.get("text"));
		en.step(player, "no");
		assertEquals("You surely have more importants things to do, and little time to do them in. I'll just stay here and freeze to death, I guess... *sniff*", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hi again, player. How can I #help you this time?", npc.get("text"));
		en.step(player, "task");
		assertEquals("Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...", npc.get("text"));
		en.step(player, "hat");
		assertEquals("You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thanks, my good friend. I'll be waiting here for your return!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		Item item = ItemTestHelper.createItem("money", 25);
		player.getSlot("bag").add(item);
		enXin.step(player, "hi");
		assertEquals("Greetings! How may I help you?", npcXin.get("text"));
		enXin.step(player, "buy leather_helmet");
		assertEquals("1 leather_helmet will cost 25. Do you want to buy it?", npcXin.get("text"));
		enXin.step(player, "yes");
		assertEquals("Congratulations! Here is your leather_helmet!", npcXin.get("text"));
		enXin.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hey! Is that leather hat for me?", npc.get("text"));
		en.step(player, "no");
		assertEquals("I guess someone more fortunate will get his hat today... *sneeze*", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		en.step(player, "hi");
		assertEquals("Hey! Is that leather hat for me?", npc.get("text"));
		en.step(player, "yes");
		// [22:40] player earns 10 experience points.
		assertEquals("Bless you, my good friend! Now my head will stay nice and warm.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		// (sorry i meant to put it on ground to test if he noticed it went missing, i did, but i forgot i had one on my head too, he took that.)
	}
}
