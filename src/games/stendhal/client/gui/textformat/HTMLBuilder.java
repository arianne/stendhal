/***************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
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

/**
 * Text sink for generating HTML snippets. The used {@link TextAttributeSet}
 * should have HTML tag names as the values of each attribute.
 */
public class HTMLBuilder implements AttributedTextSink<TextAttributeSet> {
	/** Builder for the plain string representation. */
	private final StringBuilder stringBuilder = new StringBuilder();

	@Override
	public void append(String s, TextAttributeSet attrs) {
		for (Object o : attrs.contents().values()) {
			stringBuilder.append('<');
			stringBuilder.append(o);
			stringBuilder.append('>');
		}
		stringBuilder.append(s);
		for (Object o : attrs.contents().values()) {
			stringBuilder.append("</");
			stringBuilder.append(o);
			stringBuilder.append('>');
		}
	}

	/**
	 * Get the generated HTML.
	 *
	 * @return HTML string
	 */
	public String toHTML() {
		return stringBuilder.toString();
	}
}
