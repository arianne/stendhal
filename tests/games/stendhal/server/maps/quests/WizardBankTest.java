package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class WizardBankTest extends ZonePlayerAndNPCTestImpl {

	private static final String GRAFINDLE_QUEST_SLOT = "grafindle_gold";
	private static final String ZARA_QUEST_SLOT = "suntan_cream_zara";
	private static final String QUEST_SLOT = "wizard_bank";
	private static final String ZONE_NAME = "int_magic_bank";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);

		final WizardBank wb = new WizardBank();
		wb.addToWorld();
	}

	public WizardBankTest() {
		super(ZONE_NAME, "Javier X");
	}

	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // Test trusted access to the bank.
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				npc.get("text"));

		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				npc.get("text"));

		player.setQuest(ZARA_QUEST_SLOT, "done");
		 // Now we are welcome.
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("Welcome to the Wizard's Bank, player.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));

		 // set quest to "start"
		player.setQuest(QUEST_SLOT, "start");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. You may #leave sooner, if required.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));

		 // set quest to "done"
		player.setQuest(QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "no"));
		assertTrue(npc.isTalking());
		assertEquals("Very well.", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));

		 // Test second chest access
		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "yes"));
		assertTrue(npc.isTalking());
		assertEquals("You do not have enough money!", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", npc.get("text"));

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", npc.get("text"));
		assertTrue(npc.isTalking());

		 // equip the player with enough money to pay the fee
		assertTrue(equipWithMoney(player, 1000));

		assertTrue(en.step(player, "yes"));
		assertTrue(npc.isTalking());
		assertEquals("Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.", npc.get("text"));
	}

	@Test
	public void testReplies() {
		 // A named player name needed to create the name based hash code.
		final Player player = createPlayer("player1");

		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // set requirements to access the bank
		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		player.setQuest(ZARA_QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player1.", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I control access to the bank. My spells ensure people cannot simply come and go as they please. We charge a #fee.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "deal"));
		assertEquals("I would have thought that the offer of these #fiscal services is enough for you.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "fiscal"));
		assertEquals("You do not understand the meaning of the word? You should spend more time in libraries, I hear the one in Ados is excellent. Anyhow, to #enter the bank just ask.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "help me"));
		assertEquals("This bank is suffused with #magic, and as such you may access any vault you own. There will be a #fee to pay for this privilege, as we are not a charity.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "magic"));
		assertEquals("Have you not heard of magic? It is what makes the grass grow here. Perhaps in time your kind will learn how to use this fine art.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "task"));
		assertEquals("To #enter this bank you need only ask.", npc.get("text"));
		assertTrue(npc.isTalking());
	}

	@Test
	public void testDoQuest() {
		 // A named player name needed to create the name based hash code.
		final Player player = createPlayer("player2");
		registerPlayer(player, ZONE_NAME);

		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // set requirements to access the bank
		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		player.setQuest(ZARA_QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player2.", npc.get("text"));
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", npc.get("text"));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "yes"));
		assertEquals("You do not have enough money!", npc.get("text"));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", npc.get("text"));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "no"));
		assertEquals("Very well.", npc.get("text"));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "leave"));
		assertEquals("Thank you for using the Wizard's Bank", npc.get("text"));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		 // equip the player with enough money to pay the fee
		assertTrue(equipWithMoney(player, 1000));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "yes"));
		assertEquals("Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.", npc.get("text"));
		assertTrue(npc.isTalking());
		assertTrue(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hm, I do not understand you. If you wish to #leave, just say", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "no"));
		assertEquals("Hm, I do not understand you. If you wish to #leave, just say", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "fee"));
		assertEquals("As you already know, the fee is 1000 money.", npc.get("text"));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "leave"));
		assertEquals("Thank you for using the Wizard's Bank", npc.get("text"));
		assertTrue(npc.isTalking());

		player.setQuest(QUEST_SLOT, "done");

		assertTrue(en.step(player, "leave"));
		assertEquals("Leave where?", npc.get("text"));
		assertTrue(npc.isTalking());
	}
}
