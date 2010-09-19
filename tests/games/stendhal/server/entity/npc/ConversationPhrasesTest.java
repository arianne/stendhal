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
package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests for ConversationPhrases
 *
 * @author hendrik
 */
public class ConversationPhrasesTest {

	/**
	 * test for empty list
	 */
	@Test
	public void testEmpty() {
		assertTrue(ConversationPhrases.EMPTY.size() == 0);
	}
}
