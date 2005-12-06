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
 *  modified for stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.undo;

import java.util.Iterator;
import javax.swing.undo.*;

import tiled.util.TiledConfiguration;


public class UndoStack extends UndoManager
{
  private static final long serialVersionUID = -1133852031461217562L;

    UndoableEdit savedAt;

    public UndoStack() {
        setLimit(TiledConfiguration.getInstance().getIntValue(
                    "tmx.undo.depth", 30));
    }

    public boolean isAllSaved() {
        return (editToBeUndone() == savedAt);
    }

    public void commitSave() {
        savedAt = editToBeUndone();	
    }

    public String[] getEdits() {
        String[] list = new String[edits.size()];		
        Iterator itr = edits.iterator();
        int i = 0;

        while (itr.hasNext()) {
            UndoableEdit e = (UndoableEdit)itr.next();
            list[i++] = e.getPresentationName();
        }

        return list;
    }
}
