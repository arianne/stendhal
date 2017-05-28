/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;

import games.stendhal.common.CRC;
import games.stendhal.common.IO;
import marauroa.common.crypto.Hash;
import marauroa.common.net.message.TransferContent;

/**
 * Manages a cache for content files such as zone data transmitted by the server
 */
class Cache {
	private static Logger logger = Logger.getLogger(Cache.class);

	/**
	 * Inits the cache.
	 */
	protected void init() {
		try {

			// Create file object
			File file = new File(stendhal.getGameFolder());
			if (!file.exists() && !file.mkdirs()) {
				logger.error("Can't create " + file.getAbsolutePath() + " folder");
			} else if (file.exists() && file.isFile()) {
				if (!file.delete() || !file.mkdirs()) {
					logger.error("Can't removing file " + file.getAbsolutePath() + " and creating a folder instead.");
				}
			}

			file = new File(stendhal.getGameFolder() + "cache");
			if (!file.exists() && !file.mkdir()) {
				logger.error("Can't create " + file.getAbsolutePath() + " folder");
			}
		} catch (final RuntimeException e) {
			logger.error("cannot create cach folder", e);
		}
	}

	/**
	 * Gets an item from cache.
	 *
	 * @param item
	 *            key
	 * @return InputStream or null if not in cache
	 */
	protected InputStream getItem(final TransferContent item) {
		if (item.name.indexOf("..") > -1) {
			logger.error("Cannot get item from cache because .. is not allowed in name " + item.name);
			return null;
		}
		String filename = getFilename(item.name);
		byte[] data = IO.readFileContent(filename);
		if (data == null) {
			return null;
		}

		// Check hash, if provided by the server
		byte[] expectedHash = item.getTransmittedHash();
		if (expectedHash != null) {
			byte[] actualHash = Hash.hash(data);
			if (Arrays.equals(expectedHash, actualHash)) {
				return new ByteArrayInputStream(data);
			} else {
				return null;
			}
		}

		// Otherwise check CRC for Stendhal up to 0.97
		int expectedCRC = item.timestamp;
		int actualCRC = CRC.cmpCRC(data);

		if (expectedCRC == actualCRC) {
			return new ByteArrayInputStream(data);
		}
		return null;
	}



	/**
	 * Stores an item in cache.
	 *
	 * @param item
	 *            key
	 * @param data
	 *            data
	 */
	protected void store(final TransferContent item, final byte[] data) {
		try {
			if (item.name.indexOf("..") > -1) {
				logger.error("Cannot store item to cache because .. is not allowed in name " + item.name);
				return;
			}
			String filename = stendhal.getGameFolder() + "cache/" + item.name;
			OutputStream os = new FileOutputStream(filename);
			try {
				os.write(data);
			} finally {
				os.close();
			}

			logger.debug("Content " + item.name + " cached now.");
		} catch (IOException e) {
			logger.error("store", e);
		}
	}

	/**
	 * gets the filename
	 *
	 * @param name name
	 * @return filename
	 */
	String getFilename(String name) {
		if (name.indexOf("..") > -1) {
			logger.error("Cannot access item in cache because .. is not allowed in name " + name);
			return null;
		}
		return stendhal.getGameFolder() + "cache/" + name;
	}
}
