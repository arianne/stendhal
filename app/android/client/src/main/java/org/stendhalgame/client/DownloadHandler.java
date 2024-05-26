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
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;


/**
 * The main purpose of this class is to handle downloading screenshots
 * created by the web client page.
 */
public class DownloadHandler {

	private static enum DownloadType {
		IMAGE_PNG("image/png"),
		TEXT_PLAIN("text/plain"),
		UNSUPPORTED(null);

		private String value;

		private DownloadType(final String value) {
			this.value = value;
		}

		public static DownloadType fromString(final String value) {
			for (final DownloadType v: DownloadType.values()) {
				if (v.value != null && v.value.equals(value)) {
					return v;
				}
			}
			return DownloadType.UNSUPPORTED;
		}
	}

	private boolean result = false;
	private String message = null;


	/**
	 * Checks for a supported MIME type.
	 *
	 * @param url
	 *   File or data URL.
	 * @param mimetype
	 *   Detected MIME type.
	 * @return
	 *   Download type or unsupported.
	 */
	private DownloadType checkMimeType(final String url, final String mimetype) {
		final DownloadType dtype = DownloadType.fromString(mimetype);
		if (DownloadType.UNSUPPORTED.equals(dtype) && url.startsWith("data:image/png;base64,")) {
			return DownloadType.IMAGE_PNG;
		}
		return dtype;
	}

	/**
	 * Writes file data to device storage.
	 *
	 * @param dir
	 *   Directory where new file is to be created.
	 * @param basename
	 *   Filename of new file.
	 * @param data
	 *   File contents to be written to storage.
	 */
	private void downloadInternal(final File dir, final String basename, final byte[] data) {
		String stacktrace = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			final FileOutputStream fos = new FileOutputStream(new File(dir, basename));
			fos.write(data);
			fos.close();
			this.result = true;
		} catch (final java.lang.NoClassDefFoundError e) {
			this.message = "an error occurred while decoding data (see debug log for more info)";
			stacktrace = stackTraceToString(e);
		} catch (java.io.IOException e) {
			this.message = "an error occurred while attempting to write file (see debug log for more info)";
			stacktrace = stackTraceToString(e);
		}

		if (stacktrace != null) {
			Logger.error(stacktrace.toString());
		}
	}

	/**
	 * Writes file data to device storage.
	 *
	 * @param url
	 *   A data URL.
	 * @param mimetype
	 *   Detected MIME type.
	 * @param targetName
	 *   Filename for new file.
	 */
	public void download(final String url, final String mimetype, String targetName) {

		final Uri uri = Uri.parse(url);
		final String scheme = uri.getScheme();
		final String storageState = Environment.getExternalStorageState();
		final DownloadType dtype = checkMimeType(url, mimetype);

		if (!MainActivity.get().getActiveClientView().isGameActive()) {
			this.message = "downloading from this page not supported";
		} else if (!"data".equals(scheme)) {
			// only data URL supported
			this.message = "download type \"" + scheme + "\" not supported";
		} else if (DownloadType.UNSUPPORTED.equals(dtype)) {
			this.message = "mimetype not supported: " + mimetype;
		} else if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
			this.message = "storage not available for writing (state: " + storageState + ")";
		} else if (DownloadType.IMAGE_PNG.equals(dtype)) {
			final File targetDir = new File(Environment.getExternalStorageDirectory()
					+ "/Pictures/Screenshots");
			if (targetName == null) {
				// default filename
				targetName = "stendhal_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())
						+ ".png";
			}

			Logger.debug("Saving screenshot: " + targetDir.getPath()
					+ "/" + targetName + " (" + mimetype + ")");

			final byte[] data = Base64.decode(url.split("base64,")[1], Base64.DEFAULT);
			downloadInternal(targetDir, targetName, data);
			if (this.result) {
				this.message = "saved screenshot to " + targetDir.getPath() + "/" + targetName;
			}
		} else if (DownloadType.TEXT_PLAIN.equals(dtype)) {
			// `Environment.DIRECTORY_DOWNLOADS` fails on newer Android versions
			//final File targetDir = new File(Environment.DIRECTORY_DOWNLOADS);
			final File targetDir = new File(Environment.getExternalStorageDirectory() + "/Download");
			if (targetName == null) {
				// default filename
				targetName = "stendhal_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())
						+ ".txt";
			}

			Logger.debug("Saving file: " + targetDir.getPath()
			+ "/" + targetName + " (" + mimetype + ")");

			final String[] parts = url.split(",");
			byte[] data;
			if (parts[0].endsWith(";base64")) {
				data = Base64.decode(parts[1], Base64.DEFAULT);
			} else {
				try {
					data = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name())
							.getBytes(StandardCharsets.UTF_8);
				} catch (final java.io.UnsupportedEncodingException e) {
					this.message = "an error occurred while attempting to decode data URL (see debug log for more info)";
					Logger.error(stackTraceToString(e));
					return;
				}
			}

			downloadInternal(targetDir, targetName, data);
			if (this.result) {
				this.message = "file saved to " + targetDir.getPath() + "/" + targetName;
			}
		} else {
			this.message = "an unknown error occurred";
		}
	}

	/**
	 * Writes file data to device storage.
	 *
	 * @param url
	 *   A data URL.
	 * @param mimetype
	 *   Detected MIME type.
	 */
	public void download(final String url, final String mimetype) {
		download(url, mimetype, null);
	}

	public boolean getResult() {
		return result;
	}

	public String getMessage() {
		return message;
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
