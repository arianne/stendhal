package games.stendhal.common;

import java.awt.Point;
import java.util.Vector;

public class Line {

	public abstract static class Action {

		public abstract void fire(int x, int y);
	}



	public static Vector<Point> renderLine(int x1, int y1, int x2, int y2) {
		int deltax = Math.abs(x2 - x1); // The difference between the x's
		int deltay = Math.abs(y2 - y1); // The difference between the y's
		int x = x1; // Start x off at the first pixel
		int y = y1; // Start y off at the first pixel
		int xinc1 = 0;
		int xinc2 = 0;
		int yinc1 = 0;
		int yinc2 = 0;
		int num = 0;
		int numadd = 0;
		int den = 0;
		int numpixels = 0;

		if (x2 >= x1) { // The x-values are increasing
			xinc1 = 1;
			xinc2 = 1;
		} else { // The x-values are decreasing
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) { // The y-values are increasing
			yinc1 = 1;
			yinc2 = 1;
		} else { // The y-values are decreasing
			yinc1 = -1;
			yinc2 = -1;
		}

		if (deltax >= deltay) { // There is at least one x-value for every
								// y-value

			xinc1 = 0; // Don't change the x when numerator >= denominator
			yinc2 = 0; // Don't change the y for every iteration
			den = deltax;
			num = deltax / 2;
			numadd = deltay;
			numpixels = deltax; // There are more x-values than y-values
		} else { // There is at least one y-value for every x-value
			xinc2 = 0; // Don't change the x for every iteration
			yinc1 = 0; // Don't change the y when numerator >= denominator
			den = deltay;
			num = deltay / 2;
			numadd = deltax;
			numpixels = deltay; // There are more y-values than x-values
		}

		Vector<Point> points = new Vector<Point>(numpixels);

		for (int curpixel = 0; curpixel <= numpixels; curpixel++) {
			points.add(new Point(x, y));

			num += numadd; // Increase the numerator by the top of the fraction
			if (num >= den) { // Check if numerator >= denominator
				num -= den; // Calculate the new numerator value
				x += xinc1; // Change the x as appropriate
				y += yinc1; // Change the y as appropriate
			}
			x += xinc2; // Change the x as appropriate
			y += yinc2; // Change the y as appropriate
		}

		return points;
	}

	// TODO: refactor duplicate code
	public static void renderLine(int x1, int y1, int x2, int y2, Action action) {
		int deltax = Math.abs(x2 - x1); // The difference between the x's
		int deltay = Math.abs(y2 - y1); // The difference between the y's
		int x = x1; // Start x off at the first pixel
		int y = y1; // Start y off at the first pixel
		int xinc1 = 0;
		int xinc2 = 0;
		int yinc1 = 0;
		int yinc2 = 0;
		int numerator = 0;
		int numadd = 0;
		int denominator = 0;
		int numpixels = 0;

		if (x2 >= x1) { // The x-values are increasing
			xinc1 = 1;
			xinc2 = 1;
		} else { // The x-values are decreasing
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) { // The y-values are increasing
			yinc1 = 1;
			yinc2 = 1;
		} else { // The y-values are decreasing
			yinc1 = -1;
			yinc2 = -1;
		}

		if (deltax >= deltay) {
			// There is at least one x-value for every y-value

			xinc1 = 0; // Don't change the x when numerator >= denominator
			yinc2 = 0; // Don't change the y for every iteration
			denominator = deltax;
			numerator = deltax / 2;
			numadd = deltay;
			numpixels = deltax; // There are more x-values than y-values
		} else	{
			 // There is at least one y-value for every x-value
			xinc2 = 0; // Don't change the x for every iteration
			yinc1 = 0; // Don't change the y when numerator >= denominator
			denominator = deltay;
			numerator = deltay / 2;
			numadd = deltax;
			numpixels = deltay; // There are more y-values than x-values
		}

		for (int curpixel = 0; curpixel <= numpixels; curpixel++) {
			action.fire(x, y);
			numerator += numadd; // Increase the numerator by the top of the fraction
			if (numerator >= denominator) {
				numerator -= denominator; // Calculate the new numerator value
				x += xinc1; // Change the x as appropriate
				y += yinc1; // Change the y as appropriate
			}
			x += xinc2; // Change the x as appropriate
			y += yinc2; // Change the y as appropriate
		}
	}
}
