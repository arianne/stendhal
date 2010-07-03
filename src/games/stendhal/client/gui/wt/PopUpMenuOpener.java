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
import java.util.HashMap;
import java.util.Map;
/**
 *
 * 
 * @author kymara
 */
public class PopUpMenuOpener implements WtClickListener{
	
	WtPanel panel;
	WtButton button;
	String group;
	
	private static Map<String, String[]> groupsAndCommands;
	
	private static void initialize() {
		// TODO: Map pretty command with spaces to display into one words command which work as a slash action?
		groupsAndCommands = new HashMap<String, String[]>();
		groupsAndCommands.put("help", new String[] {"Help", "Manual", "FAQ", "Rules", "Atlas"});
		groupsAndCommands.put("accountcontrol", new String[] {"ChangePassword", "Merge", "LoginHistory"});
		groupsAndCommands.put("settings", new String[] {"Mute", "Clickmode"});
	// TODO: before adding rp section, put listproducers in the slash action repository...
	//	groupsAndCommands.put("rp", new String[] {"HallOfFame", "ListProducers"});
	//	groupsAndCommands.put("contribute", new String[] {"ReportBug", "RequestFeature", "Chat"});
	}
	
	public PopUpMenuOpener(WtPanel panel, WtButton button, String group){
		this.panel = panel;
		this.button = button;
		this.group = group;
		initialize();
	}
	
	
	/**
	 * the panel has been clicked.
	 * 
	 * @param name
	 *            name of the panel. Note that the panels name does not need to
	 *            be unique. We are using the name to also define the group of commands.
	 * @param point
	 *            coordinate of the clicked point within the clicked panel
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	
	public void onClick(String name, Point point, IGameScreen gameScreen) {
		final ButtonCommandList list = new ButtonCommandList(name, groupsAndCommands.get(name));
		panel.setContextMenu(list);
		list.setVisible(true);
		button.setPressed(false);
	}

}
