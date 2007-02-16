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
 * Buddies.java
 *
 * Created on 19. Oktober 2005, 21:06
 */
package games.stendhal.client.gui.wt;
import games.stendhal.client.*;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtList;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;
/**
 * This is the panel where the character can be outfittet.
 * 
 * @author mtotz
 */
public class Buddies extends WtPanel {
	private Sprite online;
	private Sprite offline;
	private LinkedList<String> buddies;
	/** Creates a new instance of Buddies */
	public Buddies(GameObjects gameObjects) {
		super("buddies", j2DClient.SCREEN_WIDTH - 132, 265, 132, 200);

		setTitleBar(true);
		setFrame(true);
		setMoveable(true);
		setMinimizeable(true);
		SpriteStore st = SpriteStore.get();
		online = st.getSprite("data/gui/buddy_online.png");
		offline = st.getSprite("data/gui/buddy_offline.png");

		buddies = new LinkedList<String>();
	}

	/** we're using the window manager */
	protected boolean useWindowManager() {
		return true;
	}

	public synchronized boolean onMouseRightClick(Point p) {
		int i = ((int) p.getY() - 2) / 20 - 1;
		if (i < buddies.size() && i >= 0) {

			/**
			 * don't know if this is the right way to find out
			 * if a player is online, but it works :)
			 */
			boolean isOnline = false;
			RPObject object = StendhalClient.get().getPlayer();
			if (object != null) {
				RPSlot slot = object.getSlot("!buddy");	
				RPObject buddy = slot.getFirst();
				String buddyName = buddies.get(i);
				if (buddy.has("_" + buddyName) && (buddy.getInt("_" + buddyName) != 0)) {
					isOnline = true; 
				}
			}

			StyledJPopupMenu menu =
				new StyledJPopupMenu(
					WoodStyle.getInstance(),
					buddies.get(i));

			ActionListener listener =
				new ActionSelectedCB(buddies.get(i));

			JMenuItem mi;

			if(isOnline) {
				mi = new JMenuItem("Talk");
				mi.setActionCommand("talk");
				mi.addActionListener(listener);
				menu.add(mi);

				mi = new JMenuItem("Where");
				mi.setActionCommand("where");
				mi.addActionListener(listener);
				menu.add(mi);
			} else {
				mi = new JMenuItem("Leave Message");
				mi.setActionCommand("leave-message");
				mi.addActionListener(listener);
				menu.add(mi);
			}

			mi = new JMenuItem("Remove");
			mi.setActionCommand("remove");
			mi.addActionListener(listener);
			menu.add(mi);

			setContextMenu(menu);
		}
		return true;
	}


	/**
	 * Handle a choosen popup item.
	 */
	protected void doAction(String command, String buddieName) {
		StendhalClient client = StendhalClient.get();

		if (command.equals("talk")) {
			// Compatibility to grandfathered accounts with a ' '
			// New accounts cannot contain a space anymore.
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			client.getTextLineGUI().setText(
				"/tell " + buddieName + " ");
		} else if (command.equals("leave-message")) {
			// Compatibility to grandfathered accounts with a ' '
			// New accounts cannot contain a space anymore.
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			client.getTextLineGUI().setText(
				"/msg postman tell " + buddieName + " ");
		} else if (command.equals("where")) {
			RPAction where = new RPAction();
			where.put("type", "where");
			where.put("target", buddieName);
			client.send(where);
		} else if (command.equals("remove")) {
			RPAction where = new RPAction();
			where.put("type", "removebuddy");
			where.put("target", buddieName);
			client.send(where);
		}
	}


	/** refreshes the player stats and draws them */
	public Graphics draw(Graphics g) {
		if(isClosed())
			return g;

		Graphics clientg = super.draw(g);
		
		if(!isMinimized())  {
			int i = 0;			
			RPObject object = StendhalClient.get().getPlayer();
			
			if (object != null) {				
				RPSlot slot = object.getSlot("!buddy");				
				RPObject buddy = slot.getFirst();				
				buddies.clear();			
				
				for (String key : buddy) {					
					if (key.startsWith("_")) {						
						buddies.add(key.substring(1));						
						
						if (buddy.getInt(key) == 0) {							
							clientg.setColor(Color.RED);							
							offline.draw(clientg, 3, 2 + i * 20);							
							clientg.drawString(key.substring(1), 24, 16 + i * 20);							
						} else {							
							clientg.setColor(Color.GREEN);							
							online.draw(clientg, 3, 2 + i * 20);							
							clientg.drawString(key.substring(1), 24, 16 + i * 20);							
						}						
						
						i++;						
					}					
				}				
			}
			
			resizeToFitClientArea(132, i * 20 + 3);			
		}
		
		return clientg;		
	}

	//
	//

	/**
	 * Handle action selections.
	 */
	protected class ActionSelectedCB implements ActionListener {
		/**
		 * The buddy to act on.
		 */
		protected String	buddy;


		/**
		 * Create a listener for action items.
		 *
		 *
		 */
		public ActionSelectedCB(String buddy) {
			this.buddy = buddy;
		}


		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			doAction(ev.getActionCommand(), buddy);
		}
	};
}
