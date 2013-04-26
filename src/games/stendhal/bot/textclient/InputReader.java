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
package games.stendhal.bot.textclient;

import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.scripting.ChatLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Reads and handles the input
 *
 * @author hendrik
 */
public class InputReader implements Runnable {
	private static Logger logger = Logger.getLogger(InputReader.class);

	@Override
	public void run() {
		SlashActionRepository.register();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				ChatLineParser.parseAndHandle(line);
				
			} catch (IOException e) {
				logger.error(e, e);
				System.exit(0);
			}
		}
	}
}
