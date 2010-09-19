/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
