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

import java.awt.Graphics;
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

	@Override
	public void draw(Graphics g, int x, int y, int width, int height) {
		if (mySprite != null) {
			mySprite.draw(g, 0, 0);
		}
	}

	@Override
	public void setTileset(final Tileset tilset) {
	}
}
