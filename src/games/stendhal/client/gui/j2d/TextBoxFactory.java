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
package games.stendhal.client.gui.j2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.TransparencyMode;
import games.stendhal.client.gui.textformat.AttributedStringBuilder;
import games.stendhal.client.gui.textformat.StringFormatter;
import games.stendhal.client.gui.textformat.TextAttributeSet;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;

/**
 * A helper class for painting speech bubbles and other
 * messages used on the screen.
 */
public class TextBoxFactory {
	/** Used for calculating the line metrics. */
	private static final Graphics graphics = (new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)).getGraphics();

	/** space to be left at the beginning and end of line in pixels. */
	private static final int MARGIN_WIDTH = 3;
	/** height of text lines in pixels. */
	private static final int LINE_HEIGHT = graphics.getFontMetrics().getHeight();
	/** Font ascent. */
	private static final int LINE_ASCENT = graphics.getFontMetrics().getAscent();
	/** space needed for the bubble "handle" in pixels. */
	private static final int BUBBLE_OFFSET = 10;
	/** the diameter of the arc of the rounded bubble corners. */
	private static final int ARC_DIAMETER = 2 * MARGIN_WIDTH + 2;
	/**
	 * The maximum number of lines to try to fit in a text box. It is not a
	 * hard limit, but can be exceeded by one in some situations.
	 */
	private static final int MAX_LINES = 6;
	private final StringFormatter<Map<TextAttribute, Object>, TextAttributeSet> formatter;

	/**
	 * Create a new TextBoxFactory.
	 */
	public TextBoxFactory() {
		formatter = new StringFormatter<Map<TextAttribute, Object>, TextAttributeSet>();

		// ** Formatting characters and their effects **
		TextAttributeSet set = new TextAttributeSet();
		set.setAttribute(TextAttribute.FOREGROUND, Color.blue);
		set.setAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		formatter.addStyle('#', set);

		set = new TextAttributeSet();
		set.setAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		formatter.addStyle('§', set);
	}

	/**
	 * Creates a text box sprite.
	 *
	 * @param text the text inside the box
	 * @param width maximum width of the text in the box in pixels
	 * @param textColor color of the text
	 * @param fillColor background color
	 * @param isTalking true if the box should look like a chat bubble
	 *
	 * @return sprite of the text box
	 */
	public Sprite createTextBox(final String text, final int width, final Color textColor,
			final Color fillColor, final boolean isTalking) {
		List<AttributedCharacterIterator> lines = createFormattedLines(text, textColor, width);
		// Find the actual width of the text
		final int lineLengthPixels = getMaxPixelWidth(lines);
		final int numLines = lines.size();

		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final int imageWidth;
		if (lineLengthPixels + BUBBLE_OFFSET < width) {
			imageWidth = (lineLengthPixels + BUBBLE_OFFSET) + ARC_DIAMETER;
		} else {
			imageWidth = width + BUBBLE_OFFSET + ARC_DIAMETER;
		}

		final int imageHeight = LINE_HEIGHT * numLines + 2 * MARGIN_WIDTH;

		final BufferedImage image = gc.createCompatibleImage(imageWidth, imageHeight, TransparencyMode.TRANSPARENCY);

		final Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Background image
		if (fillColor != null) {
			if (isTalking) {
				drawBubble(g2d, fillColor, textColor, imageWidth - BUBBLE_OFFSET, imageHeight);
			} else {
				drawRectangle(g2d, fillColor, textColor, imageWidth - BUBBLE_OFFSET, imageHeight);
			}
		}

		// Text
		drawTextLines(g2d, lines, textColor, MARGIN_WIDTH + BUBBLE_OFFSET, MARGIN_WIDTH);
		g2d.dispose();

		return new ImageSprite(image);
	}

	/**
	 * Create a text box with an image background.
	 *
	 * @param text the text inside the box
	 * @param textColor base color of the text
	 * @param width maximum width of the text in the box in pixels
	 * @param leftMargin margin from the left side of the background image to
	 * 	the drawn text
	 * @param rightMargin margin from the right side of the background image to
	 * 	the drawn text
	 * @param topMargin margin from the top of the background image to the drawn
	 * 	text
	 * @param bottomMargin margin from the bottom of the background image to the
	 * 	drawn text
	 * @param background painter for the background
	 *
	 * @return text box sprite
	 */
	public Sprite createFancyTextBox(final String text, final Color textColor,
			final int width, final int leftMargin, final int rightMargin,
			final int topMargin, final int bottomMargin,
			final BackgroundPainter background) {
		List<AttributedCharacterIterator> lines = createFormattedLines(text, textColor, width);

		int imageWidth = getMaxPixelWidth(lines) + leftMargin + rightMargin;
		final int imageHeight = LINE_HEIGHT * lines.size() + topMargin + bottomMargin;

		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final BufferedImage image = gc.createCompatibleImage(imageWidth, imageHeight, TransparencyMode.TRANSPARENCY);

		final Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background image
		g2d.setComposite(AlphaComposite.Src);
		background.paint(g2d, imageWidth, imageHeight);
		g2d.setComposite(AlphaComposite.SrcOver);

		// Text
		drawTextLines(g2d, lines, textColor, leftMargin, topMargin);
		g2d.dispose();

		return new ImageSprite(image);
	}

	/**
	 * Create formatted lines from a text.
	 *
	 * @param text text to be formatted and grouped to lines
	 * @param textColor base text color
	 * @param width maximum width of the text in pixels
	 *
	 * @return formatted lines
	 */
	private List<AttributedCharacterIterator> createFormattedLines(String text,
			Color textColor, int width) {
		// Format before splitting to get the coloring right
		AttributedString formattedString = formatLine(text.trim(), textColor);
		// split it to max width long pieces
		return splitFormatted(formattedString, width);
	}

	 /**
	  * Draw a chat bubble.
	  *
	  * @param g2d
	  * @param fillColor the bacground color of the bubble
	  * @param outLineColor the color of the bubble outline
	  * @param width width of the bubble body
	  * @param height height of the bubble
	  */
	private void drawBubble(final Graphics2D g2d, final Color fillColor,
			final Color outLineColor, final int width, final int height) {
		/*
		 * There's an one pixel difference in how sun java and openjdk
		 * do drawRoundRect, so we use fillRoundRect for both the
		 * outline and the fill to have pretty bubbles on both
		 */
		g2d.setColor(outLineColor);
		g2d.fillRoundRect(BUBBLE_OFFSET, 0, width, height, ARC_DIAMETER, ARC_DIAMETER);
		g2d.setColor(fillColor);
		g2d.fillRoundRect(BUBBLE_OFFSET + 1, 1, width - 2, height - 2, ARC_DIAMETER, ARC_DIAMETER);

		// The bubble handle
		final Polygon p = new Polygon();
		p.addPoint(BUBBLE_OFFSET + 1, MARGIN_WIDTH + 1);
		p.addPoint(0, LINE_HEIGHT);
		p.addPoint(BUBBLE_OFFSET + 1, LINE_HEIGHT / 2 + MARGIN_WIDTH);
		g2d.fillPolygon(p);

		g2d.setColor(outLineColor);
		g2d.drawLine(0, LINE_HEIGHT, BUBBLE_OFFSET, MARGIN_WIDTH + 1);
		g2d.drawLine(0, LINE_HEIGHT, BUBBLE_OFFSET, LINE_HEIGHT / 2 + MARGIN_WIDTH);
	}

	/**
	 * Draw an outlined rectangle.
	 *
	 * @param g2d graphics
	 * @param fillColor The background color of the rectangle
	 * @param outLineColor Color of the outline
	 * @param width Pixel width of the drawn rectangle
	 * @param height Pixel height of the drawn rectangle
	 */
	private void drawRectangle(final Graphics2D g2d, final Color fillColor,
			final Color outLineColor, final int width, final int height) {
		// Using filled rectangles to work around a rendering
		// incompatibility in openjdk.
		g2d.setColor(outLineColor);
		g2d.fillRect(BUBBLE_OFFSET, 0, width, height);
		g2d.setColor(fillColor);
		g2d.fillRect(BUBBLE_OFFSET + 1, 1, width - 2, height - 2);
	}

	/**
	 * Color a string according to the formatting characters in it.
	 *
	 * @param line string to be formatted
	 * @param normalColor base color used for the text
	 * @return colored sting
	 */
	private AttributedString formatLine(final String line,
			final Color normalColor) {
		try {
			TextAttributeSet normal = new TextAttributeSet();
			normal.setAttribute(TextAttribute.FOREGROUND, normalColor);

			AttributedStringBuilder builder = new AttributedStringBuilder();
			formatter.format(line, normal, builder);

			return builder.toAttributedString();
		} catch (final Exception e) {
			Logger.getLogger(TextBoxFactory.class).error(e, e);
			return null;
		}
	}

	/**
	 * Splits a text to lines with specified maximum width, preserving the line breaks in the original.
	 *
	 * @param text the text to be split
	 * @param width maximum line length in pixels
	 *
	 * @return list of lines
	 */
	private List<AttributedCharacterIterator> splitFormatted(final AttributedString text, final int width) {
		final List<AttributedCharacterIterator> lines = new LinkedList<AttributedCharacterIterator>();

		final BreakIterator iter = BreakIterator.getLineInstance();
		iter.setText(text.getIterator());

		int previous = iter.first();

		AttributedCharacterIterator best = null;

		while (iter.next() != BreakIterator.DONE) {
			final AttributedCharacterIterator candidate = text.getIterator(null, previous, iter.current());

			if (getPixelWidth(candidate) <= width) {
				// check for line breaks within the provided text
				// unfortunately, the BreakIterators are too dumb to tell *why* they consider the
				// location a break, so the check needs to be implemented here
				final CharacterIterator cit = iter.getText();
				if (isHardLineBreak(cit)) {
					lines.add(candidate);
					previous = iter.current();
					best = null;
				} else {
					best = candidate;
				}
			} else {
				if (best == null) {
					// could not break the line - the word's simply too long. Use more force to
					// to fit it to the width
					best = splitAggressively(candidate, width);
					// splitAggressively returns an iterator with its own indexing,
					// so instead of using it directly we need to adjust the old one
					previous += best.getEndIndex() - best.getBeginIndex();
				} else {
					previous = best.getEndIndex();
					// Trim the trailing white space
					char endChar = best.last();

					int endIndex = previous;
					while (Character.isWhitespace(endChar) && endChar != CharacterIterator.DONE) {
						endIndex = best.getIndex();
						endChar = best.previous();
					}

					best = text.getIterator(null, best.getBeginIndex(), endIndex);
				}

				lines.add(best);

				// a special check for a hard line break just after the word
				// that got moved to the next line
				final CharacterIterator cit = iter.getText();
				if (isHardLineBreak(cit)) {
					lines.add(text.getIterator(null, previous, iter.current()));
					previous = iter.current();
				}

				// Pick the shortest candidate possible (backtrack a bit, if needed)
				if (iter.current() > previous + 1) {
					iter.previous();
				}

				best = null;

				if (lines.size() > MAX_LINES) {
					/*
					 * Limit the height of the text boxes. Append ellipsis
					 * to tell the user to take a look at the chat log.
					 * The last line is removed twice to avoid the situation
					 * where the last text line would fit on the space the
					 * ellipsis occupies.
					 */
					lines.remove(lines.size() - 1);
					lines.remove(lines.size() - 1);
					lines.add(new AttributedString("...").getIterator());
					return lines;
				}
			}
		}

		// add the rest of the text, if there's any
		if (previous < iter.last()) {
			lines.add(text.getIterator(null, previous, iter.last()));
		}

		return lines;
	}

	/**
	 * Try splitting a line considering anything that looks like a word break a
	 * valid line break point.
	 * (should we break just anywhere if even that fails? now we just return
	 * the whole line)
	 *
	 * @param text iterator to the line that should be split
	 * @param width the maximum allowed pixel width
	 *
	 * @return iterator to the part of the line that fits in width
	 */
	private AttributedCharacterIterator splitAggressively(final AttributedCharacterIterator text, final int width) {
		final int offset = text.getBeginIndex();
		final BreakIterator wordIterator = BreakIterator.getWordInstance();

		final AttributedString tmpText = new AttributedString(text);
		// return the original iterator if there are no suitable break points
		AttributedCharacterIterator best = text;
		wordIterator.setText(text);

		while (wordIterator.next() != BreakIterator.DONE) {
			final AttributedCharacterIterator candidate = tmpText.getIterator(null, tmpText.getIterator().getBeginIndex(), wordIterator.current() - offset);

			if (getPixelWidth(candidate) <= width) {
				best = candidate;
			} else {
				return best;
			}
		}

		// should never be reached, but java is trying to be too smart and does
		// not allow throwing exceptions here
		return best;
	}

	/**
	 * Get the longest pixel width of a list of lines.
	 *
	 * @param lines lines to be checked
	 * @return the longest pixel width of the checked lines
	 */
	private int getMaxPixelWidth(final List<AttributedCharacterIterator> lines) {
		int pixelWidth = 0;

		for (final AttributedCharacterIterator line : lines) {
			final int width = getPixelWidth(line);
			if (width > pixelWidth) {
				pixelWidth = width;
			}
		}

		return pixelWidth;
	}

	/**
	 * Get the pixel width of a text line.
	 *
	 * @param iter iterator representing the text line
	 * @return pixel width of the line
	 */
	private int getPixelWidth(final AttributedCharacterIterator iter) {
		return (int) graphics.getFontMetrics().getStringBounds(iter, iter.getBeginIndex(), iter.getEndIndex(), graphics).getWidth();
	}

	/**
	 * Draw a list of text lines.
	 *
	 * @param g2d graphics where the text should be drawn
	 * @param lines the text lines to be drawn
	 * @param textColor the base color of the text
	 * @param leftMargin left side margin
	 * @param topMargin top margin
	 */
	private void drawTextLines(final Graphics2D g2d,
			final List<AttributedCharacterIterator> lines, final Color textColor,
			int leftMargin, int topMargin) {
		int y = topMargin + LINE_ASCENT;
		for (final AttributedCharacterIterator line : lines) {
			if (textColor == null) {
				g2d.setColor(Color.black);
			}
			g2d.setColor(textColor);

			g2d.drawString(line, leftMargin, y);
			y += LINE_HEIGHT;
		}
	}

	/**
	 * Check if a location is at a hard line break.
	 *
	 * @param cit iterator
	 * @return <code>true</code> if there is a hard line break
	 */
	private boolean isHardLineBreak(final CharacterIterator cit) {
		// save the location while we are checking the preceding characters
		final int currentIndex = cit.getIndex();

		char currentChar = cit.previous();
		while (currentChar != CharacterIterator.DONE && !Character.isLetterOrDigit(currentChar)) {
			if (currentChar == '\n') {
				cit.setIndex(currentIndex);
				return true;
			}
			currentChar = cit.previous();
		}
		cit.setIndex(currentIndex);

		return false;
	}
}
