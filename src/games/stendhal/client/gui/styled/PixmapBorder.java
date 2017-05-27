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
package games.stendhal.client.gui.styled;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import games.stendhal.client.sprite.Sprite;

/**
 * A <code>Border</code> that draws raised or lowered borders
 * based on a template image.
 */
public class PixmapBorder implements Border {
	/** Drawing width of the borders */
	private static final int WIDTH = 2;
	private static final int NARROW_WIDTH = 1;

	/** Insets for most components using these borders */
	private static final Insets insets = new Insets(WIDTH, WIDTH, WIDTH, WIDTH);
	/**
	 * Insets for components that should have narrow borders
	 * <em>and</em> no padding.
	 */
	private static final Insets smallInsets = new Insets(NARROW_WIDTH,
		NARROW_WIDTH, NARROW_WIDTH, NARROW_WIDTH);


	/** Image for painting the top and left borders */
	private final Image topLeftImage;
	/** Image for painting the bottom and right borders */
	private final Image bottomRightImage;

	private final int imageWidth, imageHeight;

	/**
	 * Create a new <code>PixmapBorder</code>.
	 *
	 * @param template {@link Sprite} to be used as the base image for drawing
	 * 	the border
	 * @param raised if <code>true</code>, the border will appear raised,
	 * 	otherwise it will look sunken
	 */
	public PixmapBorder(Sprite template, boolean raised) {
		if (raised) {
			bottomRightImage = makeModifiedImage(template, Color.BLACK);
			topLeftImage = makeModifiedImage(template, Color.WHITE);
		} else {
			bottomRightImage = makeModifiedImage(template, Color.WHITE);
			topLeftImage = makeModifiedImage(template, Color.BLACK);
		}
		imageWidth = template.getWidth();
		imageHeight = template.getHeight();
	}

	/**
	 * Create a painted over version if a {@link Sprite} image. The image is
	 * painted over with alpha 0.5.
	 *
	 * @param template {@link Sprite} to used as the original image
	 * @param color painting color
	 * @return modified image
	 */
	private Image makeModifiedImage(Sprite template, Color color) {
		final int width = template.getWidth();
		final int height = template.getHeight();

		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final BufferedImage image = gc.createCompatibleImage(width, height, Transparency.OPAQUE);

		Graphics2D g = image.createGraphics();
		template.draw(g, 0, 0);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();

		return image;
	}

	@Override
	public Insets getBorderInsets(Component component) {
		if (component instanceof JPopupMenu) {
			return smallInsets;
		} else {
			return insets;
		}
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component component, Graphics graphics, int x, int y,
			int width, int height) {
		Rectangle oldClip = graphics.getClipBounds();
		Graphics g = graphics.create();

		int borderWidth = getBorderWidth(component);

		// *** Clipping for  top and left borders ***
		Polygon p = new Polygon();
		p.addPoint(x, y);
		p.addPoint(x + width, y);
		p.addPoint(x + width - borderWidth, y + borderWidth);
		p.addPoint(x + borderWidth, y + borderWidth);
		p.addPoint(x + borderWidth, y + height - borderWidth);
		p.addPoint(x, y + height);
		g.setClip(p);
		g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);

		// top border
		for (int i = x; i < x + width; i += imageWidth) {
			g.drawImage(topLeftImage, i, y, null);
		}
		// left border
		for (int i = y; i < y + height; i += imageHeight) {
			g.drawImage(topLeftImage, x, i, null);
		}

		// *** Clipping for bottom and right borders ***
		// We have the same number of vertices as before, so it's efficient to
		// reuse the polygon
		p.reset();
		p.addPoint(x + width, y);
		p.addPoint(x + width, y + height);
		p.addPoint(x, y + height);
		p.addPoint(x + borderWidth, y + height - borderWidth);
		p.addPoint(x + width - borderWidth, y + height - borderWidth);
		p.addPoint(x + width - borderWidth, y + borderWidth);
		g.setClip(p);
		g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);

		// Bottom border. More than one y coordinate may be needed in case the
		// tile border coincides to be inside the bottom border.
		int startY = y + height - borderWidth - (height - borderWidth) % imageHeight;
		int endY = y + height - height % imageHeight;
		for (int borderY = startY; borderY <= endY; borderY += imageHeight) {
			for (int i = x; i < x + width; i += imageWidth) {
				g.drawImage(bottomRightImage, i, borderY, null);
			}
		}
		// Right border. More than one x coordinate may be needed in case the
		// tile border coincides to be inside the right border.
		int startX = x + width - borderWidth - (width - borderWidth) % imageWidth;
		int endX = x + width - width % imageWidth;
		for (int borderX = startX; borderX <= endX; borderX += imageWidth) {
			for (int i = y; i < y + height; i += imageHeight) {
				g.drawImage(bottomRightImage, borderX, i, null);
			}
		}

		g.dispose();
	}

	/**
	 * Get the appropriate border draw width for a component.
	 *
	 * @param component
	 * @return border width
	 */
	private int getBorderWidth(Component component) {
		if ((component instanceof JMenuItem)
				|| (component instanceof JPopupMenu)) {
			return NARROW_WIDTH;
		} else {
			return WIDTH;
		}
	}
}
