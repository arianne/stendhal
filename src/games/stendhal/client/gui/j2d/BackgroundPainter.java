/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import java.awt.Graphics;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * A painter for a background image that consists of 9 tiles, with the
 * center tile being repeated. The rest of the tiles are the corners and borders
 * in natural order. The sides will be painted with preference to adhering to
 * painted area borders rather than trying to preserve the pattern. Therefore
 * the image should be such that it can tolerate miss tiling at the borders, if
 * the painted area dimensions can not be guaranteed to be multiples of the tile
 * dimensions.
 */
public class BackgroundPainter {
	private final Sprite[] images;

	/**
	 * Create a new BackgroundPainter.
	 *
	 * @param image image name. The image dimensions should be multiples of 3.
	 * 	The tiles used for painting will be the image divided uniformly to three
	 *	in both vertical and horizontal directions
	 */
	public BackgroundPainter(String image) {
		SpriteStore store = SpriteStore.get();
		Sprite mother = store.getSprite(image);
		int tileWidth = mother.getWidth() / 3;
		int tileHeight = mother.getHeight() / 3;
		images = new Sprite[9];
		int i = 0;
		for (int y = 0; y < 3 * tileHeight; y += tileHeight) {
			for (int x = 0; x < 3 * tileWidth; x += tileWidth) {
				images[i] = store.getTile(mother, x, y, tileWidth, tileHeight);
				i++;
			}
		}
	}

	/**
	 * Create a new BackgroundPainter. The tiles used for painting will be cut
	 * non-uniformly, so that the grid will be placed according to the given
	 * dimensions.
	 *
	 * @param image image name
	 * @param leftWidth width of the left tile row
	 * @param centerWidth width of the center tile row
	 * @param topHeight height of the top tile row
	 * @param centerHeight height of the center tile row
	 */
	public BackgroundPainter(String image, int leftWidth, int centerWidth,
			int topHeight, int centerHeight) {
		SpriteStore store = SpriteStore.get();
		Sprite mother = store.getSprite(image);
		images = new Sprite[9];
		int[] widths = new int[3];
		widths[0] = leftWidth;
		widths[1] = centerWidth;
		widths[2] = mother.getWidth() - leftWidth - centerWidth;
		int[] heights = new int[3];
		heights[0] = topHeight;
		heights[1] = centerHeight;
		heights[2] = mother.getHeight() - topHeight - centerHeight;
		int x = 0;
		int y = 0;
		int i = 0;
		for (int yInd = 0; yInd < 3; yInd++) {
			for (int xInd = 0; xInd < 3; xInd++) {
				images[i] = store.getTile(mother, x, y, widths[xInd], heights[yInd]);
				x += widths[xInd];
				i++;
			}
			x = 0;
			y += heights[yInd];
		}
	}

	/**
	 * Paint an area with the image pattern.
	 *
	 * @param g graphics
	 * @param width width of the painted area
	 * @param height height of the painted area
	 */
	public void paint(Graphics g, int width, int height) {
		// Paint the center part. That covers the whole area
		Sprite centerSprite = images[4];
		int centerWidth = centerSprite.getWidth();
		int centerHeight = centerSprite.getHeight();
		for (int y = 0; y < height; y += centerHeight) {
			for (int x = 0; x < width; x += centerWidth) {
				centerSprite.draw(g, x, y);
			}
		}

		// Sides
		// Some needed dimensions (and sprites)
		Sprite rightSprite = images[5];
		int rightWidth = rightSprite.getWidth();
		Sprite leftSprite = images[3];
		int leftWidth = leftSprite.getHeight();
		Sprite topSprite = images[1];
		int topHeight = topSprite.getHeight();
		Sprite bottomSprite = images[7];
		int bottomHeight = bottomSprite.getHeight();

		// Top row.
		for (int x = leftWidth; x < width - rightWidth; x += centerWidth) {
			topSprite.draw(g, x, 0);
		}
		// left side
		for (int y = topHeight; y < height - bottomHeight; y += centerHeight) {
			leftSprite.draw(g, 0, y);
		}
		/*
		 * The rest of the sides will not tile properly, but the background
		 * pattern is subtle enough that it will not be immediately noticeable.
		 */
		// right side
		// Do not draw over the left side, but let the image overflow from the
		// right if there's no space
		int rightX = Math.max(width - rightWidth, leftWidth);
		for (int y = topHeight; y < height - bottomHeight; y += centerHeight) {
			rightSprite.draw(g, rightX, y);
		}
		// bottom
		// Do not draw over the top border, but let the scroll overflow from the
		// bottom if there's no space
		int bottomY = Math.max(height - bottomHeight, topHeight);
		for (int x = centerWidth; x < width - centerWidth; x += centerWidth) {
			bottomSprite.draw(g, x, bottomY);
		}

		// Corners. Again, only the first one will tile properly
		// Top left corner
		Sprite sprite = images[0];
		sprite.draw(g, 0, 0);
		// Top right corner
		sprite = images[2];
		sprite.draw(g, rightX, 0);
		// Bottom left corner
		sprite = images[6];
		sprite.draw(g, 0, bottomY);
		// Bottom right corner
		sprite = images[8];
		sprite.draw(g, rightX, bottomY);
	}
}
