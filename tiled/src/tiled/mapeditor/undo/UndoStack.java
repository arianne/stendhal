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

package tiled.mapeditor.undo;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import tiled.mapeditor.MapEditor;
import tiled.util.TiledConfiguration;

public class UndoStack extends UndoManager {
	private static final long serialVersionUID = 1;

	/** last saved point. */
	private UndoableEdit savedAt;
	/** the mapeditor this undo stack belongs to. */
	private MapEditor mapEditor;

	public UndoStack(MapEditor mapEditor) {
		setLimit(TiledConfiguration.getInstance().getIntValue("tmx.undo.depth", 30));
		this.mapEditor = mapEditor;
	}

	public boolean isAllSaved() {
		return (editToBeUndone() == savedAt);
	}

	public void commitSave() {
		savedAt = editToBeUndone();
	}

	public String[] getEdits() {
		String[] list = new String[edits.size()];
		int i = 0;

		for (UndoableEdit e : edits) {
			list[i++] = e.getPresentationName();
		}

		return list;
	}

	/**
	 * If inProgress, inserts anEdit at indexOfNextAdd, and removes any old
	 * edits that were at indexOfNextAdd or later. The die method is called on
	 * each edit that is removed is sent, in the reverse of the order the edits
	 * were added. Updates indexOfNextAdd.
	 * 
	 * <p>
	 * If not <code>inProgress</code>, acts as a <code>CompoundEdit</code>.
	 * 
	 * @param anEdit
	 *            the edit to be added
	 * @see CompoundEdit#end
	 * @see CompoundEdit#addEdit
	 */
	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean ret = super.addEdit(anEdit);

		if (ret) {
			mapEditor.updateHistory();
		}
		return ret;
	}

	/** nice toString. */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		String[] strings = getEdits();
		buf.append("[" + super.toString());
		for (String string : strings) {
			buf.append(" " + string);
		}
		return buf.toString() + "]";
	}
}
