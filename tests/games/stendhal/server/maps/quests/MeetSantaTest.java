/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;

import org.junit.Test;

/**
 * Tests for MeetSanta
 *
 * @author hendrik
 */
public class MeetSantaTest {

	/**
	 * Tests for isChristmasTime
	 */
	@Test
	public void testIsChristmasTime() {
		assertTrue(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 0, 1)));
		assertTrue(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 0, 5)));
		assertTrue(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 0, 6)));
		assertFalse(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 0, 7)));
		assertFalse(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 1, 1)));
		assertFalse(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 1, 7)));
		assertFalse(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 10, 22)));
		assertTrue(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 10, 23)));
		assertTrue(MeetSanta.isChristmasTime(new GregorianCalendar(2013, 10, 24)));
	}

}
