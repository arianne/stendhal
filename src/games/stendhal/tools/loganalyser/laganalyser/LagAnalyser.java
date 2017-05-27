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

import java.io.IOException;

/**
 * analyses turn overflows
 *
 * @author hendrik
 */
public class LagAnalyser {

	/**
	 * analyses the logfile for turn overflows and generates a colored html file
	 *
	 * @param inputFileName
	 * @param outputFileName
	 * @throws IOException
	 */
	private void generateHTMLReport(String inputFileName, String outputFileName) throws IOException {
		LagReader reader = new LagReader(inputFileName);
		LagHTMLWriter writer = new LagHTMLWriter(outputFileName);

		writer.writeHeader();
		int[] times = reader.readTurnOverflowRelative();
		while (times != null) {
			writer.writeTurnOverflows(times);
			times = reader.readTurnOverflowRelative();
		}
		writer.writeFooter();

		reader.close();
		writer.close();
	}

	/**
	 * main method
	 *
	 * @param args inputfile, outputfile
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new LagAnalyser().generateHTMLReport(args[0], args[1]);
	}

}
