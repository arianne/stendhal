/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a wall.
 */
class Wall2DView extends Entity2DView<IEntity> {

	/**
	 * Build the visual representation of this entity.
	 *
	 * @param entity entity for which to build the representation
	 */
	@Override
	protected void buildRepresentation(IEntity entity) {
		final SpriteStore store = SpriteStore.get();
		ZoneInfo info = ZoneInfo.get();
		Sprite sprite = store.getModifiedSprite(translate(getClassResourcePath()),
				info.getZoneColor(), info.getColorMethod());

		// TODO: can we cache these images (based on objectref, height and width?)
		int width = (int) entity.getWidth();
		int height = (int) entity.getHeight();
		BufferedImage image = new BufferedImage(width * 32, height * 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		for (int ix = 0; ix < width; ix++) {
			for (int iy = 0; iy < height; iy++) {
				sprite.draw(graphics, ix * 32, iy * 32);
			}
		}
		setSprite(new ImageSprite(image));
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 *
	 * Also, players can only interact with the topmost entity.
	 *
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 1000;
	}

	/**
	 * Draw the base entity part.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn entity width.
	 * @param height
	 *            The drawn entity height.
	 */
	/*
	@Override
	protected void drawEntity(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
		int w = (int) super.entity.getWidth();
		int h = (int) super.entity.getHeight();
		for (int ix = 0; ix < w; ix++) {
			for (int iy = 0; iy < h; iy++) {
				getSprite().draw(g2d, x + width * ix, y + height * iy);
			}
		}
	}*/

	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param name
	 *            The resource name.
	 *
	 * @return The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/" + name + ".png";
	}

	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.NORMAL;
	}
}
