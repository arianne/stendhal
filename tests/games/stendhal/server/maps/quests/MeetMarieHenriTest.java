package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.library.WriterNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class MeetMarieHenriTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private SpeakerNPC npc;
	private Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	public MeetMarieHenriTest() {
		setNpcNames("Marie-Henri");
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new WriterNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		npc = SingletonRepository.getNPCList().get("Marie-Henri");
		en = npc.getEngine();

		quest = new MeetMarieHenri();
		quest.addToWorld();
	}

	/**
	 * Tests for dialogues of npc.
	 */
	@Test
	public void testHiAndBye() {
		assertNotNull(npc);
		assertTrue(en.step(player, "hi Marie-Henri"));
		assertTrue(npc.isTalking());
		assertEquals("Bonjour!", getReply(npc));
		assertTrue(en.step(player, "bye bye"));
		assertFalse(npc.isTalking());
		assertEquals("Au revoir!", getReply(npc));
	}

	/**
	 * Tests for the quest
	 */
	@Test
	public void testQuest() {
		en.step(player, "hi");
		en.step(player, "task");
		assertEquals("I am currently testing the general knowledge of the adventurers around here. "
				+ "If you are able to tell me the #pseudonym I am using for my novels, I'll reward you. "
				+ "Do you feel smart enough for that?", getReply(npc));
		//TODO: test the rest of the Marie-Henri quest
	}

}
