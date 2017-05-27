/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2012 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * input/output utility methods
 *
 * @author hendrik
 */
public class IO {
	private static Logger logger = Logger.getLogger(IO.class);

	/**
	 * reads a file into a byte array
	 *
	 * @param filename name of file
	 * @return byte-array or <code>null</code> in case the file cannot be read
	 */
	public static byte[] readFileContent(String filename) {
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
			try {
				while ((offset < size) && numRead > -1) {
					numRead = is.read(res, offset, size - offset);
					offset += numRead;
				}
			} finally {
				is.close();
			}
		} catch (IOException e) {
			logger.warn(e, e);
			return null;
		}
		return res;
	}
}
