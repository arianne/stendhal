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

	/** Logs directory. */
	private static File logsDir;

	/** Singleton instance. */
	private static Logger instance;


	/**
	 * Retrieves singleton instance.
	 */
	public static Logger get() {
		if (Logger.instance == null) {
			Logger.instance = new Logger();
		}

		return Logger.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private Logger() {
		// do nothing
	}

	/**
	 * Initializes logs directory.
	 */
	public static void init(final File dir, @SuppressWarnings("unused") final MainActivity activity) {
		logsDir = new File(dir.getPath() + "/logs");
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void writeLine(final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void writeLine(final String text, final LogLevel level) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void info(final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void warn(final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void error(final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void debug(final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void debug(final boolean notify, final String text) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void notify(final String message) {
		// do nothing
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void notify(final String message, final LogLevel level) {
		// do nothing
	}

	/**
	 * Retrieves logs directory.
	 *
	 * @return
	 *   Directory path to outpu logs.
	 */
	public static String getLogsDir() {
		return logsDir.getPath();
	}
}
