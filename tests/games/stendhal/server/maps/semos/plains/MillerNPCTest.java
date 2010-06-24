package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

/**
 * Test for MillerNPC: mill grain.
 *
 * @author Martin Fuchs
 */
public class MillerNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_semos_plains_ne";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MillerNPC().configureZone(zone, null);

		setupZone(ZONE_NAME);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public MillerNPCTest() {
		super(ZONE_NAME, "Jenny");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Jenny"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC npc = getNPC("Jenny");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.", getReply(npc));

		assertTrue(en.step(player, "grain"));
		assertEquals("There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.", getReply(npc));

		assertTrue(en.step(player, "help"));
		assertEquals("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.", getReply(npc));

		assertTrue(en.step(player, "mill"));
		assertEquals("I can only mill a sack of flour if you bring me 5 #'sheaves of grain'.", getReply(npc));

		assertTrue(en.step(player, "mill two sacks of flour"));
		assertEquals("I can only mill 2 sacks of flour if you bring me 10 #'sheaves of grain'.", getReply(npc));

		assertTrue(en.step(player, "mill grain"));
		assertEquals("Sorry, I can only produce flour.", getReply(npc));

//TODO mf - complete milling test case
//		assertTrue(equipWithItem(player, "scythe"));
//
//		assertTrue(equipWithItem(player, "chaos legs"));
//		assertTrue(en.step(player, "sell chaos leg"));
//		assertEquals("1 pair of chaos legs is worth 8000. Do you want to sell it?", getReply(npc));
//
//		assertFalse(player.isEquipped("money", 8000));
//		assertTrue(en.step(player, "yes"));
//		assertEquals("Thanks! Here is your money.", getReply(npc));
//		assertTrue(player.isEquipped("money", 8000));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

}
