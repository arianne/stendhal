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

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;

import marauroa.common.Pair;


/**
 * The main purpose of this class is to handle downloading screenshots
 * created by the web client page.
 */
public class DownloadHandler {

	public Pair<Boolean, String> download(final String url, final String mimetype) {
		final Pair<Boolean, String> res = new Pair<Boolean, String>(false, null);

		final Uri uri = Uri.parse(url);
		final String scheme = uri.getScheme();
		final String storageState = Environment.getExternalStorageState();

		if (!ClientView.get().isGameActive()) {
			res.setSecond("downloading from this page not supported");
		} else if (!"data".equals(scheme)) {
			res.setSecond("download type \"" + scheme + "\" not supported");
		} else if (!"image/png".equals(mimetype) || !url.startsWith("data:image/png;base64,")) {
			res.setSecond("mimetype not supported: " + mimetype);
		} else if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
			res.setSecond("storage not available for writing (state: " + storageState + ")");
		} else {
			final File targetDir = new File(Environment.getExternalStorageDirectory()
					+ "/Pictures/Screenshots");
			// TODO: format output name using YYYYMMDD_HH.MM.SS.MS
			final String targetName = "stendhal_" + System.currentTimeMillis() + ".png";

			DebugLog.debug("Saving screenshot: " + targetDir.getPath()
					+ "/" + targetName + " (" + mimetype + ")");

			String msg;
			String stacktrace = null;
			try {
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}

				final byte[] data = Base64.decode(url.split("base64,")[1], Base64.DEFAULT);
				final FileOutputStream fos = new FileOutputStream(new File(targetDir, targetName));
				fos.write(data);
				fos.close();

				res.setFirst(true);
				msg = "saved screenshot to " + targetDir.getPath() + "/" + targetName;
			} catch (final java.lang.NoClassDefFoundError e) {
				msg = "an error occurred while decoding data (see debug log for more info)";
				stacktrace = stackTraceToString(e);
			} catch (java.io.IOException e) {
				msg = "an error occurred while attempting to write file (see debug log for more info)";
				stacktrace = stackTraceToString(e);
			}

			res.setSecond(msg);
			if (stacktrace != null) {
				DebugLog.error(stacktrace.toString());
			}
		}

		return res;
	}

	/**
	 * Converts an exception stacktrace to string.
	 *
	 * @param e
	 *     The error with stacktrace information.
	 * @return
	 *     String formatted stacktrace.
	 */
	private String stackTraceToString(final Throwable e) {
		final StringBuilder sb = new StringBuilder();
		sb.append(e.toString());
		for (final StackTraceElement ste: e.getStackTrace()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(ste.toString());
		}

		return sb.toString();
	}
}
