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

	private static File logsDir;

	public static enum DebugLevel {
		INFO,
		WARN,
		ERROR,
		DEBUG
	}

	private static DebugLog instance;


	public static DebugLog get() {
		if (instance == null) {
			instance = new DebugLog();
		}

		return instance;
	}

	public static void init(final File dir, final AppCompatActivity activity) {
		logsDir = new File(dir.getPath() + "/logs");
	}

	public static void writeLine(final String text) {
		// do nothing
	}

	public static void writeLine(final String text, final DebugLevel level) {
		// do nothing
	}

	public static void info(final String text) {
		// do nothing
	}

	public static void warn(final String text) {
		// do nothing
	}

	public static void error(final String text) {
		// do nothing
	}

	public static void debug(final String text) {
		// do nothing
	}

	public static void notify(final String message) {
		// do nothing
	}

	public static void notify(final String message, final DebugLevel level) {
		// do nothing
	}

	public static String getLogsDir() {
		return logsDir.getPath();
	}
}
