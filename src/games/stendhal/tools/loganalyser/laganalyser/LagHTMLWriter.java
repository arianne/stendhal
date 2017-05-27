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

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * writes a colored html based lag report
 *
 * @author hendrik
 */
public class LagHTMLWriter {
	private PrintStream ps;

	/**
	 * creates a new LagHTMLWriter
	 *
	 * @param outputFileName name of file to write
	 * @throws FileNotFoundException in case
	 */
	public LagHTMLWriter(String outputFileName) throws FileNotFoundException {
		this.ps = new PrintStream(outputFileName);
	}

	/**
	 * writes the header of the html-file (including the content header).
	 */
	public void writeHeader() {
		ps.println("<html>");
		ps.println("<head>");
		ps.println("\t<title>Lag</title>");
		ps.println("\t<style>.first {background-color: #F00} .second {background-color: #FF0}</style>");
		ps.println("</head>");
		ps.println("<body>");
		ps.println("<table border=\"1\">");
	}

	/**
	 * writes a line
	 *
	 * @param times times
	 */
	public void writeTurnOverflows(int[] times) {
		String[] cssClasses = calculateCssClasses(times);
		ps.print("<tr>");
		for (int i = 0; i < times.length; i++) {
			ps.print("<td class=" + cssClasses[i] + ">" + times[i] + "</td>");
		}
		ps.println("</tr>");
	}

	/**
	 * calculates the highest two numbers
	 *
	 * @param times numbers
	 * @return css styles
	 */
	String[] calculateCssClasses(int[] times) {
		String[] cssClasses = new String[times.length];
		int firstIdx = 0;
		int secondIdx = 0;
		int highest = 0;
		int secondHighest = 0;
		for (int i = 0; i < times.length; i++) {
			if (times[i] > highest) {
				secondIdx = firstIdx;
				firstIdx = i;
				secondHighest = highest;
				highest = times[i];
			} else if (times[i] > secondHighest) {
				secondIdx = i;
				secondHighest = times[i];
			}
		}
		cssClasses[secondIdx] = "second";
		cssClasses[firstIdx] = "first";
		return cssClasses;
	}

	/**
	 * writes the html footer.
	 */
	public void writeFooter() {
		ps.println("</table>");
		ps.println("</body>");
		ps.println("</html>");
	}

	/**
	 * closes the output stream.
	 */
	public void close() {
		ps.close();
	}

}
