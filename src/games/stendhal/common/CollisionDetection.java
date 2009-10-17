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
package games.stendhal.common;

import games.stendhal.tools.tiled.LayerDefinition;

import java.awt.geom.Rectangle2D;

/**
 * This class loads the map and allow you to determine if a player collides or
 * not with any of the non trespasable areas of the world.
 */
public class CollisionDetection {
	CollisionMap map;

	private int width;

	private int height;

	public void clear() {
		if (map == null) {
			map = new CollisionMap(width, height);
		}
	}

	public void init(final int width, final int height) {
		if (this.width != width || this.height != height) {
			map = null;
		}
		
		
		this.width = width;
		this.height = height;
		
		clear();
	}

	public void setCollide(final int x, final int y) {
		
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}
		map.set(x, y);
	}

	public void setCollide(final Rectangle2D shape, final boolean value) {
		final double x = shape.getX();
		final double y = shape.getY();
		final double w = shape.getWidth();
		final double h = shape.getHeight();
	
		
		if ((x < 0) || (x/* +w */ >= width)) {
			return;
		}

		if ((y < 0) || (y/* +h */ >= height)) {
			return;
		}

		int startx = 0;
		if ((x >= 0)) {
			startx = (int) x;
		} 
		
		final int endx;
		if (x + w < width) {
			endx = (int) (x + w);
		} else {
			endx = width;
		}
		int starty = 0;
		if (y >= 0) {
			starty = (int) y;
		}
		 
		final int endy;
		if (y + h < height) {
			endy = (int) (y + h);
		} else {
			endy = height;
		}

		for (int k = starty; k < endy; k++) {
			for (int i = startx; i < endx; i++) {
				if (value) {
					map.set(i, k);
				} else {
					map.unset(i, k);
				}
			}
		}
	}

	public void setCollisionData(final LayerDefinition collisionLayer) {
		/* First we build the int array. */
		collisionLayer.build();

		width = collisionLayer.getWidth();
		height = collisionLayer.getHeight();
		map = new CollisionMap(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				/*
				 * NOTE: Right now our collision detection system is binary, so
				 * something or is blocked or is not.
				 */
				boolean b = collisionLayer.getTileAt(x, y) != 0;
				if (b) {
					map.set(x, y);
				}
				
				
			}
		}
	}

	/** Print the area around the (x,y) useful for debugging. 
	 * @param x 
	 * @param y 
	 * @param size */
	public void printaround(final int x, final int y, final int size) {
		for (int j = y - size; j < y + size; j++) {
			for (int i = x - size; i < x + size; i++) {
				if ((j >= 0) && (j < height) && (i >= 0) && (i < width)) {
					if ((j == y) && (i == x)) {
						System.out.print("O");
					} else if (map.get(i, j)) {
						System.out.print("X");
					} else {
						System.out.print(".");
					}
				}
			}
			System.out.println();
		}
	}

	public boolean walkable(final double x, final double y) {
		return !map.get((int) x, (int) y);
	}

	public boolean leavesZone(final Rectangle2D shape) {
		final double x = shape.getX();
		final double y = shape.getY();
		// double w=shape.getWidth();
		// double h=shape.getHeight();

		if ((x < 0) || (x/* +w */ >= width)) {
			return true;
		}

		if ((y < 0) || (y/* +h */ >= height)) {
			return true;
		}

		return false;
	}

	/**
	 * @param shape 
	 * @return true if the shape enters in any of the non trespasable areas of
	 * the map.
	 */
	public boolean collides(final Rectangle2D shape) {
		final double x = shape.getX();
		final double y = shape.getY();
		double w = shape.getWidth();
		double h = shape.getHeight();
		if ((x < 0) || (x/* +w */ >= width)) {
			return true;
		}

		if ((y < 0) || (y/* +h */ >= height)) {
			return true;
		}

		final double startx;
		if (x >= 0) {
			startx = x;
		} else {
			startx = 0;
		}
		final double endx;
		if (x + w < width) {
			endx = x + w;
		} else {
			endx = width;
		}
		final double starty;
		if (y >= 0) {
			starty = y;
		} else {
			starty = 0;
		}
		final double endy;
		if (y + h < height) {
			endy = y + h;
		} else {
			endy = height;
		}

		/*
		 * TODO: the advantages from using bitset in collissionmapare lost here
		 * the weird behaviour with clientside player when using map.collides (x,y,width, height)
		 * is caused by the delta calculation for 2dviewtiles; 
		 */
		
		for (int k = (int) starty; k < endy; k++) {
			
			for (int i = (int) startx; i < endx; i++) {
				if (map.get(i, k)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean collides(final int x, final int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}

		if ((y < 0) || (y >= height)) {
			return true;
		}
		return map.get(x, y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
