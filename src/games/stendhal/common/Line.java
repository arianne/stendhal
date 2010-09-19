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
package games.stendhal.common;

import java.awt.Point;
import java.util.Vector;

public class Line {

	private static int deltax;
	private static int deltay;
	private static int x;
	private static int y;
	private static int numsteps;

	public abstract static class Action {

		public abstract void fire(int x, int y);
	}

	public static Vector<Point> renderLine(final int x1, final int y1, final int x2, final int y2) {
		final Vector<Point> points = new Vector<Point>(numsteps);
		renderLine(x1, y1, x2, y2, new Action() {
			@Override
			public void fire(final int x, final int y) {
				points.add(new Point(x, y));
			};
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
