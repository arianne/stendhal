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
package games.stendhal.tools.npcparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.WordList;
import marauroa.common.io.UnicodeSupportingInputStreamReader;

/**
 * WordListUpdate reads the current word list from predefined resources, writes a new updated, pretty
 * formatted list in the file "words.txt" and updates the database table "words".
 */
public final class WordListUpdate {

    public static void main(final String[] args) {
//    	// initialise TransactionPool for DB access
//		new DatabaseFactory().initializeDatabase();

		// load word list and perform the update
		String msg = updateWordList(WordList.getInstance());

		System.out.print(msg);
    }

    public static String updateWordList(final WordList wl) {
		StringBuilder log = new StringBuilder();

        try {
            // read in the current word list including comment lines
            final InputStream str = WordList.class.getResourceAsStream(WordList.WORDS_FILENAME);
            final BufferedReader reader = new BufferedReader(new UnicodeSupportingInputStreamReader(str, "UTF-8"));
            final List<String> comments = new ArrayList<String>();

            try {
	            wl.read(reader, comments);
            } finally {
            	reader.close();
            }

            // update the hash value
//			wl.calculateHash();

            // see if we can find the word list source file in the file system
            String outputPath = "src/games/stendhal/common/parser/" + WordList.WORDS_FILENAME;

            final File file = new File(outputPath);
            if (!file.exists()) {
                // Otherwise just write the output file into the current directory.
                outputPath = WordList.WORDS_FILENAME;
            }

            final PrintWriter writer = new PrintWriter(outputPath, "UTF-8");

            for (final String c : comments) {
                writer.println(c);
            }

            writeWordList(wl, writer);

            writer.close();

            log.append("The updated word list has been written to the file '" + outputPath + "'.\n");

//        	// initialise TransactionPool if not yet ready
//    		new DatabaseFactory().initializeDatabase();
//
//    		// update database entries
//          DBWordList.writeToDB(wl);
//          log.append("The word list has been stored into the database.\n");
        } catch (final IOException e) {
        	log.append("Exception: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        return log.toString();
    }

    /**
     * Print all words sorted by known types.
     *
     * @param wl
     *            word list
     * @param writer
     */
    private static void writeWordList(final WordList wl, final PrintWriter writer) {
        writer.println();
        wl.printWordType(writer, ExpressionType.VERB);

        writer.println();
        wl.printWordType(writer, ExpressionType.OBJECT);

        writer.println();
        wl.printWordType(writer, ExpressionType.SUBJECT);

        writer.println();
        wl.printWordType(writer, ExpressionType.ADJECTIVE);

        writer.println();
        wl.printWordType(writer, ExpressionType.NUMERAL);

        writer.println();
        wl.printWordType(writer, ExpressionType.PREPOSITION);

        writer.println();
        wl.printWordType(writer, ExpressionType.QUESTION);

        writer.println();
        wl.printWordType(writer, ExpressionType.IGNORE);

        writer.println();
        wl.printWordType(writer, null);
    }

}
