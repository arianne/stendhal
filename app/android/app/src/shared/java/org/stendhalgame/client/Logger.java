/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.stendhalgame.client;

import java.io.File;


/**
 * Dummy class for release builds.
 */
public class Logger {

	private static File logsDir;

	public static enum LogLevel {
		INFO,
		WARN,
		ERROR,
		DEBUG
	}

	private static Logger instance;


	public static Logger get() {
		if (instance == null) {
			instance = new Logger();
		}

		return instance;
	}

	public static void init(final File dir, @SuppressWarnings("unused") final MainActivity activity) {
		logsDir = new File(dir.getPath() + "/logs");
	}

	public static void writeLine(final String text) {
		// do nothing
	}

	public static void writeLine(final String text, final LogLevel level) {
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

	public static void debug(final boolean notify, final String text) {
		// do nothing
	}

	public static void notify(final String message) {
		// do nothing
	}

	public static void notify(final String message, final LogLevel level) {
		// do nothing
	}

	public static String getLogsDir() {
		return logsDir.getPath();
	}
}
