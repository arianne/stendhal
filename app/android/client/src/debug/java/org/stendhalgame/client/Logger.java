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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;


/**
 * A class to output debugging information to logcat file.
 */
public class Logger {

	/** Logs directory. */
	private static File logsDir;

	/** Main activity instance. */
	private static MainActivity mainActivity;

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
	public static void init(final File dir, final MainActivity activity) {
		logsDir = new File(dir.getPath() + "/logs");
		mainActivity = activity;

		writeLine("\n  // -- debugging initialized -- //", null);
		writeLine("logs directory: " + logsDir.getPath());
	}

	/**
	 * Writes a line of text to log file.
	 *
	 * @param text
	 *   Text to be written.
	 */
	public static void writeLine(final String text) {
		writeLine(text, LogLevel.DEBUG);
	}

	/**
	 * Writes a line of text to log file.
	 *
	 * @param text
	 *   Text to be written.
	 * @param level
	 *   Logging verbosity level.
	 */
	public static void writeLine(String text, final LogLevel level) {
		if (logsDir == null) {
			System.err.println("ERROR: Logger not initialized. Call Logger.init.");
			return;
		}

		if (!logsDir.exists()) {
			logsDir.mkdirs();
		}

		if (level != null) {
			text = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
				+ ": " + level.label + ": " + text;
		}
		text = text + "\n";

		final String logFile = logsDir.getPath() + "/debug-"
			+ new SimpleDateFormat("yyyy.MM.dd").format(new Date()) + ".txt";

		BufferedWriter buffer;
		try {
			buffer = new BufferedWriter(new FileWriter(logFile, true));
			buffer.write(text);
			if (buffer != null) {
				buffer.close();
			}
		} catch (final IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Writes a line of text to log file at `LogLevel.INFO` verbosity.
	 *
	 * @param text
	 *   Text to be written.
	 */
	public static void info(final String text) {
		Log.i("Logger", text);
		writeLine(text, LogLevel.INFO);
	}

	/**
	 * Writes a line of text to log file at `LogLevel.WARN` verbosity.
	 *
	 * @param text
	 *   Text to be written.
	 */
	public static void warn(final String text) {
		Log.w("Logger", text);
		writeLine(text, LogLevel.WARN);
	}

	/**
	 * Writes a line of text to log file at `LogLevel.ERROR` verbosity.
	 *
	 * @param text
	 *   Text to be written.
	 */
	public static void error(final String text) {
		Log.e("Logger", text);
		writeLine(text, LogLevel.ERROR);
	}

	/**
	 * Writes a line of text to log file at `LogLevel.DEBUG` verbosity.
	 *
	 * @param text
	 *   Text to be written.
	 */
	public static void debug(final String text) {
		Log.d("Logger", text);
		writeLine(text, LogLevel.DEBUG);
	}

	/**
	 * Writes a line of text to log file at `LogLevel.DEBUG` verbosity.
	 *
	 * @param notify
	 *   If `true` a toast notification is displayed to user.
	 * @param text
	 *   Text to be written.
	 */
	public static void debug(final boolean notify, final String text) {
		if (notify) {
			Logger.notify(text, LogLevel.DEBUG);
			return;
		}
		Logger.debug(text);
	}

	/**
	 * Displays a toast notification to user.
	 *
	 * @param message
	 *   Text to display in notification.
	 */
	public static void notify(final String message) {
		notify(message, LogLevel.INFO);
	}

	/**
	 * Displays a toast notification to user.
	 *
	 * @param message
	 *   Text to display in notification.
	 * @param level
	 *   Logging verbosity level.
	 */
	public static void notify(final String message, LogLevel level) {
		if (mainActivity == null) {
			System.err.println("ERROR: Logger not initialized. Call Logger.init.");
			return;
		}

		if (level == null) {
			level = LogLevel.DEBUG;
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(ClientView.get().getContext());
		builder.setTitle(level.label);
		builder.setMessage(message);

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
