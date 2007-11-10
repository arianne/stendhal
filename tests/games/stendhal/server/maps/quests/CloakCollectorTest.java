package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerHelper;

public class CloakCollectorTest {
@BeforeClass
 static public void  setupclass(){
	PlayerHelper.generateNPCRPClasses();
	PlayerHelper.generatePlayerRPClasses();
	Log4J.init();
}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
		NPCList.get().remove("Josephine");
	}

	@Test
	public final void testAddToWorld() {




	}

	@Test

	public final void rejectQuest() {
		NPCList.get().add(new SpeakerNPC("Josephine"));
		CloakCollector cc = new CloakCollector();
		cc.addToWorld();
		SpeakerNPC npc = cc.getNPC();
		Engine en = npc.getEngine();
		Player monica = new Player(new RPObject());
		PlayerHelper.addEmptySlots(monica);

		en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(cc.welcomeBeforeStartingQuest(),npc.get("text"));
		en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
		assertEquals(cc.respondToQuest(),npc.get("text"));
		en.stepTest(monica, cc.getTriggerPhraseToEnumerateMissingItems().get(0));

		assertEquals(cc.askForMissingItems(cc.getNeededItems()),npc.get("text"));
		en.stepTest(monica, ConversationPhrases.NO_MESSAGES.get(0));
		assertEquals(cc.respondToQuestRefusal(),npc.get("text"));

	}
		@Test
		public final void doQuest() {
			NPCList.get().add(new SpeakerNPC("Josephine"));
			CloakCollector cc = new CloakCollector();
			cc.addToWorld();
			cc.init("CloaksCollector");
			SpeakerNPC npc = cc.getNPC();
			Engine en = npc.getEngine();
			Player monica = new Player(new RPObject());
			PlayerHelper.addEmptySlots(monica);

			en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
			assertEquals(cc.welcomeBeforeStartingQuest(),npc.get("text"));
			en.stepTest(monica, cc.getAdditionalTriggerPhraseForQuest().get(0));
			assertEquals(cc.respondToQuest(),npc.get("text"));

			en.stepTest(monica, "elf_cloak");
			assertEquals("You haven't seen one before? Well, it's a white_cloak. So, will you find them all?",npc.get("text"));


			en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
			assertEquals(cc.respondToQuestAcception(),npc.get("text"));
			assertFalse(npc.isTalking());
			npc.remove("text");

			assertTrue("the quest was accepted, so it should be started",cc.isStarted(monica));
			assertFalse(cc.isCompleted(monica));


			en.stepTest(monica, ConversationPhrases.GREETING_MESSAGES.get(0));
			assertEquals(cc.welcomeDuringActiveQuest(),npc.get("text"));
			npc.remove("text");
			en.stepTest(monica, ConversationPhrases.YES_MESSAGES.get(0));
			// I would expect : 	[11:19] <Josephine> Great! What cloaks did you bring?
			en.stepTest(monica,"elf_cloak");
			assertEquals(cc.respondToItemBrought(),npc.get("text"));


//		[11:18] <monica> hi
//		[11:18] <Josephine> Hello! Did you bring any cloaks with you?
//		[11:18] <monica> elf_cloak
//		[11:18] <monica> dwarf_cloak
//		[11:18] <monica> stone_cloak
//		[11:18] <monica> hi
//		[11:18] <monica> oge
//		[11:18] <ogetester> hi
//		[11:18] <monica> yes
//		[11:19] <monica> bye
//		[11:19] <Josephine> Bye bye now!
//		[11:19] <monica> hi
//		[11:19] <Josephine> Hello! Did you bring any cloaks with you?
//		[11:19] <monica> yes
//		[11:19] <monica> cloaks
//		[11:19] <Josephine> I want 10 cloaks. That's cloak, elf_cloak, dwarf_cloak, blue_elf_cloak, stone_cloak, green_dragon_cloak, bone_dragon_cloak, lich_cloak, vampire_cloak, and blue_dragon_cloak. Did you bring any?
//		[11:19] <monica> yes
//		[11:19] <Josephine> Great! What cloaks did you bring?
//		[11:19] <monica> elf_cloak
//		[11:19] <Josephine> Wow, thank you! What else did you bring?
//		[11:19] <monica> dwarf_cloak
//		[11:19] <Josephine> Wow, thank you! What else did you bring?
//		[11:20] <Josephine> Bye bye now!
//		[11:20] <monica> hi
//		[11:20] <Josephine> Hello! Did you bring any cloaks with you?
//		[11:20] <monica> yes
//		[11:20] <monica> cloaks
//		[11:20] <Josephine> I want 8 cloaks. That's cloak, blue_elf_cloak, stone_cloak, green_dragon_cloak, bone_dragon_cloak, lich_cloak, vampire_cloak, and blue_dragon_cloak. Did you bring any?
//		[11:20] <monica> yes
//		[11:20] <Josephine> Great! What cloaks did you bring?
//		[11:20] <monica> blue_elf_cloak
//		[11:20] <Josephine> Wow, thank you! What else did you bring?
//		[11:20] <monica> stone_cloak
//		[11:20] <Josephine> Wow, thank you! What else did you bring?
//		[11:20] <monica> black_cloak
//		[11:21] <Josephine> Bye bye now!
//		[11:22] <monica> hi
//		[11:22] <Josephine> Hello! Did you bring any cloaks with you?
//		[11:23] <monica> yes
//		[11:23] <monica> cloaks
//		[11:23] <Josephine> I want 6 cloaks. That's cloak, green_dragon_cloak, bone_dragon_cloak, lich_cloak, vampire_cloak, and blue_dragon_cloak. Did you bring any?
//		[11:23] <monica> green_dragon_cloak
//		[11:23] <Josephine> Wow, thank you! What else did you bring?
//		[11:23] <monica> bone_dragon_cloak
//		[11:23] <Josephine> Wow, thank you! What else did you bring?
//		[11:23] <monica> vampire_cloak
//		[11:23] <Josephine> Wow, thank you! What else did you bring?
//		[11:23] <monica> blue_dragon_cloak
//		[11:23] <Josephine> Wow, thank you! What else did you bring?
//		[11:23] <monica> lich_cloak
//		[11:24] <monica> lich_cloak
//		[11:24] <Josephine> Wow, thank you! What else did you bring?
//		[11:24] <monica> cloaks
//		[11:24] <Josephine> That's not a real cloak...
//		[11:24] <monica> ice_sword
//		[11:24] <monica> bye
//		[11:24] <Josephine> Bye bye now!
//		[11:24] <monica> hi
//		[11:24] <Josephine> Hello! Did you bring any cloaks with you?
//		[11:24] <monica> cloaks
//		[11:24] <Josephine> I want 1 cloak. That's cloak. Did you bring any?
//		[11:24] <monica> cloak
//		[11:24] monica earns 2500 experience points.
//		[11:24] <Josephine> Oh, they look so beautiful all together, thank you. Please take this black cloak in return, I don't like the colour.
//		[11:25] <monica> bye
//		[11:25] <Josephine> Bye bye now!
//		[11:25] <monica> hi
//		[11:25] <Josephine> Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my collection isn't complete...
//		[11:25] <monica> collection
//		[11:25] <Josephine> It's missing 8 cloaks. That's red_cloak, shadow_cloak, xeno_cloak, elvish_cloak, chaos_cloak, mainio_cloak, golden_cloak, and black_dragon_cloak. Will you find them?
//		[11:26] <Josephine> Bye bye now!
//		[11:26] You put a valuable item on the ground. Please note that it will expire in 10 minutes, as all items do. But in this case there is no way to restore it.
//		[11:26] You put a valuable item on the ground. Please note that it will expire in 10 minutes, as all items do. But in this case there is no way to restore it.
//		[11:26] <monica> hi
//		[11:26] <Josephine> Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my collection isn't complete...
//		[11:26] <monica> collection
//		[11:26] <Josephine> It's missing 8 cloaks. That's red_cloak, shadow_cloak, xeno_cloak, elvish_cloak, chaos_cloak, mainio_cloak, golden_cloak, and black_dragon_cloak. Will you find them?
//		[11:27] <monica> red_cloak
//		[11:27] <Josephine> You haven't seen one before? Well, it's a red_spotted_cloak. Sorry if that's not much help, it's all I know! So, will you find them all?
//		[11:27] <monica> shadow_cloak
//		[11:27] <Josephine> You haven't seen one before? Well, it's a shadow_cloak. Sorry if that's not much help, it's all I know! So, will you find them all?
//		[11:27] <monica> xeno_cloak
//		[11:27] <Josephine> You haven't seen one before? Well, it's a xeno_cloak. Sorry if that's not much help, it's all I know! So, will you find them all?
//		[11:27] <monica> elvish_cloak
//		[11:27] <Josephine> You haven't seen one before? Well, it's a elvish_cloak. Sorry if that's not much help, it's all I know! So, will you find them all?
//		[11:27] <monica> crap
//		[11:27] <monica> yes
//		[11:27] <Josephine> Brilliant! I'm all excited again! Bye!
//		[11:27] <monica> elvish_cloak
//		[11:28] <monica> hi
//		[11:28] <Josephine> Welcome back! Have you brought any cloaks with you?
//		[11:28] <monica> yes
//		[11:28] <Josephine> Woo! What cloaks did you bring?
//		[11:28] <monica> elvish_cloak
//		[11:28] <Josephine> Wow, thank you! What else did you bring?
//		[11:28] <monica> shadow_cloak
//		[11:28] <Josephine> Wow, thank you! What else did you bring?
//		[11:28] <monica> red_cloak
//		[11:28] <Josephine> Wow, thank you! What else did you bring?
//		[11:28] <monica> xeno_cloak
//		[11:28] <Josephine> Wow, thank you! What else did you bring?
//		[11:28] <monica> cloaks
//		[11:28] <Josephine> I want 4 cloaks. That's chaos_cloak, mainio_cloak, golden_cloak, and black_dragon_cloak. Did you bring any?
//		[11:28] ogetester tells monica: hi monica, do u know what i have to say to annie so she can have the icecream?
//		[11:29] <Josephine> Bye bye now!
//		[11:29] onSummon: Entity "manio_cloak" not found.
//		[11:30] <monica> hi
//		[11:30] <Josephine> Welcome back! Have you brought any cloaks with you?
//		[11:30] <monica> yes
//		[11:30] <Josephine> Woo! What cloaks did you bring?
//		[11:30] <monica> chaos_cloak
//		[11:30] <Josephine> Wow, thank you! What else did you bring?
//		[11:31] <monica> mainio_cloak
//		[11:31] <Josephine> Wow, thank you! What else did you bring?
//		[11:31] <monica> golden_cloak
//		[11:31] <Josephine> Wow, thank you! What else did you bring?
//		[11:31] <monica> black_dragon_cloak
//		[11:31] monica earns 25000 experience points.
//		[11:31] <Josephine> Oh, yay! My collection is complete, at least for now! You're so kind, I bet you'll have great Karma now!
//		[11:31] <Josephine> Bye bye now!
//		[11:33] 2 Players online: monica(217) ogetester(40)
//		[11:33] ogetester is in 0_kalavan_city_gardens at (86,88)


	}

	@Test
	public final void testGetNPC() {
		fail("Not yet implemented");
	}

	@Test
	public final void testGetNeededItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testGetSlotName() {
		fail("Not yet implemented");
	}

	@Test
	public final void testGetTriggerPhraseToEnumerateMissingItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testShouldWelcomeAfterQuestIsCompleted() {
		fail("Not yet implemented");
	}

	@Test
	public final void testWelcomeBeforeStartingQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testWelcomeDuringActiveQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testWelcomeAfterQuestIsCompleted() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToQuestAcception() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToQuestAfterItHasAlreadyBeenCompleted() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToQuestRefusal() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAskForItemsAfterPlayerSaidHeHasItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAskForMissingItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToItemBrought() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToLastItemBrought() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotExistingItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotMissingItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToOfferOfNotNeededItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRespondToPlayerSayingHeHasNoItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRewardPlayer() {
		fail("Not yet implemented");
	}

}
