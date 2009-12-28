package games.stendhal.client.gui.j2d;

import games.stendhal.client.FormatTextParserExtension;
import games.stendhal.client.gui.FormatTextParser;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class TextBoxFactory {
	private Graphics2D graphics;
	
	/** space to be left at the beginning and end of line in pixels. */
	private static final int MARGIN_WIDTH = 3;
	/** height of text lines in pixels. */
	private static final int LINE_HEIGHT = 16;
	/** space needed for the bubble "handle" in pixels. */
	private static final int BUBBLE_OFFSET = 10;
	/** the diameter of the arc of the rounded bubble corners. */
	private static final int ARC_DIAMETER = 2 * MARGIN_WIDTH + 2;
	
	public TextBoxFactory(final Graphics2D graphics) {
		this.graphics = graphics;
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

		// Format before splitting to get the coloring right
		final AttributedString formattedString = formatLine(text.trim(), graphics.getFont(), textColor);
		// split it to max width long pieces
		final List<AttributedCharacterIterator> formattedLines = splitFormatted(formattedString, width);
		
		// Find the actual width of the text
		final int lineLengthPixels = getMaxPixelWidth(formattedLines);
		final int numLines = formattedLines.size();

		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final int imageWidth;
		if (lineLengthPixels + BUBBLE_OFFSET < width) {
			imageWidth = (lineLengthPixels + BUBBLE_OFFSET) + ARC_DIAMETER;
		} else {
			imageWidth = width + BUBBLE_OFFSET + ARC_DIAMETER;
		}
		
		final int imageHeight = LINE_HEIGHT * numLines + MARGIN_WIDTH;

		final Image image = gc.createCompatibleImage(imageWidth, imageHeight, Transparency.BITMASK);

		final Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (fillColor != null) {
			if (isTalking) {
				drawBubble(g2d, fillColor, textColor, imageWidth - BUBBLE_OFFSET, imageHeight);
			} else {
				drawRectangle(g2d, fillColor, textColor, imageWidth - BUBBLE_OFFSET, imageHeight);
			}
		}

		drawTextLines(g2d, formattedLines, textColor);

		return new ImageSprite(image);
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
		g2d.setColor(fillColor);
		g2d.fillRoundRect(BUBBLE_OFFSET, 0, width, height, ARC_DIAMETER, ARC_DIAMETER);
		
		g2d.setColor(outLineColor);
		// The definition of the arcs for fillRoundRect() and drawRoundRect() are the
		// same according to java docs, but apparently they are drawn differently anyway.
		// The widhts and heights are different by 1 (documented).
		g2d.drawRoundRect(BUBBLE_OFFSET, 0, width - 1, height - 1, ARC_DIAMETER - 2, ARC_DIAMETER - 2);
		
		// The bubble handle
		g2d.setColor(fillColor);
		final Polygon p = new Polygon();
		p.addPoint(BUBBLE_OFFSET + 1, MARGIN_WIDTH + 1);
		p.addPoint(0, LINE_HEIGHT);
		p.addPoint(BUBBLE_OFFSET + 1, LINE_HEIGHT / 2 + MARGIN_WIDTH);
		g2d.fillPolygon(p);
		
		g2d.setColor(outLineColor);
		g2d.drawLine(0, LINE_HEIGHT, BUBBLE_OFFSET, MARGIN_WIDTH);
		g2d.drawLine(0, LINE_HEIGHT, BUBBLE_OFFSET, LINE_HEIGHT / 2 + MARGIN_WIDTH);
	}
	
	private void drawRectangle(final Graphics2D g2d, final Color fillColor, 
			final Color outLineColor, final int width, final int height) {
		g2d.setColor(fillColor);
		g2d.fillRect(BUBBLE_OFFSET, 0, width, height);
		g2d.setColor(outLineColor);
		g2d.drawRect(BUBBLE_OFFSET, 0, width - 1, height - 1);
	}

	public AttributedString formatLine(final String line,
				final Font fontNormal, final Color colorNormal) {
		final Font specialFont = fontNormal.deriveFont(Font.ITALIC);

		try {
			// recreate the string without the # characters
			final StringBuilder temp = new StringBuilder();
			FormatTextParser parser = new FormatTextParserExtension(temp);
			parser.format(line);

			// create the attribute string including formating
			final AttributedString aStyledText = new AttributedString(temp.toString());

			parser = new FormatTextParser() {
				private int s = 0;

				@Override
				public void normalText(final String tok) {
					if (tok.length() > 0) {
						aStyledText.addAttribute(TextAttribute.FONT, fontNormal, s, s
								+ tok.length());
						aStyledText.addAttribute(TextAttribute.FOREGROUND, colorNormal, s, s
								+ tok.length());
						s += tok.length();
					}
				}

				@Override
				public void colorText(final String tok) {
					if (tok.length() > 0) {
						aStyledText.addAttribute(TextAttribute.FONT, specialFont, s, s
								+ tok.length());
						aStyledText.addAttribute(TextAttribute.FOREGROUND, Color.blue, s, s
								+ tok.length());
						s += tok.length();
					}
				}
			};
			parser.format(line);

			return aStyledText;
		} catch (final Exception e) {
			Logger.getLogger(TextBoxFactory.class).error(e, e);
			return null;
		}
	}
	
	/**
	 * splits a text to lines with specified maximum width, preserving the line breaks in the original.
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
					previous = best.getEndIndex();
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
				// Pick the shortest candidate possible (backtrack a bit, if needed)
				if (iter.current() > previous + 1) {
					iter.previous();
				}

				// a special check for a hard line break just after the word 
				// that got moved to the next line
				final CharacterIterator cit = iter.getText();
				if (isHardLineBreak(cit)) {
					lines.add(text.getIterator(null, previous, iter.current()));
					previous = iter.current();
				}

				best = null;

				if (lines.size() > 100) {
					// TODO: fix this bug which can create an empty loop
					break;
				}
			}
		}

		// add the rest of the text, if there's any
		if (previous != iter.last()) {
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
	
	private int getPixelWidth(final AttributedCharacterIterator iter) {
		return (int) graphics.getFontMetrics().getStringBounds(iter, iter.getBeginIndex(), iter.getEndIndex(), graphics).getWidth();
	}

	private void drawTextLines(final Graphics2D g2d, final List<AttributedCharacterIterator> lines, final Color textColor) {
		int i = 0;
		for (final AttributedCharacterIterator line : lines) {
			if (textColor == null) {
				g2d.setColor(Color.black);
			}
			g2d.setColor(textColor);

			g2d.drawString(line, BUBBLE_OFFSET + MARGIN_WIDTH, MARGIN_WIDTH + i * LINE_HEIGHT + 10);
			i++;
		}
	}
	
	private boolean isHardLineBreak(final CharacterIterator cit) {
		// save the location while we are checking the preceeding characters
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
