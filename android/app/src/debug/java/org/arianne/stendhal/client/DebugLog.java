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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;


/**
 * A class to output debugging information to logcat file.
 */
public class DebugLog {

	private static File dataDir;

	private static AppCompatActivity mainActivity;

	public static enum DebugLevel {
		ERROR("ERROR"),
		DEBUG("DEBUG");

		public final String label;

		private DebugLevel(final String label) {
			this.label = label;
		}
	}


	public static void init(final File dir, final AppCompatActivity activity) {
		dataDir = dir;
		mainActivity = activity;
	}

	public static void writeLine(final String text) {
		writeLine(text, DebugLevel.DEBUG);
	}

	public static void writeLine(String text, final DebugLevel level) {
		if (dataDir == null) {
			System.err.println("ERROR: DebugLog not initialized. Call DebugLog.init.");
			return;
		}

		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}

		text = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())
			+ ": " + text + "\n";

		final String logFile = dataDir.getPath() + "/debug-"
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
}
