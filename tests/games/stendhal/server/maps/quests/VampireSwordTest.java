package games.stendhal.server.maps.quests;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class VampireSwordTest {
	private static String quest_slot;
	private static VampireSword vs;
		
	private static final String DWARF_NPC = "Hogart";
	private static final String VAMPIRE_NPC = "Markovich";
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		
		SingletonRepository.getNPCList().add(new SpeakerNPC(DWARF_NPC));
		SingletonRepository.getNPCList().add(new SpeakerNPC(VAMPIRE_NPC));
		
		vs = new VampireSword();
		vs.addToWorld();
		
		quest_slot = vs.getSlotName();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		SingletonRepository.getNPCList().clear();
	}
	
	// **** Early quest tests ****
	@Test 
	public void requestQuest()  {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			
			assertFalse(player.hasQuest(quest_slot));
			en.setCurrentState(ConversationStates.ATTENDING);
			
			en.step(player, request);
			assertEquals(request, "I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.QUEST_OFFERED);
		}
	}
	
	@Test
	public void requestAgainAfterDone() {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
		
			player.setQuest(quest_slot, "done");
			en.setCurrentState(ConversationStates.ATTENDING);
			
			en.step(player, request);
			assertEquals(request, "What are you bothering me for now? You've got your sword, go and use it!", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}
	
	@Test public void requestWhileQuestActive() {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
		
			// getting to this state should not be possible while the quest is active,
			// but there's a response for it in the quest. test for a sane answer in
			// case the implementation changes
			for (String state : Arrays.asList("start", "forging")) {
				player.setQuest(quest_slot, state);
				en.setCurrentState(ConversationStates.ATTENDING);
			
				en.step(player, request);
				assertEquals(request, "Why are you bothering me when you haven't completed your quest yet?", npc.getText());
				assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
			}
		}
	}
		
	@Test
	public void rejectQuest() {
		final Player player = PlayerTestHelper.createPlayer("me");			
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
		final double karma = player.getKarma();
		
		assertFalse(player.hasQuest(quest_slot));
		en.setCurrentState(ConversationStates.QUEST_OFFERED);
			
		en.step(player, "no");
		assertEquals("Refusing", "Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.", npc.getText());
		assertEquals("karma penalty", karma - 5.0, player.getKarma(), 0.01);
		assertFalse(player.isEquipped("empty goblet"));
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
	}
	
	@Test
	public void acceptQuest() {
		for (String answer : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			final double karma = player.getKarma();
			
			assertFalse(player.hasQuest(quest_slot));
			en.setCurrentState(ConversationStates.QUEST_OFFERED);
			
			en.step(player, answer);
			assertEquals("Then you need this #goblet. Take it to the Semos #Catacombs.", npc.getText());
			assertEquals("karma bonus", karma + 5.0, player.getKarma(), 0.01);
			assertTrue("Player is given a goblet", player.isEquipped("empty goblet"));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}
	
	@Test
	public void explanations() {
		final Player player = PlayerTestHelper.createPlayer("me");			
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
		
		en.setCurrentState(ConversationStates.ATTENDING);
		en.step(player, "catacombs");
		assertEquals("answer to 'catacombs'", "The Catacombs of north Semos of the ancient #stories.", npc.getText());
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		
		en.step(player, "goblet");
		assertEquals("answer to 'goblet'", "Go fill it with the blood of the enemies you meet in the #Catacombs.", npc.getText());
	}
	
	@Test 
	public void greetWithGoblet() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			
			assertFalse(player.hasQuest(quest_slot));
			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(quest_slot, "start");
			
			final Item goblet = SingletonRepository.getEntityManager().getItem("empty goblet");
			player.equip(goblet, true);
			
			en.step(player, hello);
			assertEquals(hello, "Did you lose your way? The Catacombs are in North Semos. Don't come back without a full goblet! Bye!", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		}
	}
	
	@Test
	public void greetWithLostGoblet() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			
			assertFalse(player.hasQuest(quest_slot));
			assertFalse(player.isEquipped("empty goblet"));
			assertFalse(player.isEquipped("goblet"));
			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(quest_slot, "start");
			
			en.step(player, hello);
			assertEquals(hello, "I hope you didn't lose your goblet! Do you need another?", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.QUESTION_1);
		}
	}
	
	
	@Test
	public void getAnotherGoblet() {
		for (String answer : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			
			assertFalse(player.hasQuest(quest_slot));
			assertFalse(player.isEquipped("empty goblet"));
			assertFalse(player.isEquipped("goblet"));
			en.setCurrentState(ConversationStates.QUESTION_1);
			player.setQuest(quest_slot, "start");
			
			en.step(player, answer);
			assertEquals(answer, "You stupid ..... Be more careful next time. Bye!", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
			assertTrue("Player is given a goblet", player.isEquipped("empty goblet"));
		}
	}
	
	@Test
	public void refuseAnotherGoblet() {
		final Player player = PlayerTestHelper.createPlayer("me");			
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			
		assertFalse(player.hasQuest(quest_slot));
		assertFalse(player.isEquipped("empty goblet"));
		assertFalse(player.isEquipped("goblet"));
		en.setCurrentState(ConversationStates.QUESTION_1);
		player.setQuest(quest_slot, "start");
			
		en.step(player, "no");
		assertEquals("Then why are you back here? Go slay some vampires! Bye!", npc.getText());
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		assertFalse("Player is not given a goblet", player.isEquipped("empty goblet"));
	}
	
	// **** Goblet filling tests ****
	@Test
	public void sayHelloToVampire() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");			
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();
			
			en.step(player, hello);
			assertEquals("vampires greeting", "Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty goblet I will #fill it with blood for you in my cauldron.", npc.getText());
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}
}
