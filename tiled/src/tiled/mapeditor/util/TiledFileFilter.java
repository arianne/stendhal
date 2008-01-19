/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileFilter;

public class TiledFileFilter extends FileFilter {
	public static final int FILTER_TMX = 1;
	public static final int FILTER_TSX = 2;
	public static final int FILTER_BOTH = 3;

	private String desc;
	private List<String> exts;

	public TiledFileFilter() {
		desc = "Tiled files";
		exts = new ArrayList<String>();
		exts.add("tmx");
		exts.add("tmx.gz");
		exts.add("tsx");
	}

	public TiledFileFilter(int filter) {
		exts = new ArrayList<String>();
		desc = "";
		if ((filter & FILTER_TMX) != 0) {
			desc = "Tiled Maps files ";
			exts.add("tmx");
			exts.add("tmx.gz");
		}
		if ((filter & FILTER_TSX) != 0) {
			desc = desc + "Tiled Tileset files";
			exts.add("tsx");
		}
	}

	public TiledFileFilter(String filter, String desc) {
		exts = new ArrayList<String>();
		this.desc = desc;
		String[] ext = filter.split(",");
		for (int i = 0; i < ext.length; i++) {
			exts.add(ext[i].substring(ext[i].indexOf('.') + 1));
		}
	}

	public void setDescription(String d) {
		desc = d;
	}

	public void addExtention(String e) {
		exts.add(e);
	}

	public boolean accept(File f) {
		if (f.isFile()) {
			if (f.getAbsolutePath().lastIndexOf('.') == -1) {
				return false;
			}

			String fileName = f.getAbsolutePath().toLowerCase();

			Iterator itr = exts.iterator();
			while (itr.hasNext()) {
				String ext = (String) itr.next();
				if (fileName.endsWith(ext)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public String getDescription() {
		String filter = "(";
		Iterator<String> itr = exts.iterator();
		while (itr.hasNext()) {
			String ext = itr.next();
			filter = filter + "*." + ext;
			if (itr.hasNext()) {
				filter = filter + ",";
			}
		}
		return desc + " " + filter + ")";
	}
}
