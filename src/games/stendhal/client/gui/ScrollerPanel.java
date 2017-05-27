/*
 * "Developed by Infosys 7, FH-Brandenburg, Germany
 *
 * This Software is provided as it is and is published under the GPL-License. No
 * warranty is provided. Building systems and portals based on this system is on
 * your own risk.
 *
 * @author Ken Werner modulname: Client contact: wernerk@fh-brandenburg.de"
 *
 * I asked the author for permission to use it in our project which links against
 * Apache Software License 2.0 code. He responded that he releases this class under
 * the Apache Software License 2.0 aswell.
 *
 * Slightly modified (by Zenix) to be used with the Stendhal MMORPG
 */
package games.stendhal.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * ScrollerPanel can be used for displaying a scrolling text (as in movie
 * credits).
 */
class ScrollerPanel extends JComponent {

	private static final long serialVersionUID = -9047582023793318785L;

	private static final Logger logger = Logger.getLogger(ScrollerPanel.class);

	private final List<String> text;

	private final Font font;

	private int textPos;

	private final Timer t;

	private final int lineSpacing;

	private final Color textColor;

	private final Color backgroundColor;

	private Dimension prefferedSize;

	private int lineHeight;

	private boolean scrollingStarted;

	private GradientPaint gp;

	/**
	 * Creates an ScrollerPane which scrolls the given text and uses the given
	 * attributes.
	 *
	 * @param text
	 *            the text array which should be scrolled - one string per line
	 *            is scrolled
	 * @param font
	 *            the font which is rendered
	 * @param lineSpacing
	 *            the gap between the lines
	 * @param textColor
	 *            color of the text
	 * @param backgroundColor
	 *            the background color of the panel
	 * @param scrollSpeed
	 *            defines the scroller speed (pixel per second);
	 */
	ScrollerPanel(final List<String> text, final Font font, final int lineSpacing,
			final Color textColor, final Color backgroundColor, final int scrollSpeed) {
		super();
		this.text = text;
		this.font = font;
		this.lineSpacing = lineSpacing;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.t = new Timer((int) (1.0 / scrollSpeed * 1000.0),
				new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moveText();
			}
		});
		logger.debug("Created a new scrolling panel");
		calculateSizes();
		// setting up event handling
		eventHandling();
	}

	/**
	 * Sets up the listeners an event handling in general.
	 */
	private void eventHandling() {
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				gp = null;
				if (!t.isRunning()) {
					resetTextPos();
					scrollingStarted = true;
					t.start();
					logger.debug("start scrolling");
				}
			}
		});
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (scrollingStarted) {
			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setBackground(backgroundColor);
			g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
			if (gp == null) {
				gp = new GradientPaint(0f, 0f, backgroundColor, 0f,
					this.getHeight() / 2.f, textColor, true);
			}
			g2d.setPaint(gp);
			g2d.setFont(font);
			final FontMetrics metrics = g2d.getFontMetrics();
			int i = 0;
			for (String s : text) {
				int width = metrics.stringWidth(s);
				int height = metrics.getHeight();
				int startPos = textPos + ((height + lineSpacing) * i);
				// Speed optimizations. Drawing is ridiculously slow even with
				// them.
				if (startPos - height > getHeight()) {
					// This line, and lines after are not yet visible
					break;
				}
				// Otherwise line already scrolled above the top
				if (startPos >= 0) {
					g2d.drawString(s, this.getWidth() / 2 - width / 2, startPos);
				}

				++i;
			}
		}
	}

	/**
	 * Calculates the new position of text.
	 */
	private void moveText() {
		if (textPos >= -((lineHeight + lineSpacing) * text.size())) {
			textPos--;
		} else {
			resetTextPos();
		}
		this.repaint(0, 0, this.getWidth(), this.getHeight());
	}

	/**
	 * Resets the text's position.
	 */
	private void resetTextPos() {
		textPos = this.getHeight() - (lineSpacing + lineHeight) * 2;
	}

	/**
	 * Stops scrolling.
	 */
	void stop() {
		t.stop();
		logger.debug("stop scrolling");
	}

	/**
	 * Calculates the line height and preferred size of this component depending
	 * on the given font.
	 */
	private void calculateSizes() {
		this.prefferedSize = new Dimension();
		final BufferedImage image = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = image.createGraphics();
		g2d.setFont(font);
		final FontMetrics metrics = g2d.getFontMetrics();
		this.lineHeight = metrics.getHeight();
		this.prefferedSize.height = this.lineHeight * 8;
		for (String s : text) {
			prefferedSize.width = Math.max(prefferedSize.width,
					metrics.stringWidth(s));
		}
		this.prefferedSize.width = prefferedSize.width + 6
				* metrics.stringWidth(" ");
	}

	@Override
	public Dimension getPreferredSize() {
		return this.prefferedSize;
	}
}
