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
package games.stendhal.client.gui.j2d.entity;

import java.util.Map;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;

/**
 * A 2D view of a boss creature. Boss creatures have 1x2 image layouts.
 */
class BossCreature2DView extends Creature2DView {

	//
	// RPEntity2DView
	//

	/*
	 * Populate named state sprites.
	 *
	 * This only has a single frame for left and right direction.
	 *
	 * @param map The map to populate. @param tiles The master sprite. @param
	 * width The image width (in pixels). @param height The image height (in
	 * pixels).
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		final SpriteStore store = SpriteStore.get();

		final Sprite right = store.getTile(tiles, 0, 0, width, height);
		final Sprite left = store.getTile(tiles, 0, height, width, height);

		map.put(Direction.RIGHT, right);
		map.put(Direction.LEFT, left);
		map.put(Direction.UP, right);
		map.put(Direction.DOWN, left);
	}

	/**
	 * Get the number of tiles in the X axis of the base sprite.
	 *
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesX() {
		return 1;
	}

	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 *
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesY() {
		return 2;
	}
}
