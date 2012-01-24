package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.library.WriterNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class MeetMarieHenriTest extends ZonePlayerAndNPCTestImpl {
	
	//private static final String QUEST_SLOT = "meet_marie_henri";
	private static final String ZONE_NAME = "testzone";
	
	private static MeetMarieHenri quest;
	private static Player player = null;
	private static SpeakerNPC npc = null;
	private static Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new WriterNPC());

		quest = new MeetMarieHenri();
		quest.addToWorld();
		
	}
	
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		npc = SingletonRepository.getNPCList().get("Marie-Henri");
		en = npc.getEngine();
	}

	public MeetMarieHenriTest() {
		super(ZONE_NAME, "Marie-Henri");
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
