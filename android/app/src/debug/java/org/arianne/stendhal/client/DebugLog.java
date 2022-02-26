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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A class to output debugging information to logcat file.
 */
public class DebugLog {

	private static File logsDir;

	private static AppCompatActivity mainActivity;

	public static enum DebugLevel {
		INFO("INFO"),
		WARN("WARN"),
		ERROR("ERROR"),
		DEBUG("DEBUG");

		private final String label;

		private DebugLevel(final String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
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
		mainActivity = activity;

		writeLine("\n  // -- debugging initialized -- //", null);
		writeLine("logs directory: " + logsDir.getPath());
	}

	public static void writeLine(final String text) {
		writeLine(text, DebugLevel.DEBUG);
	}

	public static void writeLine(String text, final DebugLevel level) {
		if (logsDir == null) {
			System.err.println("ERROR: DebugLog not initialized. Call DebugLog.init.");
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

	public static void info(final String text) {
		Log.i("DebugLog", text);
		writeLine(text, DebugLevel.INFO);
	}

	public static void warn(final String text) {
		Log.w("DebugLog", text);
		writeLine(text, DebugLevel.WARN);
	}

	public static void error(final String text) {
		Log.e("DebugLog", text);
		writeLine(text, DebugLevel.ERROR);
	}

	public static void debug(final String text) {
		Log.d("DebugLog", text);
		writeLine(text, DebugLevel.DEBUG);
	}

	public static void notify(final String message) {
		notify(message, DebugLevel.DEBUG);
	}

	public static void notify(final String message, DebugLevel level) {
		if (mainActivity == null) {
			System.err.println("ERROR: DebugLog not initialized. Call DebugLog.init.");
			return;
		}

		if (level == null) {
			level = DebugLevel.DEBUG;
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setTitle(level.label);
		builder.setMessage(message);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		builder.create().show();
	}

	public static String getLogsDir() {
		return logsDir.getPath();
	}
}
