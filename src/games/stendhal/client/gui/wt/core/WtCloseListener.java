/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
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
 * CloseListener.java
 *
 * Created on 24. Oktober 2005, 19:49
 */

package games.stendhal.client.gui.wt.core;

/**
 * Callback for closing a panel.
 * 
 * @author mtotz
 */
public interface WtCloseListener {

	/**
	 * The panel has been closed.
	 * 
	 * @param name
	 *            name of the panel. Note that the panels name does not need to
	 *            be unique
	 */
	void onClose(String name);

}
