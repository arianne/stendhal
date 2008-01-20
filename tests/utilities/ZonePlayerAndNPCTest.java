package utilities;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.List;
import java.util.Vector;

import org.junit.After;

/**
 * NPCTest is the base class for tests dealing with NPCs.
 *
 * @author Martin Fuchs
 */
public abstract class ZonePlayerAndNPCTest extends ZoneAndPlayerTest {

	private List<String> npcNames = new Vector<String>();

	/**
	 * Register NPC names for cleanup in tearDown().
	 * 
	 * @param npcNames
	 */
	protected ZonePlayerAndNPCTest(String zoneName, String... npcNames) {
		super(zoneName);
		
		assertTrue(npcNames.length > 0);

		for(String npcName : npcNames) {
			this.npcNames.add(npcName);
		}
    }

	/**
	 * Reset all involved NPCs.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		for(String npcName : npcNames) {
			resetNPC(npcName);
		}
		
		super.tearDown();
	}

	/**
	 * Return the SpeakerNPC of the given name.
	 * 
	 * @param npcName
	 * @return SpeakerNPC
	 */
	protected SpeakerNPC getNPC(String npcName) {
		SpeakerNPC npc = NPCList.get().get(npcName);

		assertNotNull(npc);

		return npc;
	}

}
