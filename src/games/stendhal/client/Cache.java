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

import games.stendhal.common.CRC;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import marauroa.common.crypto.Hash;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * Manages a cache for content files such as zone data transmitted by the server
 */
public class Cache {
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
		byte[] data = readFileContent(filename);
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
	 * reads a file into a byte array
	 *
	 * @param filename name of file
	 * @return byte-array or <code>null</code> in case the file cannot be read
	 */
	private static byte[] readFileContent(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			return null;
		}
		int offset = 0;
		int numRead = 0;
		int size = (int) file.length();
		byte[] res = new byte[size];

		// we need that loop because java does not garantee, 
		// that read(...) returns the complete remaining stream at once
		try {
			InputStream is = new FileInputStream(file);
			while ((offset < size) && numRead > -1) {
				numRead = is.read(res, offset, size - offset);
				offset += numRead;
			}
			is.close();
		} catch (IOException e) {
			logger.warn(e, e);
			return null;
		}
		return res;
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
			os.write(data);
			os.close();

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
	public String getFilename(String name) {
		if (name.indexOf("..") > -1) {
			logger.error("Cannot access item in cache because .. is not allowed in name " + name);
			return null;
		}
		return stendhal.getGameFolder() + "cache/" + name;
	}
}
