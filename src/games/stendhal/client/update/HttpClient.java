/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * a very simple http client.
 *
 * @author hendrik
 */
public class HttpClient {

	private final String urlString;

	private HttpURLConnection connection;

	private InputStream is;

	private ProgressListener progressListener;

	// 1.5 seconds
	private final static int timeout = 1500;

	private boolean tryVeryHard;

	/**
	 * An interface to notify some other parts of the program about download
	 * process.
	 */
	interface ProgressListener {

		/**
		 * update download status.
		 *
		 * @param downloadedBytes
		 *            bytes downloaded now
		 */
		void onDownloading(int downloadedBytes);

		/**
		 * completed download of this file.
		 *
		 * @param downloadedBytes
		 *            completed download
		 */
		void onDownloadCompleted(int downloadedBytes);
	}

	/**
	 * Creates a HTTP-Client which will connect to the specified URL.
	 *
	 * @param url
	 *            URL to connect to
	 */
	public HttpClient(final String url) {
		this.urlString = url;
	}

	/**
	 * Creates a HTTP-Client which will connect to the specified URL.
	 *
	 * @param url
	 *            URL to connect to
	 * @param tryVeryHard
	 *            true, to do several attempts.
	 */
	HttpClient(final String url, final boolean tryVeryHard) {
		this.urlString = url;
		this.tryVeryHard = tryVeryHard;
	}

	/**
	 * Sets a ProgressListener to be informed of download progress.
	 *
	 * @param progressListener
	 *            ProgressListener
	 */
	public void setProgressListener(final ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * connects to the server and opens a stream.
	 */
	private void openInputStream() {
		// try very hard to download updates from sourceforge as they have
		// sometimes problems with the webservers being slow or not responding
		// at all.
		try {
			final URL url = new URL(urlString);
			int retryCount = 0;
			int myTimeout = timeout;
			while (is == null) {
				retryCount++;
				try {
					connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(myTimeout);
					connection.setInstanceFollowRedirects(true);
					connection.setUseCaches(false);
					int responseCode = connection.getResponseCode();
					if (responseCode != HttpURLConnection.HTTP_OK) {
						// handle redirects
						if(isRedirect(responseCode)) {
							boolean redirect = true;
							Set<String> passedRedirects = new HashSet<String>();
							passedRedirects.add(urlString);

							while(redirect) {
								String newUrl = connection.getHeaderField("Location");

								// check if we already were redirected to this url
								if(!passedRedirects.contains(newUrl)) {
									// open the new connnection again
									passedRedirects.add(newUrl);
									connection = (HttpURLConnection) new URL(newUrl).openConnection();
									redirect = isRedirect(connection.getResponseCode());
								} else {
									System.err.println(String.format("The URL '%s' leads into a circular redirect.", url));
									connection = null;
								}
							}
						} else {
							System.err.println("HttpServer returned an error code ("
									+ urlString
									+ "): "
									+ responseCode);
							connection = null;
						}
					}
					if (connection != null) {
						is = connection.getInputStream();
						if (retryCount > 1) {
							System.err.println("Retry successful");
						}
					}
				} catch (final SocketTimeoutException e) {
					System.err.println("Timeout (" + urlString + "): " + " "
							+ e.toString());
				}
				myTimeout = myTimeout * 2;
				if (!tryVeryHard || (retryCount > 3)) {
					break;
				}
			}
		} catch (final Exception e) {
			System.err.println("Error connecting to http-Server (" + urlString
					+ "): ");
			e.printStackTrace(System.err);
		}
		return;
	}

	/**
	 * Determine if a repsonse code is a redirect
	 *
	 * @param responseCode
	 * @return
	 */
	private boolean isRedirect(int responseCode) {
		return responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER;
	}

	/**
	 * Return an InputStream to read the requested file from. You have to close
	 * it using
	 *
	 * @see #close
	 *
	 * @return InputStream or null on error.
	 */
	public InputStream getInputStream() {
		openInputStream();
		return is;
	}

	/**
	 * Closes the connection and associated streams.
	 */
	void close() {
		if (is != null) {
			try {
				is.close();
			} catch (final IOException e) {
				System.err.println(e);
				e.printStackTrace(System.err);
			}
		}
		if (connection != null) {
			connection.disconnect();
		}
	}

	/**
	 * fetches the first line of a file using http and closes the connection
	 * automatically.
	 *
	 * @return the first line
	 */
	public String fetchFirstLine() {
		String line = null;
		try {
			openInputStream();
			if (is == null) {
				return null;
			}
			final BufferedReader br = new BufferedReader(new InputStreamReader(is));
			line = br.readLine();
			br.close();
			connection.disconnect();
		} catch (final Exception e) {
			System.err.println("Error connecting to http-Server: ");
			e.printStackTrace(System.err);
		}
		return line;
	}

	/**
	 * fetches a file using http as Properties object and closes the connection
	 * automatically.
	 *
	 * @return the first line
	 */
	Properties fetchProperties() {
		Properties prop = null;
		openInputStream();
		if (is == null) {
			return prop;
		}
		try {
			prop = new Properties();
			prop.load(is);
		} catch (final IOException e) {
			System.err.println(e);
			e.printStackTrace(System.err);
		}
		this.close();
		return prop;
	}

	/**
	 * Fetches a file from the HTTP-Server and stores it on disk.
	 *
	 * @param filename
	 *            name of the file to write
	 * @return true on success, false otherwise
	 */
	boolean fetchFile(final String filename) {
		boolean res = false;

		openInputStream();
		if (is == null) {
			return res;
		}

		try {
			final BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(filename));
			copyStream(is, os);
			connection.disconnect();
			res = true;
		} catch (final Exception e) {
			res = false;
			System.err.println(e);
			e.printStackTrace(System.err);
		}
		return res;
	}

	/**
	 * Copies data from an inputStream to an outputStream and closes both
	 * streams after work.
	 *
	 * @param inputStream
	 *            stream to read from
	 * @param outputStream
	 *            stream to write to
	 * @throws IOException
	 *             on an input/output error
	 */
	private void copyStream(final InputStream inputStream, final OutputStream outputStream)
			throws IOException {
		final byte[] buffer = new byte[10240];
		int length = inputStream.read(buffer);
		int byteCounter = length;
		while (length > -1) {
			outputStream.write(buffer, 0, length);
			length = inputStream.read(buffer);
			if (length > 0) {
				byteCounter = byteCounter + length;
				if (progressListener != null) {
					progressListener.onDownloading(byteCounter);
				}
			}
		}
		inputStream.close();
		outputStream.close();
		if (progressListener != null) {
			progressListener.onDownloadCompleted(byteCounter);
		}
	}
}
