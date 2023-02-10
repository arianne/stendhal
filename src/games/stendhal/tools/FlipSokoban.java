/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.google.common.base.Charsets;

/**
 * flips the sokoban.txt file
 *
 * @author hendrik
 */
public class FlipSokoban {

	/**
	 * reverses each line in a sokoban board; without reversing comments.
	 *
	 * @param reader input
	 * @param out out
	 * @throws IOException in case of an input/output error
	 */
	public void flip(BufferedReader reader, PrintStream out) throws IOException {
		String line = reader.readLine();
		while (line != null) {
			if (!line.startsWith("-")) {
				line = new StringBuilder(line).reverse().toString().replace('>', 'G').replace('<', '>').replace('G', '<');
			}
			out.println(line);

			line = reader.readLine();
		}
	}

	/**
	 * reverses each line in a sokoban board; without reversing comments.
	 *
	 * @param args ignored
	 * @throws IOException in case of an input/output error
	 */
	public static void main(String[] args) throws IOException {
		InputStream is = FlipSokoban.class.getClassLoader().getResourceAsStream("games/stendhal/server/entity/mapstuff/game/sokoban.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
		try {
			new FlipSokoban().flip(reader, System.out);
		} finally {
			reader.close();
		}
	}

}
