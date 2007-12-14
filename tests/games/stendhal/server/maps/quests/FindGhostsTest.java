package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.city.KidGhostNPC;
import games.stendhal.server.maps.ados.hauntedhouse.WomanGhostNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class FindGhostsTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private SpeakerNPC npcGhost = null;
	private Engine enGhost = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		ZoneConfigurator zoneConf = new WomanGhostNPC();
		zoneConf.configureZone(zone, null);
		npc = NPCList.get().get("Carena");
		en = npc.getEngine();

		zoneConf = new KidGhostNPC();
		zoneConf.configureZone(zone, null);

		zoneConf = new games.stendhal.server.maps.athor.cave.GhostNPC();
		zoneConf.configureZone(zone, null);

		zoneConf = new games.stendhal.server.maps.orril.dungeon.GhostNPC();
		zoneConf.configureZone(zone, null);

		zoneConf = new games.stendhal.server.maps.wofol.house5.GhostNPC();
		zoneConf.configureZone(zone, null);
		
		AbstractQuest quest = new FindGhosts();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer();
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Wooouhhhhhh!", npc.get("text"));
		en.step(player, "help");
		assertEquals("Here is a warning: if you die, you will become a ghost like me, partially visible and intangible. But if you can find your way out of the afterlife, you will be reborn.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I feel so lonely. I only ever see creatures and alive people. If I knew about #spirits like me, I would feel better.", npc.get("text"));
		en.step(player, "spirits");
		assertEquals("I sense that there are 4 other spirits, but if only I knew their names I could contact them. Will you find them, then come back and tell me their names?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Oh. Never mind. Perhaps since I'm only a ghost I couldn't offer you much reward anyway.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Wooouhhhhhh!", npc.get("text"));
		en.step(player, "task");
		assertEquals("I feel so lonely. I only ever see creatures and alive people. If I knew about #spirits like me, I would feel better.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("That's lovely of you. Good luck searching for them.", npc.get("text"));
		en.step(player, "hi");
		en.step(player, "bye");
		assertEquals("Bye", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("If you found any #spirits, please tell me their name.", npc.get("text"));
		en.step(player, "spirits");
		assertEquals("I seek to know more about other spirits who are dead but stalk the earthly world as ghosts. Please tell me any names you know.", npc.get("text"));
		en.step(player, "Vodka");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", npc.get("text"));
		en.step(player, "Ben");
		assertEquals("I don't believe you've spoken with any spirit of that name. If you met any other spirits, please tell me their name.", npc.get("text"));
		en.step(player, "Mark");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", npc.get("text"));
		en.step(player, "Mary");
		assertEquals("I don't believe you've spoken with any spirit of that name. If you met any other spirits, please tell me their name.", npc.get("text"));
		en.step(player, "Whiskey");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", npc.get("text"));
		en.step(player, "bye");
		// TODO: fix this bug. Expected: assertEquals("Bye", npc.get("text"));
		en.setCurrentState(ConversationStates.IDLE);

		// -----------------------------------------------

		npcGhost = NPCList.get().get("Mary");
		enGhost = npcGhost.getEngine();
		enGhost.step(player, "hi");
		assertEquals("Remember my name ... Mary ... Mary ...", npcGhost.get("text"));
		// [22:26] superkym earns 100 experience points. 

		// -----------------------------------------------

		npcGhost = NPCList.get().get("Ben");
		enGhost = npcGhost.getEngine();
		enGhost.step(player, "hi");
		assertEquals("Hello! Hardly anyone speaks to me. The other children pretend I don't exist. I hope you remember me.", npcGhost.get("text"));
		// [22:26] superkym earns 100 experience points. 

		// -----------------------------------------------

		npcGhost = NPCList.get().get("Goran");
		enGhost = npcGhost.getEngine();
		enGhost.step(player, "hi");
		assertEquals("Remember my name ... Goran ... Goran ...", npcGhost.get("text"));
		// [22:26] superkym earns 100 experience points. 

		// -----------------------------------------------

		npcGhost = NPCList.get().get("Zak");
		enGhost = npcGhost.getEngine();
		enGhost.step(player, "hi");
		assertEquals("Remember my name ... Zak ... Zak ...", npcGhost.get("text"));
		// [22:26] superkym earns 100 experience points. 

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("If you found any #spirits, please tell me their name.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", npc.get("text"));
		en.step(player, "Mary");
		assertEquals("Thank you. If you met any other spirits, please tell me their name.", npc.get("text"));
		en.step(player, "Brandy");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", npc.get("text"));
		en.step(player, "Ben");
		assertEquals("Thank you. If you met any other spirits, please tell me their name.", npc.get("text"));
		en.step(player, "Zak");
		assertEquals("Thank you. If you met any other spirits, please tell me their name.", npc.get("text"));
		en.step(player, "Goran");
		assertEquals("Thank you. Now that I know those 4 names, perhaps I can even reach the spirits with my mind. I can't give you anything of material value, but I have given you a boost to your basic wellbeing, which will last forever. May you live long, and prosper.", npc.get("text"));
		// [22:27] superkym heals 50 health points. 
		// [22:27] superkym earns 5000 experience points. 
		en.step(player, "bye");
		assertEquals("Bye", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Wooouhhhhhh!", npc.get("text"));
		en.step(player, "task");
		assertEquals("Thank you! I feel better now that I know the names of other spirits on Fauimoni.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye", npc.get("text"));
	}
}

