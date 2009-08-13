/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.Tileset;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * This class renders a layer based on a complete image.
 */
public class ImageRenderer extends LayerRenderer {
	private static Logger logger = Logger.getLogger(ImageRenderer.class);
	private Sprite mySprite;

	public ImageRenderer(final URL url) {
		try {
			final BufferedImage myImage = ImageIO.read(url);
			width = myImage.getWidth();
			height = myImage.getHeight();
			mySprite = new ImageSprite(myImage);
		} catch (final Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Render the layer to screen. We assume that game screen will clip.
	 * 
	 * @param screen
	 *            The screen to draw on.
	 */
	public void draw(final IGameScreen screen) {
		if (mySprite != null) {
			screen.draw(mySprite, 0, 0);
		}
	}

	/**
	 * Render the layer to screen. We assume that game screen will clip.
	 * 
	 * @param screen
	 *            The screen to draw on.
	 * @param x
	 *            The view X world coordinate.
	 * @param y
	 *            The view Y world coordinate.
	 * @param w
	 *            The view world width.
	 * @param h
	 *            The view world height.
	 */
	@Override
	public void draw(final IGameScreen screen, final int x, final int y, final int w, final int h) {
		draw(screen);
	}

	@Override
	public void setTileset(final Tileset tilset) {
	}
}
