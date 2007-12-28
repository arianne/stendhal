/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 * Various static utility methods.
 */
public class Util {

	/** no public constructor, no instance allowed. */
	private Util() {
	}

	/**
	 * This function converts an <code>int</code> integer array to a
	 * <code>byte</code> array. Each integer element is broken into 4 bytes
	 * and stored in the byte array in litte endian byte order.
	 * 
	 * @param integers
	 *            an integer array
	 * @return a byte array containing the values of the int array. The byte
	 *         array is 4x the length of the integer array.
	 */
	public static byte[] convertIntegersToBytes(int[] integers) {
		if (integers != null) {
			byte[] outputBytes = new byte[integers.length * 4];

			for (int i = 0, k = 0; i < integers.length; i++) {
				int integerTemp = integers[i];
				for (int j = 0; j < 4; j++, k++) {
					outputBytes[k] = (byte) ((integerTemp >> (8 * j)) & 0xFF);
				}
			}
			return outputBytes;
		} else {
			return null;
		}
	}

	/**
	 * This utility function will check the specified string to see if it starts
	 * with one of the OS root designations. (Ex.: '/' on Unix, 'C:' on Windows)
	 * 
	 * @param filename
	 *            a filename to check for absolute or relative path
	 * @return <code>true</code> if the specified filename starts with a
	 *         filesystem root, <code>false</code> otherwise.
	 */
	public static boolean checkRoot(String filename) {
		File[] roots = File.listRoots();

		for (int i = 0; i < roots.length; i++) {
			try {
				if (filename.toLowerCase().startsWith(roots[i].getCanonicalPath().toLowerCase())) {
					return true;
				}
			} catch (IOException e) {
				// Do we care?
			}
		}

		return false;
	}

	/**
	 * returns a rectangle for these points. The width and height are always
	 * positive
	 */
	public static Rectangle getRectangle(Point p1, Point p2) {
		int x = p1.x;
		int y = p1.y;
		int w = p2.x - x;
		int h = p2.y - y;

		if (w < 0) {
			w = -w;
			x -= w;
		}
		if (h < 0) {
			h = -h;
			y -= h;
		}

		return new Rectangle(x, y, w, h);
	}
}
