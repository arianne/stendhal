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
package games.stendhal.tools.updateprop;

import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Writes an update.properties file.
 *
 * @author hendrik
 */
public class UpdatePropertiesWriter {
	private Set<String> keys;
	private Properties prop;
	private PrintStream ps;

	/**
	 * Creates a new UpdatePropertiesWriter.
	 *
	 * @param prop properties to write
	 * @param ps   output stream
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UpdatePropertiesWriter(final Properties prop, final PrintStream ps) {
		this.prop = prop;
		this.ps = ps;
		this.keys = new TreeSet<String>((Set) prop.keySet());
	}

	/**
	 * writes the update properties.
	 */
	public void process() {
		header();
		status();
		destination();
		init();
		updateFileList();
		fileSize();
		fileSignature();
	}

	/**
	 * writes all keys starting with the specified prefix in alphabetical order.
	 *
	 * @param prefix prefix of keys
	 */
	private void writeKeysWithPrefix(String prefix) {
		for (String key : keys) {
			if (key.startsWith(prefix)) {
				ps.println(key + "=" + prop.getProperty(key));
			}
		}
		ps.println();
	}

	/**
	 * writes all keys starting with the specified prefix and not starting with the exclude prefix
	 * in alphabetical order.
	 *
	 * @param prefix prefix of keys
	 * @param exclude exclude prefix of keys
	 */
	private void writeKeysWithPrefix(String prefix, String exclude) {
		for (String key : keys) {
			if (key.startsWith(prefix) && !key.startsWith(exclude)) {
				ps.println(key + "=" + prop.getProperty(key));
			}
		}
		ps.println();
	}

	/**
	 * writes the header.
	 */
	private void header() {
		ps.println("# This file contains information required for automatic updates");
		ps.println();
	}

	/**
	 * writes the status help
	 */
	private void status() {
		ps.println("# Status of this version:");
		ps.println("#     OUTDATED: sorry, you have to redownload");
		ps.println("#     UPDATE_NEEDED:   there is an update available");
		ps.println("#     CURRENT:  good, we don't have to do anything at the moment");

		writeKeysWithPrefix("version.", "version.destination");
	}

	private void destination() {
		ps.println("# new version after update to calculate multiple updates in a row");
		writeKeysWithPrefix("version.destination.");
	}

	private void init() {
		ps.println("# files to download on first install");
		writeKeysWithPrefix("init.");
	}

	private void updateFileList() {
		ps.println("# files to download on update from this version");
		writeKeysWithPrefix("update-file-list.");
	}

	private void fileSize() {
		ps.println("# size of files");
		writeKeysWithPrefix("file-size.");
	}

	private void fileSignature() {
		ps.println("# signature of files");
		writeKeysWithPrefix("file-signature.");
	}
}
