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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class HealedEventTest {

	/**
	 * Tests for healedEvent.
	 */
    @Test
    public void testHealedEvent() {
        HealedEvent event = new HealedEvent(1);
        assertThat(Integer.valueOf(event.getInt("amount")), is(Integer.valueOf(1)));
    }

	/**
	 * Tests for healedEventTryBroken.
	 */
    @Ignore
    @Test
    public void testHealedEventTryBroken() {
        HealedEvent event = new HealedEvent(1);
        event.put("amount", "boo boo");
        assertThat(event.get("amount"), is("boo boo"));
        assertThat(Integer.valueOf(event.getInt("amount")), is(Integer.valueOf(0)));
    }

}
