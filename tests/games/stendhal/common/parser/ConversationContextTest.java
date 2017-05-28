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
package games.stendhal.common.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the ConversationContext class.
 *
 * @author Martin Fuchs
 */
public class ConversationContextTest {

	/**
	 * Tests for sentenceMatching.
	 */
	@Test
	public final void testEquality() {
		final ConversationContext ctx1 = new ConversationContext();
		final ConversationContext ctx2 = new ConversationContext();

		assertEquals(ctx1, ctx2);
		assertTrue(ctx1.equals(ctx2));

		ctx1.forMatching = true;
		assertFalse(ctx1.equals(ctx2));
		assertTrue(ctx1.equals(new ConvCtxForMatcher()));
	}

}
