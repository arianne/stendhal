/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.postman;

import java.util.regex.Matcher;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

/**
 * Tests for PostmanIRC
 *
 * @author hendrik
 */
public class PostmanIRCTest {

	/**
	 * tests the pattern
	 */
	@Test
	public void testWhoisResponseParsing() {
		Matcher m = PostmanIRC.patternWhoisResponse.matcher("postman-bot-TEST hendrik_ hendrik :is logged in as");
		assertTrue("Pattern matches: ", m.find());
		assertThat(m.group(1), equalTo("hendrik_"));
		assertThat(m.group(2), equalTo("hendrik"));

		m = PostmanIRC.patternWhoisResponse.matcher("postman-bot-TEST hendrik_ :End of /WHOIS list.");
		assertTrue("Pattern matches: ", m.find());
		assertThat(m.group(1), equalTo("hendrik_"));
		assertThat(m.group(2), equalTo(""));
	}
}
