/* $Id$ */
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
package games.stendhal.server.maps.quests.maze;

import games.stendhal.common.Rand;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.common.tiled.TileSetDefinition;

/**
 * Makes a pretty maze map based on the generated collision layer.
 */
public class MazePainter {
	/**
	 * Rules for selecting the tiles.
	 */
	private static abstract class Style {
		private final int[] wall = getWallIndices();

		/**
		 * Get the tile indices used for walls. <br>
		 * Wall tile indices:
		 * <ol>
		 * 	<li>top left convex corner</li>
		 * 	<li>bottom left convex corner</li>
		 * 	<li>left vertical wall</li>
		 * 	<li>top right convex corner</li>
		 *  <li>bottom right convex corner</li>
		 *  <li>right vertical wall</li>
		 *  <li>top horizontal wall</li>
		 *  <li>bottom horizontal wall</li>
		 *  <li>top left concave corner</li>
		 *  <li>bottom left concave corner</li>
		 *	<li>top right concave corner</li>
		 *  <li>bottom right concave corner</li>
		 * 	<li>outside</li>
		 * </ol>
		 *
		 * @return list of indices
		 */
		abstract int[] getWallIndices();
		/**
		 * Get the index of the ground tile.
		 *
		 * @return ground tile index
		 */
		abstract int getGroundIndex();
		/**
		 * Get the index of the portal tile.
		 *
		 * @return portal tile index
		 */
		abstract int getPortalIndex();

		/**
		 * Select tile to a location.
		 *
		 * @param collision collision layer
		 * @param x
		 * @param y
		 * @return tile index
		 */
		public int selectTile(LayerDefinition collision, int x, int y) {
			if (collision.getTileAt(x, y) == 0) {
				// nothing
				return 0;
			} else {
				return selectWall(collision, x, y);
			}
		}

		/**
		 * Select a wall tile.
		 *
		 * @param collision collision layer
		 * @param x
		 * @param y
		 * @return tile index
		 */
		private int selectWall(LayerDefinition collision, int x, int y) {
			if (!collides(collision, x - 1, y)) {
				if (!collides(collision, x, y - 1)) {
					// nw corner
					return wall[0];
				} else if (!collides(collision, x, y + 1)) {
					// sw corner
					return wall[1];
				} else {
					// w wall
					return wall[2];
				}
			} else if (!collides(collision, x + 1, y)) {
				if (!collides(collision, x, y - 1)) {
					// ne corner
					return wall[3];
				} else if (!collides(collision, x, y + 1)) {
					// se corner
					return wall[4];
				} else {
					// e wall
					return wall[5];
				}
			} else if (!collides(collision, x, y - 1)) {
				// n wall
				return wall[6];
			} else if (!collides(collision, x, y + 1)) {
				// s wall
				return wall[7];
			} else if (!collides(collision, x - 1, y - 1)) {
				// inner nw corner
				return wall[8];
			} else if (!collides(collision, x - 1, y + 1)) {
				// inner sw corner
				return wall[9];
			} else if (!collides(collision, x + 1, y - 1)) {
				// inner nw corner
				return wall[10];
			} else if (!collides(collision, x + 1, y + 1)) {
				// inner nw corner
				return wall[11];
			}
			// Filler
			return wall[wall.length - 1];
		}

		/**
		 * Check if the tile at location (x, y) is a collision or outside the
		 * map.
		 *
		 * @param collision
		 * @param x
		 * @param y
		 * @return <code>true</code> if the location is a collision, otherwise
		 * 	<code>false</code>
		 */
		private boolean collides(LayerDefinition collision, int x, int y) {
			if (x < 0 || y < 0 || x >= collision.getWidth() || y >= collision.getWidth()) {
				return true;
			} else {
				return (collision.getTileAt(x, y) != 0);
			}
		}
	}

