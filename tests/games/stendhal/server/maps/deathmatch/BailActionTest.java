package games.stendhal.server.maps.deathmatch;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.AdosDeathmatch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BailActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		PlayerTestHelper.generateNPCRPClasses();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		NPCList.get().clear();
	}

	/**
	 * Tests for bailNoDM.
	 */
	@Test
	public void testBailNoDM() {
		final AdosDeathmatch adm = new AdosDeathmatch(new StendhalRPZone("zone"), null);
		adm.createNPC("th", 0, 0);
		final SpeakerNPC th = NPCList.get().get("th");
		assertNotNull(th);
		final Engine en = th.getEngine();
		final Player player = PlayerTestHelper.createPlayer("bob");
		en.setCurrentState(ConversationStates.ATTENDING);
		en.step(player, "bail");
		assertEquals("Coward, you haven't even #started!", getReply(th));
		th.put("text", "");
	}

	/**
	 * Tests for bailDoneDM.
	 */
	@Test
	public void testBailDoneDM() {
		final AdosDeathmatch adm = new AdosDeathmatch(new StendhalRPZone("zone"), null);
		adm.createNPC("th", 0, 0);
		final SpeakerNPC th = NPCList.get().get("th");
		assertNotNull(th);
		final Engine en = th.getEngine();
		final Player player = PlayerTestHelper.createPlayer("bob");
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest("deathmatch", "done");
		en.step(player, "bail");
		assertEquals("Coward, we haven't even #started!", getReply(th));
		th.put("text", "");

	}

	/**
	 * Tests for bailStartedDMNOhelmet.
	 */
	@Test
	public void testBailStartedDMNOhelmet() {
		final AdosDeathmatch adm = new AdosDeathmatch(new StendhalRPZone("zone"), null);
		adm.createNPC("th", 0, 0);
		final SpeakerNPC th = NPCList.get().get("th");
		assertNotNull(th);
		final Engine en = th.getEngine();
		final Player player = PlayerTestHelper.createPlayer("bob");
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setQuest("deathmatch", "start");
		en.step(player, "bail");
		assertEquals("Coward! You're not as experienced as you used to be.", getReply(th));
		th.put("text", "");
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() {
		final AdosDeathmatch adm = new AdosDeathmatch(new StendhalRPZone("zone"), null);
		adm.createNPC("th", 0, 0);
		final SpeakerNPC th = NPCList.get().get("th");
		assertNotNull(th);
		final Engine en = th.getEngine();
		final Player player = PlayerTestHelper.createPlayer("bob");
		en.setCurrentState(ConversationStates.ATTENDING);
		final Item helmet = SingletonRepository.getEntityManager().getItem("trophy helmet");
		player.equipToInventoryOnly(helmet);
		assertTrue(player.isEquipped("trophy helmet"));
		helmet.put("def", 2);
		assertThat(helmet.getInt("def"), greaterThan(1));
		player.setQuest("deathmatch", "start");
		en.step(player, "bail");
		assertEquals("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.",
				getReply(th));

	}

}
