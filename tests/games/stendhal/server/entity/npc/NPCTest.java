package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.felinashouse.CatSellerNPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CatTestHelper;

/**
 * Test NPC logic.
 *
 * @author Martin Fuchs
 */
public class NPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CatTestHelper.generateRPClasses();
		ZonePlayerAndNPCTestImpl.setUpBeforeClass();

		setupZone(ZONE_NAME, new CatSellerNPC());

		//	TODO: make this tests independent of the rest of implementations
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public NPCTest() throws Exception {
		super(ZONE_NAME, "Felina");
	}

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = getNPC("Felina");
		Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Felina"));
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testLogic() {
		SpeakerNPC npc = getNPC("Felina");
		Engine en = npc.getEngine();

		npc.listenTo(player, "hi");
		assertEquals("Greetings! How may I help you?", npc.get("text"));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", npc.get("text"));

		assertNotNull(npc.getAttending());
		npc.preLogic();
		assertEquals("Bye.", npc.get("text"));
		assertEquals(null, npc.getAttending());
	}

	@Test
	public void testIdea() {
		SpeakerNPC npc = getNPC("Felina");

		assertEquals(null, npc.getIdea());
		npc.setIdea("walk");
		assertEquals("walk", npc.getIdea());

		npc.setIdea(null);
		assertEquals(null, npc.getIdea());
	}

	//TODO NPC.setOutfit() function seems not to be used anywhere, so it could be removed.
	@Test
	public void testOutfit() {
		SpeakerNPC npc = getNPC("Felina");

		assertEquals(null, npc.getIdea());
		npc.setOutfit("suite");
		assertEquals("suite", npc.get("outfit"));
	}

}
