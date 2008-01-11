/* $Id$ */

/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * MapConverter.java
 *
 * Created on 13. Oktober 2005, 18:24
 *
 */
package games.stendhal.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import tiled.core.Map;
import tiled.io.xml.XMLMapTransformer;

/**
 * Fix maps by loading and saving thems.
 * 
 * @author mtotz, miguel
 */
public class MapUpdater extends Task {
	/** list of *.tmx files to convert. */
	private List<FileSet> filesets = new ArrayList<FileSet>();

	/** Converts the map files. */
	public void convert(String tmxFile) throws Exception {
		File file = new File(tmxFile);

		String filename = file.getAbsolutePath();
		Map map = new XMLMapTransformer().readMap(filename);
		new tiled.io.xml.XMLMapWriter().writeMap(map, filename);
	}

	/**
	 * Adds a set of files to copy.
	 * 
	 * @param set
	 *            a set of files to copy
	 */
	public void addFileset(FileSet set) {
		filesets.add(set);
	}

	/**
	 * ants execute method.
	 */
	@Override
	public void execute() {
		try {
			for (FileSet fileset : filesets) {
				DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				String[] includedFiles = ds.getIncludedFiles();
				for (String filename : includedFiles) {
					System.out.println(ds.getBasedir().getAbsolutePath()
							+ File.separator + filename);
					convert(ds.getBasedir().getAbsolutePath() + File.separator
							+ filename);
				}
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage: java games.stendhal.tools.MapConverter <tmx file>");
			return;
		}

		// do the job
		MapUpdater converter = new MapUpdater();
		converter.convert(args[0]);
	}

}
