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

	public static Vector<Point> renderLine(int x1, int y1, int x2, int y2) {
		final Vector<Point> points = new Vector<Point>(numsteps);
		renderLine(x1, y1, x2, y2, new Action() {
			@Override
			public void fire(int x, int y) {
				points.add(new Point(x, y));
			};
		});

		return points;
	}

	private static void preparefields(int x1, int y1, int x2, int y2) {
		deltay = y2 - y1;
		deltax = x2 - x1;
		numsteps = Math.max(Math.abs(deltax), Math.abs(deltay));
		x = x1;
		y = y1;

	}

	public static void renderLine(int x1, int y1, int x2, int y2, Action action) {
		preparefields(x1, y1, x2, y2);

		for (int curpixel = 1; curpixel <= numsteps + 1; curpixel++) {

			action.fire(x, y);
			recalculateXY(x1, y1, deltay, deltax, numsteps, curpixel);
			// recalculateXY();
		}
	}

	private static void recalculateXY(int x1, int y1, int deltaY, int deltaX, int steps, int curpixel) {
		x = x1 + (int) ((deltaX * curpixel) / steps);
		y = y1 + (int) ((deltaY * curpixel) / steps);
	}

}
