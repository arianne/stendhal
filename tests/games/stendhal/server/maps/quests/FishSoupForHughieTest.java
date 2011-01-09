package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.farmhouse.FarmersWifeNPC;
import games.stendhal.server.maps.ados.farmhouse.MotherNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class FishSoupForHughieTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;
	private AbstractQuest quest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("int_ados_farm_house_1");
		MockStendlRPWorld.get().addRPZone(zone);

		new MotherNPC().configureZone(zone, null);	
		new FarmersWifeNPC().configureZone(zone, null);	

		quest = new FishSoupForHughie();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testGetSlotName() {
		assertEquals(questSlot,"fishsoup_for_hughie");
	}
	
	@Test
	public void testMeetPhilomenaToGetHint() {
		
		npc = SingletonRepository.getNPCList().get("Philomena");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Goeden dag!", getReply(npc));
		en.step(player, "offer");
		assertEquals("I sell butter and milk.", getReply(npc));
		en.step(player, "help");
		assertEquals("I can sell you a bottle of milk or some butter from our dairy cows if you like.", getReply(npc));
		en.step(player, "task");
		assertEquals("If you can write Junit tests then my daughter needs you. Just ask Diogenes how to help the project.", getReply(npc));
		en.step(player, "job");
		assertEquals("My husband runs this farm, and mostly I look after his younger sister and her boy, they are upstairs. If you could check on them that'd be a help, I heard her crying earlier.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Tot ziens.", getReply(npc));
		
	}
	
	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Anastasia");
		en = npc.getEngine();

		assertNull(player.getQuest(questSlot));
		
		en.step(player, "hi");
		assertEquals("Hi, I really could do with a #favor, please.", getReply(npc));
		en.step(player, "favor");
		assertEquals("My poor boy is sick and the potions I give him aren't working! Please could you fetch him some fish soup?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh no, please, he's so sick.", getReply(npc));
		
		assertEquals(player.getQuest(questSlot), "rejected");
		
		en.step(player, "task");
		assertEquals("My poor boy is sick and the potions I give him aren't working! Please could you fetch him some fish soup?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! You can ask Florence Bouillabaisse to make you fish soup. I think she's in Ados market somewhere.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
		assertEquals(player.getQuest(questSlot), "start");
		
		en.step(player, "hi");
		assertEquals("You're back already? Hughie is getting sicker! Don't forget the fish soup for him, please. I promise to reward you.", getReply(npc));
		en.step(player, "task");
		assertEquals("You already promised me to bring me some fish soup for Hughie! Please hurry!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
		PlayerTestHelper.equipWithItem(player, "fish soup");

		en.step(player, "hi");
		assertEquals("Hi, you've got fish soup, I see, is that for Hughie?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh...but my poor boy ... ", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
		// get values to test after reward
		final int xp = player.getXP();
		final double karma  = player.getKarma();
		
		en.step(player, "hi");
		assertEquals("Hi, you've got fish soup, I see, is that for Hughie?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! I will always be in your favour. I will feed it to Hughie when he wakes. Please take these potions, they did nothing for him.", getReply(npc));
		// [17:37] kymara earns 200 experience points.
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
        // test reward
		assertEquals(xp + 200, player.getXP());
		assertThat(player.getKarma(), greaterThan(karma));
		assertTrue(player.isEquipped("potion", 10));
		assertFalse(player.isEquipped("fish soup"));
		assertTrue(quest.isCompleted(player));

		en.step(player, "hi");
		assertEquals("Hello again.", getReply(npc));
		en.step(player, "task");
		assertEquals("Hughie is sleeping off his fever now and I'm hopeful he recovers. Thank you so much.", getReply(npc));
		en.step(player, "offer");
		assertNull(getReply(npc));
		en.step(player, "job");
		assertEquals("My brother runs this farm. I just look after my son here.", getReply(npc));
		en.step(player, "help");
		assertEquals("Philomena can sell you milk and butter.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
	}
		
	@Test
	public void testRepeatingQuest() {
		
		npc = SingletonRepository.getNPCList().get("Anastasia");
		en = npc.getEngine();
		
		player.setQuest(questSlot, "-1");
		
		// [17:37] Admin kymara changed your state of the quest 'fishsoup_for_hughie' from '1294594642173' to '-1'
		// [17:37] Changed the state of quest 'fishsoup_for_hughie' from '1294594642173' to '-1'
		
		en.step(player, "hi");
		assertEquals("Hello again.", getReply(npc));
		en.step(player, "task");
		assertEquals("My Hughie is getting sick again! Please could you bring another bowl of fish soup? It helped last time.", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh no, please, he's so sick.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
		
		en.step(player, "hi");
		assertEquals("Hi, I really could do with a #favor, please.", getReply(npc));
		en.step(player, "task");
		assertEquals("My poor boy is sick and the potions I give him aren't working! Please could you fetch him some fish soup?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! You can ask Florence Bouillabaisse to make you fish soup. I think she's in Ados market somewhere.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye.", getReply(npc));
	}
}
