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
package games.stendhal.server.entity.npc.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * WordListUpdate reads the current word list from predefined resources, writes a new updated, pretty
 * formatted list in the file "words.txt" and updates the database table "words".
 */
public final class WordListUpdate {

    public static void main(final String[] args) {
        run();
    }

    public static void run() {
        try {
            WordList.attachDatabase();

            final WordList wl = new WordList();

            // read in the current word list including comment lines
            final InputStream str = WordList.class.getResourceAsStream(WordList.WORDS_FILENAME);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(str));

            final List<String> comments = new ArrayList<String>();
            wl.read(reader, comments);
            reader.close();

            // update the hash value
            wl.calculateHash();

            // see if we can find the word list source file in the file system
            String outputPath = "src/games/stendhal/server/entity/npc/parser/" + WordList.WORDS_FILENAME;

            final File file = new File(outputPath);
            if (!file.exists()) {
                // Otherwise just write the output file into the current directory.
                outputPath = WordList.WORDS_FILENAME;
            }

            final PrintWriter writer = new PrintWriter(new FileWriter(outputPath));

            for (final String c : comments) {
                writer.println(c);
            }

            writeWordList(wl, writer);

            writer.close();

            System.out.println("The updated word list has been written to the file '" + outputPath + "'.");

            // update database entries
            wl.writeToDB();
            System.out.println("The word list has been stored into the database.");
        } catch (final IOException e) {
            e.printStackTrace();
        }
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
