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
package games.stendhal.server.util;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * Tests the WikipediaAccess class.
 */
public class WikipediaAccessTest {

	private String getWikiText(final String keyword) {
		final WikipediaAccess access = new WikipediaAccess(keyword);
		String response = null;

		access.run();

		if (access.getError() == null) {
			if (access.isFinished()) {
				if ((access.getText() != null) && (access.getText().length() > 0)) {
					response = access.getProcessedText();

					System.out.println("Wikipedia response to " + keyword + ": " + response);
				} else {
					fail("Sorry, could not find information on this topic in Wikipedia.");
				}
			} else {
				fail("Wikipedia query returned without error, but is not yet finished.");
			}
		} else {
			fail("Wikipedia access was not successful: " + access.getError());
		}

		return response;
	}

	/**
	 * Test Wikipedia access.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testStendhal() {
		final String response = getWikiText("Stendhal");

		if (response != null) {
			final Matcher<String> henrimariebeyle = allOf(containsString("Marie"), containsString("Henri"), containsString("Beyle"));
			assertThat("There should be named the french novelist for the topic Stendhal.", response, henrimariebeyle);
		}
	}

	/**
	 * Test redirects: GPL -> GNU General Public License.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGPL() {
		final String response = getWikiText("GPL");

		if (response != null) {
			final Matcher<String> match = allOf(containsString("software license"), containsString("GNU"));
			assertThat("There should be explained the GNU GPL.", response, match);
		}
	}

}
