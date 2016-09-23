/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HtmlPreprocessorTest {

	private HtmlPreprocessor processor = new HtmlPreprocessor();

	@Test
	public void testPreprocessTallyOnly() {
		String tallyOutput = processor.preprocess("<tally>12</tally>");
		assertEquals("<span style=\"font-family: 'Tally'\">552</span>", tallyOutput);
	}

	@Test
	public void testPreprocessTallyAndContext() {
		String before = "Killed creatures: ";
		String after = " (twelve)";
		String tallyOutput = processor.preprocess(before + "<tally>12</tally>" + after);
		assertEquals(before + "<span style=\"font-family: 'Tally'\">552</span>" + after, tallyOutput);
	}

	@Test
	public void testPreprocessTallyWithNoTag() {
		String input = "text without tally tag";
		String tallyOutput = processor.preprocess(input);
		assertEquals(input, tallyOutput);
	}
}
