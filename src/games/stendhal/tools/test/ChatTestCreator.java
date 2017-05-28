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
package games.stendhal.tools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * This program creates a simple NPC chat test based on a chat log copy&pasted
 * from the the client chat log window.
 *
 * @author hendrik
 */
public class ChatTestCreator {
	private final BufferedReader br;
	private final JavaWriter writer;
	private String lastPlayerText = "";

	public ChatTestCreator(final BufferedReader br, final PrintStream out) {
		this.br = br;
		this.writer = new JavaWriter(out);
	}

	private void convert() throws IOException {
		writer.header();
		String line = br.readLine();
		while (line != null) {
			handleLine(line);
			line = br.readLine();
		}
		writer.footer();
	}

	private void handleLine(final String line) {
		final LineAnalyser analyser = new LineAnalyser(line);

		if (analyser.isEmpty()) {
			writer.emptyLine();
		} else if (analyser.isComment()) {
			writer.comment(analyser.getText());
		} else if (analyser.isPlayerSpeaking()) {
			lastPlayerText = analyser.getText();
			writer.player(lastPlayerText);
		} else if (analyser.isNPCSpeaking()) {
			writer.npc(analyser.getText());

			if (lastPlayerText.equals("bye")) {
				writer.emptyLine();
			}
		} else {
			writer.comment(line);
		}
	}

	/**
	 * Converts a chat log into a test case.
	 *
	 * @param args
	 *            chatlog.txt [test.java]
	 * @throws IOException
	 *             in case of an input/output error
	 */
	public static void main(final String[] args) throws IOException {
		if ((args.length < 1) || (args.length > 2)) {
			System.err.println("java " + ChatTestCreator.class.getName()
					+ " chatlog.txt [chatlogtest.java]");
			System.exit(1);
		}

		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
		PrintStream out = System.out;
		if (args.length > 1) {
			out = new PrintStream(new FileOutputStream(args[1]));
		}

		final ChatTestCreator ctt = new ChatTestCreator(br, out);
		ctt.convert();

		br.close();
		out.close();
	}

}
