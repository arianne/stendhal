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
package games.stendhal.client.gui.wt;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.gui.wt.core.WtButton;
import games.stendhal.client.gui.wt.core.WtClickListener;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
/**
 * A pop up menu for WtButtons on WtPanels which shows a list of commands, 
 * which when clicked execute the matching slash action
 * 
 * @author kymara
 */
public class PopUpMenuOpener implements WtClickListener{
	
	WtPanel panel;
	WtButton button;
	String group;
	
	/**
	 * Stores the commands available for each group label 
	 */
	private static Map<String, String[]> groupsAndCommands;
	
	/**
	 * Set the commands available for each group label 
	 */
	private static void initialize() {
		groupsAndCommands = new HashMap<String, String[]>();
		groupsAndCommands.put("help", new String[] {"Help", "Manual", "FAQ", "Rules", "Atlas"});
		groupsAndCommands.put("accountcontrol", new String[] {"Change Password", "Merge", "Login History"});
		groupsAndCommands.put("settings", new String[] {"Mute", "Clickmode"});
		groupsAndCommands.put("rp", new String[] {"Who", "Hall Of Fame", "List Producers"});
	//	groupsAndCommands.put("contribute", new String[] {"Report Bug", "Request Feature", "Chat"});
	}
	
	/**
	 * Create a pop up menu opener.
	 * 
	 * @param panel
	 *            The WtPanel, so we can set the context.
	 * @param button
	 *            The WtButton, so we can tell it to not be pressed.
	 * @param group
	 *            The group of commands to display in the list. Same as panel name in ButtonCommandList and label in SettingsPanel.
	 */
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
