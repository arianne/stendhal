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

import android.app.AlertDialog;
import android.content.DialogInterface;


/**
 * Semi-functional logger for release builds.
 * 
 * Displays notification dialogs but does not write to logs directory.
 */
public class Logger {

	/** Attribute denoting state of initialization. */
	private static boolean initialized = false;
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
	public static void init(final File dir) {
		if (Logger.initialized) {
			Logger.warn("tried to re-initialize logger");
			return;
		}
		Logger.initialized = true;
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
	 * Shows an information dialog.
	 *
	 * @param notify
	 *   If `true` a toast notification is displayed to user.
	 * @param text
	 *   Text to be displayed.
	 */
	public static void info(final boolean notify, final String text) {
		if (notify) {
			Logger.notify(text, LogLevel.INFO);
		}
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void warn(final String text) {
		// do nothing
	}

	/**
	 * Shows a warning dialog.
	 *
	 * @param notify
	 *   If `true` a toast notification is displayed to user.
	 * @param text
	 *   Text to be displayed.
	 */
	public static void warn(final boolean notify, final String text) {
		if (notify) {
			Logger.notify(text, LogLevel.WARN);
		}
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void error(final String text) {
		// do nothing
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param notify
	 *   If `true` a toast notification is displayed to user.
	 * @param text
	 *   Text to be displayed.
	 */
	public static void error(final boolean notify, final String text) {
		if (notify) {
			Logger.notify(text, LogLevel.ERROR);
		}
	}

	/**
	 * Dummy method for release builds.
	 */
	public static void debug(final String text) {
		// do nothing
	}

	/**
	 * Shows a debug message dialog.
	 *
	 * @param notify
	 *   If `true` a toast notification is displayed to user.
	 * @param text
	 *   Text to be displayed.
	 */
	public static void debug(final boolean notify, final String text) {
		if (notify) {
			Logger.notify(text, LogLevel.DEBUG);
		}
	}

	/**
	 * Displays a toast notification to user.
	 *
	 * @param text
	 *   Text to display in notification.
	 */
	public static void notify(final String text) {
		notify(text, LogLevel.INFO);
	}

	/**
	 * Displays a toast notification to user.
	 *
	 * @param text
	 *   Text to display in notification.
	 * @param level
	 *   Logging verbosity level.
	 */
	public static void notify(final String text, LogLevel level) {
		if (!Logger.initialized) {
			System.err.println("ERROR: Logger not initialized. Call Logger.init.");
			return;
		}

		if (level == null) {
			level = LogLevel.INFO;
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.get());
		builder.setTitle(level.label);
		builder.setMessage(text);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		builder.create().show();
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
