package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.orril.river.CampingGirlNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;

public class CampfireTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String CAMPFIRE = "campfire";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();

		setupZone(ZONE_NAME, new CampingGirlNPC());

		Campfire cf = new Campfire();
		cf.addToWorld();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public CampfireTest() {
		super(ZONE_NAME, "Sally");
	}

	@Test
	public void testCanStartQuestNow() throws Exception {
		SpeakerNPC npc = getNPC("Sally");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Could you do me a #favor?", npc.get("text"));
		assertTrue(en.step(player, "bye"));

		player.setQuest(CampfireTest.CAMPFIRE, "start");
		assertTrue(en.step(player, "hi"));
		assertEquals(
				"You're back already? Don't forget that you promised to collect ten pieces of wood for me!",
				npc.get("text"));
		assertTrue(en.step(player, "bye"));

		player.setQuest(CampfireTest.CAMPFIRE, String.valueOf(System.currentTimeMillis()));
		en.step(player, "hi");
		assertEquals(
				"Oh, I still have plenty of wood from the last time you helped me. Thank you for helping!",
				npc.get("text"));
		assertTrue(en.step(player, "bye"));

		long SIXMINUTESAGO = System.currentTimeMillis() - 6 * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
		player.setQuest(CampfireTest.CAMPFIRE, String.valueOf(SIXMINUTESAGO));
		en.step(player, "hi");
		assertEquals("delay is 5 minutes, so 6 minutes should be enough", "Hi! Could you do me a #favor?", npc.get("text"));
		assertTrue(en.step(player, "bye"));
	}

	@Test
	public void testHiAndbye() {
		SpeakerNPC npc = getNPC("Sally");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! Could you do me a #favor?", npc.get("text"));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testDoQuest() {
		SpeakerNPC npc = getNPC("Sally");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! Could you do me a #favor?", npc.get("text"));
		assertTrue(en.step(player, "favor"));

		assertEquals(
				"I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.",
				npc.get("text"));
		assertTrue(en.step(player, "yes"));
		assertEquals(
				"Okay. You can find wood in the forest north of here. Come back when you get ten pieces of wood!",
				npc.get("text"));
		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", npc.get("text"));
		StackableItem wood = new StackableItem("wood", "", "", null);
		wood.setQuantity(10);
		wood.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(wood);
		assertEquals(10, player.getNumberOfEquipped("wood"));
		assertTrue(en.step(player, "hi"));
		assertEquals(
				"Hi again! You've got wood, I see; do you have those 10 pieces of wood I asked about earlier?",
				npc.get("text"));
		assertTrue(en.step(player, "yes"));
		assertEquals(0, player.getNumberOfEquipped("wood"));
		assertTrue("Thank you! Here, take some meat!".equals(npc.get("text"))
				|| "Thank you! Here, take some ham!".equals(npc.get("text")));
		assertTrue((10 == player.getNumberOfEquipped("meat"))
				|| (10 == player.getNumberOfEquipped("ham")));
		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));

	}

	@Test
	public void testIsRepeatable() throws Exception {
		assertTrue(new Campfire().isRepeatable(null));
	}

	@Test
	public void testIsCompleted() {
		assertFalse(new Campfire().isCompleted(player));

		player.setQuest(CAMPFIRE, "start");
		assertFalse(new Campfire().isCompleted(player));

		player.setQuest(CAMPFIRE, "notStart");
		assertTrue(new Campfire().isCompleted(player));
	}

	@Test
	public void testJobAndOffer() {
		SpeakerNPC npc = getNPC("Sally");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertTrue(npc.isTalking());
		assertEquals("Hi! Could you do me a #favor?", npc.get("text"));
		assertTrue(en.step(player, "job"));
		assertEquals("Work? I'm just a little girl! I'm a scout, you know.",
				npc.get("text"));
		assertFalse("no matching state transition", en.step(player, "offers")); 
		assertEquals("Work? I'm just a little girl! I'm a scout, you know.",
				npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals(
				"You can find lots of useful stuff in the forest; wood and mushrooms, for example. But beware, some mushrooms are poisonous!",
				npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

}
