/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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
import java.util.ArrayList;
import java.util.List;

/**
 * a line consisting of points
 */
public class Line {
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
	 * Class for holding data common to the line calculations.
	 */
	private static final class State {
		final int deltay;
		final int deltax;
		final int steps;

		State(int x1, int y1, int x2, int y2) {
			deltay = y2 - y1;
			deltax = x2 - x1;
			steps = Math.max(Math.abs(deltax), Math.abs(deltay));
		}
	}

	/**
	 * renders a line from (x1, y2) to (x2, y2)
	 *
	 * @param x1 x-coordinate of start point
	 * @param y1 y-coordinate of start point
	 * @param x2 x-coordinate of end point
	 * @param y2 y-coordinate of end point
	 * @return vector of points
	 */
	public static List<Point> renderLine(final int x1, final int y1, final int x2, final int y2) {
		State state = new State(x1, y1, x2, y2);
		final List<Point> points = new ArrayList<Point>(state.steps);

		renderLine(x1, y1, state, new Action() {
			@Override
			public void fire(final int x, final int y) {
				points.add(new Point(x, y));
			}
		});

		return points;
	}

	/**
	 * renders a line from (x1, y2) to (x2, y2)
	 *
	 * @param x1 x-coordinate of start point
	 * @param y1 y-coordinate of start point
	 * @param x2 x-coordinate of end point
	 * @param y2 y-coordinate of end point
	 * @param action callback to call for each point
	 */
	public static void renderLine(final int x1, final int y1, final int x2, final int y2, final Action action) {
		State state = new State(x1, y1, x2, y2);

		renderLine(x1, y1, state, action);
	}

	private static void renderLine(int x1, int y1, State state, Action action) {
		int x = x1;
		int y = y1;

		for (int curpixel = 1; curpixel <= state.steps + 1; curpixel++) {
			action.fire(x, y);

			x = x1 + ((state.deltax * curpixel) / state.steps);
			y = y1 + ((state.deltay * curpixel) / state.steps);
		}
	}
}
