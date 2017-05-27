package games.stendhal.server.entity.npc.action;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import utilities.SpeakerNPCTestHelper;

public class NPCSetDirectionTest {

	@Test
	public void testNPCSetDirection() {
		for (Direction dir : Direction.values()){
			new NPCSetDirection(dir);
		}
	}

	@Test
	public void testFire() {
		for (Direction dir : Direction.values()){
			NPCSetDirection action = new NPCSetDirection(dir);
			SpeakerNPC npc = SpeakerNPCTestHelper.createSpeakerNPC();
			npc.setDirection(dir.oppositeDirection());
			EventRaiser eventraiser = new EventRaiser(npc);
			action.fire(null, null, eventraiser );
			assertSame(dir, npc.getDirection());
		}
	}

}
