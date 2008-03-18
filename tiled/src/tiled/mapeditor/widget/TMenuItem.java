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

package tiled.mapeditor.widget;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * Tiled menu item extends on JMenuItem in that it allows for not accepting any
 * icon from an attached action.
 */
public class TMenuItem extends JMenuItem {
	private static final long serialVersionUID = 6065370625698852436L;

	private boolean showIcon = false;

	public TMenuItem(boolean showIcon) {
		this.showIcon = showIcon;
	}

	public TMenuItem(Action action) {
		this(action, false);
	}

	public TMenuItem(Action action, boolean showIcon) {
		this(showIcon);
		setAction(action);
	}

	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
	}

	@Override
	public void setIcon(Icon icon) {
		if (showIcon) {
			super.setIcon(icon);
		}
	}
}
