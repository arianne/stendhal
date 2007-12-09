package games.stendhal.server.entity.npc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class NPCListTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGet() {
		NPCList npclist = new NPCList() {
		};
		assertSame(npclist, NPCList.get());
	}

	@Test
	public final void testAddHas() {
		PlayerTestHelper.generateNPCRPClasses();
		NPCList npclist = new NPCList() {
		};
		npclist.add(new SpeakerNPC("Bob"));
		assertNotNull(npclist.get("Bob"));
		assertNotNull(npclist.get("BOB"));
	}

	@Test
	public final void testRemove() {
		PlayerTestHelper.generateNPCRPClasses();
		NPCList npclist = new NPCList() {
		};
		npclist.add(new SpeakerNPC("Bob"));
		assertNotNull(npclist.get("Bob"));
		assertNotNull(npclist.remove("Bob"));
		assertNull(npclist.get("Bob"));
		npclist.add(new SpeakerNPC("Bob"));
		assertNotNull(npclist.get("bob"));
		npclist.remove("BOB");
		assertNull(npclist.get("BOB"));
	}

	@Test
	public final void testGetNPCs() {
		PlayerTestHelper.generateNPCRPClasses();
		NPCList npclist = new NPCList() {
		};
		SpeakerNPC speakerNPC = new SpeakerNPC("Bob");
		npclist.add(speakerNPC);
		assertEquals(speakerNPC, npclist.get("Bob"));
		assertEquals(speakerNPC, npclist.get("BOB"));
	}

}
