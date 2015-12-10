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
package games.stendhal.server.events;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import marauroa.common.game.RPClass;

public class TextEventTest {

	/**
	 * Tests for textEvent.
	 */
	@Test
	public void testTextEvent() {
		TextEvent event = new TextEvent("text");
		assertThat(event.get("text"), is("text"));
	}

	/**
	 * Tests for generateRPClass.
	 */
	public void testGenerateRPClass() {
		assertFalse(RPClass.hasRPClass("text"));
		TextEvent.generateRPClass();
		assertTrue(RPClass.hasRPClass("text"));
	}



}
