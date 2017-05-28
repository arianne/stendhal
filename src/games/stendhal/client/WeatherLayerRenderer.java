/***************************************************************************
 *                 (C) Copyright 2003-2014 - Faiumoni e.V.                 *
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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.EmptySprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.sprite.SpriteTileset;
import games.stendhal.client.sprite.Tileset;
import games.stendhal.client.sprite.TilesetGroupAnimationMap;

/**
 * Weather renderer. The weather sprites are looked up from
 * "data/sprites/weather". Animation can be specified normally in animation.seq.
 * The sprite height is taken as the tile size for both directions, the rest
 * can be used for animation.
 */
public class WeatherLayerRenderer extends LayerRenderer {
	private final Sprite weather;

	/**
	 * Create a new weather layer.
	 *
	 * @param weather weather description
	 * @param color zone coloring data
	 * @param blend zone blending mode
	 */
	public WeatherLayerRenderer(String weather, Color color, Composite blend) {
		SpriteStore sr = SpriteStore.get();

		String name = "data/sprites/weather/" + weather + ".png";
		Sprite template;
		/*
		 * The failsafe sprite must not be used here, under any circumstances.
		 * It would cover all of the map, so revert to an empty sprite instead
		 */
		if (sr.getSprite(name) == sr.getFailsafe()) {
			template = new EmptySprite(1024, 1024, null);
			Logger.getLogger(WeatherLayerRenderer.class).warn("Weather sprite not found:" + name);
		} else {
			template = sr.getModifiedSprite(name, color, blend);
		}
		Tileset ts = new SpriteTileset(sr, template, template.getHeight());
		TilesetGroupAnimationMap animationMap = TileStore.getAnimationMap();

		if (animationMap != null && animationMap.get(name) != null) {
			this.weather = animationMap.get(name).getSprite(ts, 0);
		} else {
			this.weather = ts.getSprite(0);
		}
	}

	@Override
	public void draw(Graphics g, int x, int y, int w, int h) {
		int myX = (x * IGameScreen.SIZE_UNIT_PIXELS) / weather.getWidth();
		int myY = (y * IGameScreen.SIZE_UNIT_PIXELS) / weather.getHeight();
		int myW = (w * IGameScreen.SIZE_UNIT_PIXELS) / weather.getWidth() + 1;
		int myH = (h * IGameScreen.SIZE_UNIT_PIXELS) / weather.getHeight() + 1;

		int sy = myY * weather.getHeight();
		for (int j = myY; j < myY + myH; j++) {
			int sx = myX * weather.getWidth();

			for (int i = myX; i < myX + myW; i++) {
				weather.draw(g, sx, sy);
				sx += weather.getWidth();
			}
			sy += weather.getHeight();
		}
	}

	@Override
	public void setTileset(Tileset tileset) {
		throw new UnsupportedOperationException("Adding tilesets not supported");
	}
}
