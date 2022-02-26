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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pre-processes custom HTML tags, such as {@code &lt;tally/&gt;}.
 */
public class HtmlPreprocessor {

	private static final Pattern TALLY_PATTERN = Pattern.compile("(.*)\\<tally\\>([0-9]+)\\</tally\\>(.*)");

	public String preprocess(String input) {
		return handleTallyTag(input);
	}

	private String handleTallyTag(String input) {
		Matcher matcher = TALLY_PATTERN.matcher(input);
		if (matcher.matches()) {
			String prefix = matcher.group(1);
			String number = matcher.group(2);
			String suffix = matcher.group(3);
			int numberValue = Integer.parseInt(number);
			return prefix + "<span style=\"font-family: 'Tally'\">" + new TallyMarks(numberValue) + "</span>" + suffix;
		} else {
			return input;
		}
	}
}
