/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * TextPanel.java
 *
 * Created on 22. Oktober 2005, 20:52
 */

package games.stendhal.client.gui.wt.core;

import games.stendhal.common.StringFormatter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * A simple panel with text.
 * 
 * @author matthias
 */
public class WtTextPanel extends WtPanel {

	/** default font size. */
	public static final int DEFAULT_FONT_SIZE = 12;

	/** default color. */
	public static final Color DEFAULT_COLOR = Color.WHITE;

	/** the text to display. */
	private StringFormatter formatter;

	/** the font size. */
	private int fontSize;

	/** the font color. */
	private Color color;

	/** last height of the text in pixels. */
	private int lastHeight;

	/** enable automatic line breaks? */
	private boolean autoLineBreaks;

	/** Creates a new TextPanel. */
	public WtTextPanel(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, "");
	}

	/** Creates a new TextPanel with the given StringFormatter. */
	public WtTextPanel(String name, int x, int y, int width, int height,
			String formatString) {
		super(name, x, y, width, height);
		this.formatter = new StringFormatter(formatString);
		this.fontSize = DEFAULT_FONT_SIZE;
		this.color = DEFAULT_COLOR;
		autoLineBreaks = true;
	}

	/**
	 * returns the estimated height of the text in pixels. The calculation is
	 * based in the text and the current font size
	 */
	public int getLastHeight() {
		return lastHeight;
	}

	/** sets the font size. */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/** sets the color. */
	public void setColor(Color color) {
		this.color = color;
	}

	/** sets the StringFormatter. This will invalidate all values previously set */
	public void setFormat(String format) {
		this.formatter = new StringFormatter(format);
	}

	/** sets the value of a parameter. 
	* @Deprecated use set(String param, String value)
	*/
	@Deprecated
	public void setValue(String param, String value) {
		formatter.set(param, value);
	}

	/** sets the value of a parameter. */
	public void set(String param, int value) {
		formatter.set(param, value);
	}

	/** sets the value of a parameter. */
	public void set(String param, String value) {
		formatter.set(param, value);
	}

	/**
	 * Draw the text contents. This is only called while open and not minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(Graphics2D clientArea) {

		Font font = clientArea.getFont().deriveFont((float) fontSize);
		// set font and color
		clientArea.setFont(font);
		clientArea.setColor(color);

		String text = formatter.toString();

		FontMetrics metrics = (clientArea).getFontMetrics();

		int index;
		int oldIndex = 0;
		int pos = fontSize;
		int lineHeight = (int) (fontSize * 1.2f);

		do {
			String string;
			index = text.indexOf('\n', oldIndex);
			// get next line from input string
			if (index >= 0) {
				string = text.substring(oldIndex, index);
				oldIndex = index + 1;
			} else {
				string = text.substring(oldIndex);
			}

			// now check if the string fits in te window.
			if (autoLineBreaks && (metrics.stringWidth(text) > getWidth())) {
				StringBuilder buf = new StringBuilder();
				int currentWidth = 0;
				for (int i = 0; i < string.length(); i++) {
					char theChar = string.charAt(i);
					int charWidth = metrics.charWidth(theChar);
					// is the current string longer than the width of the panel?
					if (currentWidth + charWidth > getWidth()) {
						// yep, end this line and start in the next one
						clientArea.drawString(buf.toString(), 0, pos);
						pos += lineHeight;
						buf.setLength(0);
						currentWidth = 0;
					}

					// in all lines except the first one skip leading spaces
					if (!((i > 0) && (buf.length() == 0) && (theChar == ' '))) {
						buf.append(theChar);
						currentWidth += charWidth;
					}
				}
				clientArea.drawString(buf.toString(), 0, pos);
			} else {
				clientArea.drawString(string, 0, pos);
			}
			// next line
			pos += lineHeight;
		} while (index >= 0);

		lastHeight = pos;
	}
}
