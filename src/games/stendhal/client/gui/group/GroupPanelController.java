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
package games.stendhal.client.gui.group;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Controller for the group information data.
 */
public class GroupPanelController {
	static GroupPanelController instance;
	
	final GroupPanel panel;
	
	/**
	 * Create a new GroupPaneController.
	 */
	private GroupPanelController() {
		panel = new GroupPanel();
	}
	
	/**
	 * Get the component showing the group information.
	 *  
	 * @return group information component
	 */
	public JComponent getComponent() {
		return panel.getComponent();
	}
	
	/**
	 * Update group information data.
	 * 
	 * @param members members of the group the player belongs to, or
	 * 	<code>null</code> if the player does not belong to any group
	 * @param leader name of the leader of the group
	 * @param lootMode looting mode of the group
	 */
	public void update(final List<String> members, final String leader, final String lootMode) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (members == null) {
					panel.showHeader("<html>You are not a member in a group.<html>");
					panel.setMembers(null);
				} else {
					panel.showHeader("<html>Looting: " + lootMode + "<p>Members:</html>");
					panel.setMembers(members);
					panel.setLeader(leader);
				}	
			}
		});
	}
	
	/**
	 * Get the GroupPaneController instance.
	 * 
	 * @return instance
	 */
	public static synchronized GroupPanelController get() {
		if (instance == null) {
			instance = new GroupPanelController();
		}
		return instance;
	}
}
