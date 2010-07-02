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

package games.stendhal.client.gui.wt;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.gui.wt.core.WtButton;
import games.stendhal.client.gui.wt.core.WtClickListener;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Point;
/**
 *
 * 
 * @author kymara
 */
public class PopUpMenuOpener implements WtClickListener{
	
	WtPanel panel;
	WtButton button;
	
	public PopUpMenuOpener(WtPanel panel, WtButton button){
		this.panel = panel;
		this.button = button;
	}
	
	/**
	 * the panel has been clicked.
	 * 
	 * @param name
	 *            name of the panel. Note that the panels name does not need to
	 *            be unique
	 * @param point
	 *            coordinate of the clicked point within the clicked panel
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	
	public void onClick(String name, Point point, IGameScreen gameScreen) {
		final ButtonCommandList list = new ButtonCommandList("help", new String[] {"Help", "Manual", "FAQ", "Rules", "Atlas"});
		panel.setContextMenu(list);
		list.setVisible(true);
		button.setPressed(false);
	}

}
