/* $Id$ */
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
package games.stendhal.common;

import java.awt.Point;
import java.util.Vector;

/**
 * a line consiting of points
 */
public class Line {

	// TODO: Don't use stattic mutable attributes
	// TODO: Don't use Vector but unsynchronized Lists

	private static int deltax;
	private static int deltay;
	private static int x;
	private static int y;
	private static int numsteps;

	/**
	 * callback which is invoked for each point
	 */
	public abstract static class Action {

		/** 
		 * callback for point (x, y)
		 *
		 * @param x x-coordinate
		 * @param y y-coordinate
		 */
		public abstract void fire(int x, int y);
	}

	/**
	 * renders a line from (x1, y2) to (x2, y2)
	 *
	 * @param x1 x-cooridinate of start point
	 * @param y1 y-cooridinate of start point
	 * @param x2 x-cooridinate of end point
	 * @param y2 y-cooridinate of end point
	 * @return vector of points
	 */
	public static Vector<Point> renderLine(final int x1, final int y1, final int x2, final int y2) {
		final Vector<Point> points = new Vector<Point>(numsteps);
		renderLine(x1, y1, x2, y2, new Action() {
			@Override
			public void fire(final int x, final int y) {
				points.add(new Point(x, y));
			}
		});

		return points;
	}

	private static void preparefields(final int x1, final int y1, final int x2, final int y2) {
		deltay = y2 - y1;
		deltax = x2 - x1;
		numsteps = Math.max(Math.abs(deltax), Math.abs(deltay));
		x = x1;
		y = y1;

	}

	/**
	 * renders a line from (x1, y2) to (x2, y2)
	 *
	 * @param x1 x-cooridinate of start point
	 * @param y1 y-cooridinate of start point
	 * @param x2 x-cooridinate of end point
	 * @param y2 y-cooridinate of end point
	 * @param action callback to call for each point
	 */
	public static void renderLine(final int x1, final int y1, final int x2, final int y2, final Action action) {
		preparefields(x1, y1, x2, y2);

		for (int curpixel = 1; curpixel <= numsteps + 1; curpixel++) {

			action.fire(x, y);
			recalculateXY(x1, y1, deltay, deltax, numsteps, curpixel);
			// recalculateXY();
		}
	}

	private static void recalculateXY(final int x1, final int y1, final int deltaY, final int deltaX, final int steps, final int curpixel) {
		x = x1 + ((deltaX * curpixel) / steps);
		y = y1 + ((deltaY * curpixel) / steps);
	}

}
