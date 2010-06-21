package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.guardhouse.RetiredAdventurerNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;

public class MeetHayunnTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new RetiredAdventurerNPC().configureZone(zone, null); 
		npc = SingletonRepository.getNPCList().get("Hayunn Naratha");
		en = npc.getEngine();

		AbstractQuest quest = new MeetHayunn();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Hayunn Naratha");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hi. I bet you've been sent here to learn about adventuring from me. First, lets see what you're made of. Go and kill a rat outside, you should be able to find one easily. Do you want to learn how to attack it, before you go?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well, back when I was a young adventurer, I clicked on my enemies to attack them. I'm sure that will work for you, too. Good luck, and come back once you are done.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		
		
		en.step(player, "hi");
		assertEquals("I see you haven't managed to kill a rat yet. Do you need me to tell you how to fight them?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Well, back when I was a young adventurer, I clicked on my enemies to attack them. I'm sure that will work for you, too. Good luck, and come back once you are done.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// [15:13] rat has been killed by omerob
		player.setSoloKill("rat");

		en.step(player, "hi");
		// [15:14] omerob earns 10 experience points.
		assertEquals("You killed the rat! Now, you may ask, what is the point behind risking your life to kill things? #Yes?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Ah-ha! Well, you can loot items from corpses. Once you're close enough to the corpse to reach it, you can drag the items into your bag. Do you want to hear how to identify items? #Yes?", getReply(npc));
		en.step(player, "yes");
		assertEquals("You can right-click on the items and select LOOK to get a description. Now, I know what you're thinking; how are you going to survive without getting killed? Do you want to know?", getReply(npc));
		en.step(player, "yes");
		assertEquals("You need to eat regularly! By right-clicking a food item - either your bag or on the ground - you can slowly regain your health with each bite. That takes time of course, and there are ways to regain your health instantly... want to hear?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Once you've earned enough money, you should visit one of the healers in Semos - Carmen or Ilisa - and buy a potion. Potions are very handy when you're alone in Semos dungeons. Do you want to know where Semos is?", getReply(npc));
		en.step(player, "yes");
		// [15:14] omerob earns 10 experience points.
		assertEquals("Follow the path through this village to the east, and you can't miss Semos. If you go and speak to Monogenes, the old man in this picture, he will give you a map. Here's 5 money to get you started. Bye bye!", getReply(npc));
		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals("Hello again. Have you come to learn more from me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Perhaps you have found Semos dungeons by now. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it. #Yes?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Simple, really; just double-click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?", getReply(npc));
		en.step(player, "yes");
		// [15:14] omerob earns 20 experience points.
		assertEquals("You can find many frequently asked questions are answered at #http://stendhalgame.org/wiki/StendhalFAQ \n"
			+ "You can find out about experience points and levelling up at #http://stendhalgame.org/wiki/LevelTables \n" 
			+ "You can read about some of the currently most powerful and successful warriors at #http://stendhalgame.org\n"
			+ " Well, good luck in the dungeons! This shield should help you. Here's hoping you find fame and glory, and keep watch for monsters!", getReply(npc));

		// -----------------------------------------------
		assertEquals("done", player.getQuest("meet_hayunn"));

	}
}