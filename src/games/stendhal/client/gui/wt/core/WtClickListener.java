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
 * ClickListener.java
 *
 * Created on 24. Oktober 2005, 19:49
 */

package games.stendhal.client.gui.wt.core;

/**
 * Callback for clicking on a panel
 * 
 * @author mtotz
 */
public interface WtClickListener {
	/**
	 * the panel has been clicked
	 * 
	 * @param name
	 *            name of the panel. Note that the panels name does not need to
	 *            be unique
	 * @param true
	 *            when the button in pressed and false when it is not pressed
	 */
	public void onClick(String name, boolean pressed);

}
