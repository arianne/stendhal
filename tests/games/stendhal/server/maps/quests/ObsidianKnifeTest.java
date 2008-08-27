package games.stendhal.server.maps.quests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.library.LibrarianNPC;
import games.stendhal.server.maps.wofol.blacksmith.BlacksmithNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class ObsidianKnifeTest {


	private static String questSlot = "obsidian_knife";
	
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		
		new BlacksmithNPC().configureZone(zone, null);
		new LibrarianNPC().configureZone(zone, null);
			
		final AbstractQuest quest = new ObsidianKnife();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Alrak");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", npc.get("text"));
		en.step(player, "task");
		assertEquals("You know, it's hard to get food round here. I don't have any #supplies for next year.", npc.get("text"));
		en.step(player, "supplies");
		assertTrue(npc.get("text").startsWith("If you could get me 100 pieces of "));
		en.step(player, "yes");
		assertTrue(npc.get("text").startsWith("Thank you! I hope it doesn't take too long to collect. Don't forget to say "));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		
		// alrak might have asked for meat, ham or cheese. we choose a definite one for testing.
		player.setQuest(questSlot,"meat");
		
		Item item = ItemTestHelper.createItem("meat", 100);
		player.getSlot("bag").add(item);
		final int xp = player.getXP();
		final double oldKarma = player.getKarma();
		
		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", npc.get("text"));
		en.step(player, "meat");
		assertFalse(player.isEquipped("meat"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(oldKarma));
		assertThat(player.getQuest(questSlot), is("food_brought"));
		// [15:17] kymara earns 1000 experience points.
		assertEquals("Great! You brought the meat!", npc.get("text"));
		en.step(player, "task");
		assertEquals("Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.", npc.get("text"));
		en.step(player, "book");
		assertEquals("It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Shame, I would really like to learn more about precious stones. Ah well, good bye.", npc.get("text"));
		
		final double karma = player.getKarma();
		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", npc.get("text"));
		en.step(player, "task");
		assertEquals("Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.", npc.get("text"));
		en.step(player, "book");
		assertEquals("It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thanks. Try asking at a library for a 'gem book'.", npc.get("text"));
		assertThat(player.getQuest(questSlot), is("seeking_book"));
		assertThat(player.getKarma(), greaterThan(karma));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		

		en.step(player, "hi");
		assertEquals("Hello again. I hope you haven't forgotten about the gem book I wanted.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		
		npc = SingletonRepository.getNPCList().get("Ceryl");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));
		en.step(player, "gem book");
		assertEquals("You're in luck! Ognir brought it back just last week. Now, who is it for?", npc.get("text"));
		en.step(player, "me");
		assertEquals("Hm, you better check who it's really for.", npc.get("text"));
		en.step(player, "alrak");
		assertEquals("Ah, the mountain dwarf! Hope he enjoys the gem book.", npc.get("text"));
		assertTrue(player.isEquipped("blue book"));
		assertThat(player.getQuest(questSlot), is("got_book"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		
		// there are more steps but my client crashed and this is as far as I got
	}
}