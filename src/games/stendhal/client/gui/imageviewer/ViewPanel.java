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
package games.stendhal.client.gui.imageviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * an abstract base class for all ViewPanels
 *
 * @author hendrik
 */
abstract class ViewPanel extends JComponent {

	private static final long serialVersionUID = 7442185832293104642L;

	/**
	 * creates a new ViewPanel
	 */
	public ViewPanel() {
		setLayout(new BorderLayout());
		setOpaque(true);
	}

	/**
	 * prepares the view
	 *
	 * @param maxSize of the panel
	 */
	public abstract void prepareView(Dimension maxSize);
}
