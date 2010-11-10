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

import games.stendhal.client.sprite.CompositeSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;

import java.util.ArrayList;
import java.util.List;

/**
 * A LayerRenderer that merges several tile layers in to one using
 * CompositeSprites.
 */
public class CompositeLayerRenderer extends TileRenderer {
	/**
	 * Create a CompositeLayerRenderer from a set of {@link TileRenderer}s
	 * 
	 * @param layerRenderers TileRenderers used for compositing
	 */
	public CompositeLayerRenderer(List<TileRenderer> layerRenderers) {
		LayerRenderer lr = layerRenderers.get(0);
		width = lr.getWidth();
		height = lr.getHeight();
		
		Sprite maps[][] = new Sprite[layerRenderers.size()][];
		int i = 0;
		for (LayerRenderer layer : layerRenderers) {
				maps[i] = ((TileRenderer) layer).spriteMap;
				i++;				
		}
		createComposites(layerRenderers);
	}

	/**
	 * Fill the spriteMap with composite sprites.
	 * 
	 * @param renderers slave layers
	 */
	private void createComposites(List<TileRenderer> renderers) {
		int size = width * height;
		spriteMap = new Sprite[size];
		SpriteCache cache = new SpriteCache();
		int layers = renderers.size();
		
		List<Sprite> slaves = new ArrayList<Sprite>(layers);
		
		for (int i = 0; i < size; i++) {
			for (TileRenderer r : renderers) {
				slaves.add(r.spriteMap[i]);
			}
			spriteMap[i] = CompositeSprite.getComposite(cache, slaves);
			slaves.clear();
		}
	}
}
