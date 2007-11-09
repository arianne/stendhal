package games.stendhal.server.maps.quests.logic;

import static org.junit.Assert.*;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



import utilities.PlayerHelper;

public class BringListOfItemsQuestLogicTest {

	@BeforeClass
	public static void setupClass() {
		Log4J.init();

	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testBringListOfItemsQuestLogic() {
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(
				new MockBringListOfItemsQuest());
		logic.addToWorld();
	}

	@Test
	public final void testGetListOfStillMissingItems() {
		PlayerHelper.generatePlayerRPClasses();
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(
				new MockBringListOfItemsQuest() {
					@Override
					public List<String> getNeededItems() {

						return new LinkedList<String>();
					}
				});
		assertEquals("emptyList", Arrays.asList(new String[] {}),
				logic.getListOfStillMissingItems(new Player(new RPObject()),
						false));
		BringListOfItemsQuest quest = new MockBringListOfItemsQuest() {
			@Override
			public List<String> getNeededItems() {

				return Arrays.asList(new String[] { "one", "two", "three" });
			}
		};
		logic = new BringListOfItemsQuestLogic(quest);
		assertEquals(Arrays.asList(new String[] { "one", "two", "three" }),
				logic.getListOfStillMissingItems(new Player(new RPObject()),
						false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two", "#three" }),
				logic.getListOfStillMissingItems(new Player(new RPObject()),
						true));

		Player bob = new Player(new RPObject());
		PlayerHelper.addEmptySlots(bob);
		bob.setQuest(quest.getSlotName(), "");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "two", "three" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two", "#three" }),
				logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "one");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "two", "three" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#two", "#three" }),
				logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "two");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "three" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#three" }),
				logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "three");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one", "two" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one", "#two" }),
				logic.getListOfStillMissingItems(bob, true));
		bob.setQuest(quest.getSlotName(), "three;two");
		assertTrue(bob.hasQuest(quest.getSlotName()));
		assertEquals(Arrays.asList(new String[] { "one" }),
				logic.getListOfStillMissingItems(bob, false));
		assertEquals(Arrays.asList(new String[] { "#one" }),
				logic.getListOfStillMissingItems(bob, true));

	}

	@Test
	public final void testWelcomeNewPlayer() {
		PlayerHelper.generateNPCRPClasses();
		PlayerHelper.generatePlayerRPClasses();
		MockBringListOfItemsQuest quest = new MockBringListOfItemsQuest();
		SpeakerNPC npc = new SpeakerNPC("npc");
		quest.setNpc(npc);
		BringListOfItemsQuestLogic logic = new BringListOfItemsQuestLogic(quest);
		logic.welcomeNewPlayer();

		Player player = new Player(new RPObject());
		PlayerHelper.addEmptySlots(player);
		Engine en = npc.getEngine();
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(quest.welcomeBeforeStartingQuest(), npc.get("text"));


	}

	@Test
	public final void testTellAboutQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAcceptQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testRejectQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testListMissingItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testPlayerDoesNotWantToGiveItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testPlayerWantsToGiveItems() {
		fail("Not yet implemented");
	}

	@Test
	public final void testOfferItem() {
		fail("Not yet implemented");
	}

	@Test
	public final void testWelcomeKnownPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public final void testWelcomePlayerAfterQuest() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAddToWorld() {
		fail("Not yet implemented");
	}

	class MockBringListOfItemsQuest implements BringListOfItemsQuest {

		private SpeakerNPC npc;

		public String askForItemsAfterPlayerSaidHeHasItems() {
			// TODO Auto-generated method stub
			return null;
		}

		public String askForMissingItems(List<String> missingItems) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<String> getAdditionalTriggerPhraseForQuest() {
			// TODO Auto-generated method stub
			return null;
		}

		public SpeakerNPC getNPC() {
			if (npc==null){

				PlayerHelper.generateNPCRPClasses();
				npc=new SpeakerNPC("MockBringListOfItemsQuest");
			}
			return npc;
		}

		public List<String> getNeededItems() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSlotName() {

			return "MockBringListOfItemsQuest";
		}

		public String getTriggerPhraseToEnumerateMissingItems() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToItemBrought() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToLastItemBrought() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToOfferOfNotExistingItem(String itemName) {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToOfferOfNotMissingItem() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToOfferOfNotNeededItem() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToPlayerSayingHeHasNoItems(
				List<String> missingItems) {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToQuest() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToQuestAcception() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToQuestAfterItHasAlreadyBeenCompleted() {
			// TODO Auto-generated method stub
			return null;
		}

		public String respondToQuestRefusal() {
			// TODO Auto-generated method stub
			return null;
		}

		public void rewardPlayer(Player player) {
			// TODO Auto-generated method stub

		}

		public boolean shouldWelcomeAfterQuestIsCompleted() {
			// TODO Auto-generated method stub
			return false;
		}

		public String welcomeAfterQuestIsCompleted() {
			// TODO Auto-generated method stub
			return null;
		}

		public String welcomeBeforeStartingQuest() {
			return "welcomeBeforeStartingQuest";
		}

		public String welcomeDuringActiveQuest() {
			// TODO Auto-generated method stub
			return null;
		}

		public void setNpc(SpeakerNPC npc) {
			this.npc = npc;
		}

	}
}
