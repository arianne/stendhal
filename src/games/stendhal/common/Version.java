/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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


/**
 * Handles version numbers
 *
 * Actual number stored in games.stendhal.common.Debug.VERSION
 * This file duplicates methods from games.stendhal.client.update.Version
 * as the updater should not depend on anything outside games.stendhal.client.update
 *
 * @author hendrik
 */
public class Version {
	/** Stendhal version */
	public final static String VERSION = Debug.VERSION;

	/**
	 * Extract the specified number of parts from a version-string.
	 *
	 * @param version
	 *            version-string
	 * @param parts
	 *            number of parts to extract
	 * @return parts of the version-string
	 */
	public static String cut(final String version, final int parts) {
		int pos = 0;
		for (int i = 0; i < parts; i++) {
			final int temp = version.indexOf(".", pos + 1);
			if (temp < 0) {
				pos = version.length();
				break;
			}
			pos = temp;
		}
		return version.substring(0, pos);
	}

	/**
	 * Compares two versions.
	 *
	 * @param v1
	 *            1st version string
	 * @param v2
	 *            2nd version string
	 * @return see compare
	 */
	public static int compare(final String v1, final String v2) {
		String version1 = v1;
		String version2 = v2;
		while (!version1.equals("") || !version2.equals("")) {
			// split version string at the first dot into the current
			// component and the rest of the version
			String component1;
			final int pos1 = version1.indexOf(".");
			if (pos1 > -1) {
				component1 = version1.substring(0, pos1);
				version1 = version1.substring(pos1 + 1);
			} else {
				component1 = version1;
				version1 = "";
			}
			if (component1.equals("")) {
				component1 = "0";
			}

			String component2;
			final int pos2 = version2.indexOf(".");
			if (pos2 > -1) {
				component2 = version2.substring(0, pos2);
				version2 = version2.substring(pos2 + 1);
			} else {
				component2 = version2;
				version2 = "";
			}
			if (component2.equals("")) {
				component2 = "0";
			}

			// if the current component of both version is equal,
			// we have to have a look at the next one. Otherwise
			// we return the result of this comparison.
			int res = 0;
			try {
				// try an integer comparison so that 2 < 13
				final int componentInt1 = Integer.parseInt(component1.trim());
				final int componentInt2 = Integer.parseInt(component2.trim());
				res = componentInt1 - componentInt2;
			} catch (final NumberFormatException e) {
				// integer comparison failed because one component is not a
				// number. Do a string comparison.
				res = component1.compareTo(component2);
			}
			if (res != 0) {
				return res;
			}
		}
		return 0;
	}

	private static String firstWord(String sentence) {
		int pos = sentence.indexOf(' ');
		if (pos > -1) {
			return sentence.substring(0, pos);
		}
		return sentence;
	}

	/**
	 * Checks whether these versions of stendhal are compatible.
	 *
	 * @param v1
	 *            one version string
	 * @param v2
	 *            another version string
	 * @return true, iff the first two components are equal
	 */
	public static boolean checkCompatibility(final String v1, final String v2) {
		final String ev1 = cut(firstWord(v1), 2);
		final String ev2 = cut(firstWord(v2), 2);
		final boolean res = ev1.equals(ev2);
		return res;
	}

	private Version() {
		// hide constructor; this is a static class
	}

	/**
	 * gets the version
	 *
	 * @return version
	 */
	public static String getVersion() {
		return Debug.VERSION;
	}

}
