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
package games.stendhal.client;

import java.awt.Composite;
import java.util.ArrayList;
import java.util.List;

import games.stendhal.client.sprite.CompositeSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;

/**
 * A LayerRenderer that merges several tile layers in to one using
 * CompositeSprites.
 */
class CompositeLayerRenderer extends TileRenderer {
	/**
	 * Create a CompositeLayerRenderer from a set of {@link TileRenderer}s
	 *
	 * @param layerRenderers TileRenderers used for compositing
	 * @param blend composite mode for drawing the adjustment layer
	 * @param adjustLayer adjustment layer
	 */
	CompositeLayerRenderer(List<TileRenderer> layerRenderers,
			Composite blend, TileRenderer adjustLayer) {
		LayerRenderer lr = layerRenderers.get(0);
		width = lr.getWidth();
		height = lr.getHeight();

		Sprite maps[][] = new Sprite[layerRenderers.size()][];
		int i = 0;
		for (LayerRenderer layer : layerRenderers) {
			maps[i] = ((TileRenderer) layer).spriteMap;
			i++;
		}
		createComposites(layerRenderers, blend, adjustLayer);
	}

	/**
	 * Fill the spriteMap with composite sprites.
	 *
	 * @param renderers slave layers
	 * @param blend composite mode for drawing the adjustment layer
	 * @param adjustLayer adjustment layer
	 */
	private void createComposites(List<TileRenderer> renderers,
			Composite blend, TileRenderer adjustLayer) {
		int size = width * height;
		spriteMap = new Sprite[size];
		SpriteCache cache = SpriteCache.get();
		int layers = renderers.size();

		List<Sprite> slaveSprites = new ArrayList<Sprite>(layers);

		for (int i = 0; i < size; i++) {
			for (TileRenderer r : renderers) {
				slaveSprites.add(r.tileset.getSprite(r.map[i]));
			}
			Sprite adjSprite = null;
			if (adjustLayer != null) {
				adjSprite = adjustLayer.tileset.getSprite(adjustLayer.map[i]);
			}
			spriteMap[i] = CompositeSprite.getComposite(cache, slaveSprites,
					blend, adjSprite);
			slaveSprites.clear();
		}

		// Wipe out unneeded data from the slaves. Assumes their draw() method
		// will not be called from anywhere. The renderers will be useless by
		// themselves after this, so if the assumption changes, this needs code
		// will need to be revisited
		for (TileRenderer r : renderers) {
			r.map = null;
			r.spriteMap = null;
			r.tileset = null;
		}
	}
}
