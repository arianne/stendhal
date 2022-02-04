/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Dummy class for release builds.
 */
public class DebugLog {

	public static enum DebugLevel {
		ERROR,
		DEBUG
	}


	public static void init(final File dir, final AppCompatActivity activity) {
		// do nothing
	}

	public static void writeLine(final String text) {
		// do nothing
	}

	public static void writeLine(final String text, final DebugLevel level) {
		// do nothing
	}

	public static void notify(final String message) {
		// do nothing
	}

	public static void notify(final String message, final DebugLevel level) {
		// do nothing
	}
}
