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

public class RenderingPipeline {
	private static RenderingPipeline renderPipeline;

	private StaticGameLayers gameLayers;

	private GameObjects gameObjects;

	/** singlenton class. */
	private RenderingPipeline() {
		//declared  for being singleton
		//yet nothing to do
	}

	/**
	 * 
	 * @param layer
	 */
	public void setGameLayer(StaticGameLayers layer) {
		gameLayers = layer;
	}

	/**
	 * @param objects
	 */
	public void setGameObjects(GameObjects objects) {
		gameObjects = objects;
	}

	/**
	 * singleton creation
	 * @return THE instance
	 */
	public static RenderingPipeline get() {
		if (renderPipeline == null) {
			renderPipeline = new RenderingPipeline();
		}

		return renderPipeline;
	}

	/**
	 * draw the GameLayers from bootom to top, relies on exact naming of the layers
	 * @param screen
	 */
	public void draw(final GameScreen screen) {
		final String set = gameLayers.getRPZoneLayerSet();
		gameLayers.draw(screen, set + "_0_floor");
		gameLayers.draw(screen, set + "_1_terrain");
		gameLayers.draw(screen, set + "_2_object");
		gameObjects.draw(screen);
		gameLayers.draw(screen, set + "_3_roof");
		gameLayers.draw(screen, set + "_4_roof_add");
		gameObjects.drawHPbar(screen);
		gameObjects.drawText(screen);
	}
}