	/**
	 * Style that paints the walls using the int_wall_dark_red_* set.
	 */
	private static class RedWallStyle extends Style {
		public RedWallStyle(StendhalMapStructure map) {
			TileSetDefinition set = new TileSetDefinition("filler", "../../tileset/ground/gravel.png", 1);
			map.addTileset(set);

			set = new TileSetDefinition("outercorners", "../../tileset/building/wall/int_wall_dark_red_corners_2.png", 2);
			map.addTileset(set);

			set = new TileSetDefinition("wall", "../../tileset/building/wall/int_wall_dark_red.png", 10);
			map.addTileset(set);

			set = new TileSetDefinition("innercorners", "../../tileset/building/wall/int_wall_dark_red_corners.png", 26);
			map.addTileset(set);

			set = new TileSetDefinition("paving", "../../tileset/ground/brown_paving.png", 42);
			map.addTileset(set);

			set = new TileSetDefinition("portal", "../../tileset/building/decoration/floor_sparkle.png", 44);
			map.addTileset(set);
		}

		@Override
		int[] getWallIndices() {
			return new int[] { 2, 4, 20, 3, 5, 15, 19, 16, 36, 32, 35, 31, 1 };
		}

		@Override
		int getGroundIndex() {
			return 42;
		}

		@Override
		int getPortalIndex() {
			return 59;
		}
	}

	/**
	 * Style that paints the walls using the grey decorated wall tiles.
	 */
	private static class GreyWallStyle extends Style {
		// Pick a random one from the base ground colors
		private int groundIndex = 122 + Rand.rand(3);

		public GreyWallStyle(StendhalMapStructure map) {
			TileSetDefinition set = new TileSetDefinition("wall", "../../tileset/building/wall/int_grey.png", 1);
			map.addTileset(set);

			set = new TileSetDefinition("floor", "../../tileset/ground/indoor/floor.png", 122);
			map.addTileset(set);

			set = new TileSetDefinition("portal", "../../tileset/building/decoration/floor_sparkle.png", 125);
			map.addTileset(set);
		}

		@Override
		int[] getWallIndices() {
			return new int[] { 84, 40, 65, 82, 38, 57, 105, 17, 106, 18, 104, 16, 23 };
		}

		@Override
		int getGroundIndex() {
			return groundIndex;
		}

		@Override
		int getPortalIndex() {
			return 138;
		}
	}

	private Style style;

	/**
	 * Paint tiles to a map according to a maps collision layer.
	 *
	 * @param map
	 */
	public void paint(StendhalMapStructure map) {
		LayerDefinition collision = map.getLayer("collision");

		int i = Rand.rand(2);
		switch (i) {
		case 0: style = new GreyWallStyle(map);
		break;
		case 1:
		default:
			style = new RedWallStyle(map);
		}

		LayerDefinition ground = map.getLayer("0_floor");
		// prepare the floor data arrays for for painting
		ground.build();
		fillFloor(style, ground);

		// Walls
		ground = map.getLayer("1_terrain");
		ground.build();
		drawTerrain(style, ground, collision);
	}

	/**
	 * Paint the portal tile at specified location. Can not be called before
	 * paint().
	 *
	 * @param map
	 * @param x
	 * @param y
	 */
	public void paintPortal(StendhalMapStructure map, int x, int y) {
		if (style == null) {
			throw new IllegalStateException("paint() must be called before paintPortal().");
		}
		LayerDefinition ground = map.getLayer("0_floor");
		ground.set(x, y, style.getPortalIndex());
	}

	/**
	 * Fill the ground layer with the ground tile.
	 *
	 * @param style
	 * @param floor
	 */
	private void fillFloor(Style style, LayerDefinition floor) {
		int width = floor.getWidth();
		int height = floor.getHeight();

		int ground = style.getGroundIndex();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				floor.set(x, y, ground);
			}
		}
	}

	/**
	 * Paint the walls.
	 *
	 * @param style
	 * @param terrain
	 * @param collision
	 */
	private void drawTerrain(Style style, LayerDefinition terrain, LayerDefinition collision) {
		int width = terrain.getWidth();
		int height = terrain.getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				terrain.set(x, y, style.selectTile(collision, x, y));
			}
		}
	}
}
