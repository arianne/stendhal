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
package games.stendhal.tools.loganalyser.laganalyser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author hendrik
 *
 */
public class LagReader {
	private BufferedReader br;

	/**
	 * creates a new LagReader
	 *
	 * @param inputFileName name of log file
	 * @throws FileNotFoundException in case the file is not found
	 */
	public LagReader(String inputFileName) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(inputFileName));
	}

	/**
	 * returns the relative times uesed in each step
	 *
	 * @return relative times
	 * @throws IOException in case of an input / output error
	 */
	public int[] readTurnOverflowRelative() throws IOException {
		int[] absolute = readTurnOverflowAbsolute();
		if (absolute != null) {
			return calculateRelative(absolute);
		}
		return null;
	}

	/**
	 * reads the absolute times from the file.
	 *
	 * @return array of ints
	 * @throws IOException in case of an input / output error
	 */
	public int[] readTurnOverflowAbsolute() throws IOException {
		String line = br.readLine();
		while (line != null) {
			if (line.matches(".*Turn duration overflow by.*")) {
				return splitLine(line);
			}
			line = br.readLine();
		}
		return null;
	}

	/**
	 * splits a Turn overflow line into the individual overflows
	 *
	 * @param line original line
	 * @return int array
	 */
	int[] splitLine(String line) {
		line = line.substring(line.indexOf("Turn duration overflow by"));
		line = line.substring(line.indexOf(":") + 1).trim();
		String[] tokens = line.split(" ");
		int[] res = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			res[i] = Integer.parseInt(tokens[i]);
		}
		return res;
	}

	/**
	 * calcuates the relative offset
	 *
	 * @param absolute absolute times
	 * @return relative times
	 */
	int[] calculateRelative(int[] absolute) {
		int[] res = new int[absolute.length];
		res[0] = absolute[0];
		for (int i = 1; i < res.length; i++) {
			res [i] = absolute[i] - absolute[i-1];
		}
		return res;
	}

	/**
	 * closes the input stream
	 *
	 * @throws IOException in case of an I/O error
	 */
	public void close() throws IOException {
		br.close();
	}

}
