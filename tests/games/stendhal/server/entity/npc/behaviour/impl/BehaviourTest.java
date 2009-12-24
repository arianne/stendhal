package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class BehaviourTest {

	/**
	 * Tests for setAmount.
	 */
	@Test
	public void testSetAmount() {
		Behaviour beh = new Behaviour();
		beh.setAmount(0);
		assertEquals(1, beh.getAmount());
		beh.setAmount(1001);
		assertEquals(1, beh.getAmount());
		beh.setAmount(1000);
		assertEquals(1000, beh.getAmount());
		beh.setAmount(2);
		assertEquals(2, beh.getAmount());
	}

}
