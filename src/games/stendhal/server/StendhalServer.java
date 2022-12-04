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
package games.stendhal.server;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.ContainerGenerateINI;
import games.stendhal.server.core.engine.GenerateINI;
import games.stendhal.server.core.rp.DaylightPhase;

/**
 * Starts a Stendhal server
 *
 * @author hendrik
 */
public class StendhalServer {

	private static final Logger logger = Logger.getLogger(StendhalServer.class);

	private static String serverIni = "server.ini";
	private static boolean inContainer = false;
	private static String databasePath = "~/stendhal/database/h2db";
	private static Integer keySize = Integer.valueOf(512);

	/**
	 * parses the command line for overwriten configuration file.
	 *
	 * @param args command line parameters
	 */
	private static void parseCommandLine(String[] args) {
		int i = 0;

		while (i < args.length - 1) {
			if (args[i].equals("-c")) {
				serverIni = args[i + 1];
			}
			if (args[i].equals("-container")) {
			    inContainer = true;
			}
			if (args[i].equals("-databasePath")) {
                databasePath = args[i + 1];
            }
			if (args[i].equals("-keySize")) {
                keySize = Integer.valueOf(args[i + 1]);
            }
			i++;
		}
	}

	/**
	 * Starts a Stendhal server
	 *
	 * @param args command line arguments
	 * @throws FileNotFoundException in case the init file cannot be generated
	 */
	public static void main(String[] args) throws FileNotFoundException {
		parseCommandLine(args);
		if (!new File(serverIni).exists()) {
		    if (!inContainer) {
		        System.out.println("Welcome to your own Stendhal Server.");
		        System.out.println("");
		        System.out.println("This seems to be the very first start because we could not find a server.ini.");
		        System.out.println("So there are some simple questions for you to create it...");
		        System.out.println("");
		        GenerateINI.main(args, serverIni);
		    } else {
		        new ContainerGenerateINI(databasePath, keySize).write(serverIni);
		    }
		}
		marauroa.server.marauroad.main(args);

		// permanently sets DaylightPhase for testing purposes
		String testPhase = System.getProperty("testing.daylightphase");
		if (testPhase != null) {
			testPhase = testPhase.toLowerCase();
			for (DaylightPhase phase: DaylightPhase.values()) {
				if (phase.toString().toLowerCase().equals(testPhase)) {
					logger.info("Setting testing DaylightPhase: " + phase);

					DaylightPhase.setTestingPhase(phase);
					break;
				}
			}
		}
	}
}
