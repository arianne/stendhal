/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A utility for cleaning whitespace in files.
 *
 * Usage:
 * 		CleanWhitespace <dir>[ <dir> ...] <ext>[,<ext>,...]
 *
 * @param dir
 * 		Directory or list of directories to scan.
 * @param ext
 * 		Comma-separated string of filename extensions to include in scan.
 */
public class CleanWhitespace {
	/* List of root directories to be scanned */
	private final List<File> dir;
	/* File extensions to be filtered */
	private final List<String> ext;
	/* If "false", all files found are processed */
	private boolean filterExtensions = true;

	public CleanWhitespace(final List<String> dir, final List<String> ext) {
		this.dir = new ArrayList<>();
		for (final String dirname : dir) {
			this.dir.add(new File(dirname));
		}
		this.ext = ext;

		if (this.ext.contains("*")) {
			filterExtensions = false;
		}
	}

	/**
	 * The method that does the work.
	 */
	public void clean() {
		if (filterExtensions) {
			System.out.println("Cleaning file types: " + ext.toString());
		} else {
			System.out.println("Cleaning all file types (*).");
		}

		for (final File D : dir) {
			System.out.println("\nExamining files in \"" + D.toString() + "\" ...");
			for (final File filename : getFiles(D)) {
				try {
					final String textOrig = new String(Files.readAllBytes(Paths.get(filename.toString())));
					final List<String> linesOrig = Arrays.asList(textOrig.split("\n"));
					List<String> linesNew = new ArrayList<>();

					for (final String line : linesOrig) {
						linesNew.add(stripTrailing(line, Arrays.asList(' ', '\t')));
					}

					// Adds a single empty newline to end of file.
					String textNew = stripTrailing(joinList(linesNew, "\n"), '\n') + "\n";

					if (!textNew.equals(textOrig)) {
						FileWriter fw = new FileWriter(filename);
						fw.write(textNew);
						fw.close();

						System.out.println("Cleaned: " + filename.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("\nDone.");
	}

	/**
	 * Retrieves all files recursively from a directory filtered out by filename extension pattern.
	 *
	 * @param dir
	 * 		Root directory to scan.
	 * @return
	 * 		List of files matching filename extension pattern.
	 */
	private List<File> getFiles(final File dir) {
		List<File> fileList = new ArrayList<>();
		for (final File filename : dir.listFiles()) {
			if (filename.isDirectory()) {
				fileList.addAll(getFiles(filename));
			} else if (filename.isFile()) {
				// FIXME: Should filter out non-text files.
				if (filterExtensions) {
					final String fstr = filename.toString();
					if (fstr.contains(".")) {
						final String ext = fstr.substring(fstr.lastIndexOf(".") + 1);
						if (this.ext.contains(ext)) {
							fileList.add(filename);
						}
					}
				} else {
					fileList.add(filename);
				}
			}
		}

		return fileList;
	}

	/**
	 * Strips trailing characters from a string.
	 *
	 * @param st
	 * 		The string to be analyzed.
	 * @param toStrip
	 * 		The character to be stripped.
	 * @return
	 * 		Input string with trailing characters stripped if found.
	 */
	private String stripTrailing(final String st, final Character toStrip) {
		return stripTrailing(st, Arrays.asList(toStrip));
	}


	/**
	 * Strips trailing characters from a string.
	 *
	 * @param st
	 * 		The string to be analyzed.
	 * @param toStrip
	 * 		List of characters to be stripped.
	 * @return
	 * 		Input string with trailing characters stripped if found.
	 */
	private String stripTrailing(String st, final List<Character> toTrim) {
		int idx = st.length() - 1;
		if (idx >= 0) {
			while (toTrim.contains(st.charAt(idx))) {
				idx--;
				if (idx < 0) {
					break;
				}
			}
		}

		st = st.substring(0, idx + 1);

		return st;
	}

	/**
	 * Joins a list of strings into a single string.
	 *
	 * @param toJoin
	 * 		The list to be joined.
	 * @param delim
	 * 		The character(s) placed between strings.
	 * @return
	 * 		Single string.
	 */
	private String joinList(final List<String> toJoin, final String delim) {
		final int stringCount = toJoin.size();
		String joined = "";
		for (int idx = 0; idx < stringCount; idx++) {
			if (idx == stringCount - 1) {
				joined += toJoin.get(idx);
			} else {
				joined += toJoin.get(idx) + delim;
			}
		}

		return joined;
	}

	/**
	 * Entry point.
	 *
	 * @param args
	 * 		Command line arguments
	 */
	public static void main(final String[] args) {
		if (args.length < 2) {
			System.out.println("ERROR: Not enough arguments.\n");
			showUsage();
			System.exit(1);
		}

		final List<String> dir = new ArrayList<>();
		for (int idx = 0; idx < args.length - 1; idx++) {
			dir.add(args[idx]);
		}
		final List<String> ext = Arrays.asList(args[args.length - 1].split(","));

		new CleanWhitespace(dir, ext).clean();

		System.exit(0);
	}

	/**
	 * Show help information.
	 */
	private static void showUsage() {
		String st = "Description:\n\tScans files within a directory hierarchy & strips any trailing whitespace (spaces & tabs) from lines &"
				+ "\n\tmultiple trailing newlines at end of file are replaced with a single one. If file does not end with an"
				+ "\n\tempty newline a single one is appended."
				+ "\n\nUsage:\n\t" + CleanWhitespace.class.getSimpleName() + " <dir>[ <dir> ...] <ext>[,<ext>,...]"
				+ "\n\nArguments:\n\tdir\tRoot directory to scan for files. Multiple directories can by specified before the \"ext\""
				+ "\n\t\targument."
				+ "\n\text\tFilename extensions filter. Can be a comma-separated string for multiple extensions (e.g. "
				+ "\n\t\text1,ext2,...). An asterix (*) denotes that all file types should be scanned.";
		System.out.println(st);
	}
}
