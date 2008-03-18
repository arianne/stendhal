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

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPopupMenu;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JMenuItem;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the panel where the character can be outfittet.
 * 
 * @author mtotz
 */
public class Buddies extends WtPanel {
	private StendhalUI ui;

	// private StendhalClient client;

	private Sprite online;

	private Sprite offline;

	private LinkedList<String> buddies;

	/** Creates a new instance of Buddies. */
	public Buddies(StendhalUI ui) {
		super("buddies", ui.getWidth() - 132, 265, 132, 200);

		this.ui = ui;

		setTitleBar(true);
		setFrame(true);
		setMovable(true);
		setMinimizeable(true);
		SpriteStore st = SpriteStore.get();
		online = st.getSprite("data/gui/buddy_online.png");
		offline = st.getSprite("data/gui/buddy_offline.png");

		buddies = new LinkedList<String>();
	}

	/** we're using the window manager. */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	@Override
	public synchronized boolean onMouseRightClick(Point p) {
		int i = ((int) p.getY() - 2) / 20 - 1;
		if ((i < buddies.size()) && (i >= 0)) {

			/**
			 * don't know if this is the right way to find out if a player is
			 * online, but it works :)
			 */
			boolean isOnline = false;
			RPObject object = ui.getClient().getPlayer();
			if (object != null) {
				RPSlot slot = object.getSlot("!buddy");
				RPObject buddy = slot.getFirst();
				String buddyName = buddies.get(i);
				if (buddy.has("_" + buddyName)
						&& (buddy.getInt("_" + buddyName) != 0)) {
					isOnline = true;
				}
			}

			StyledJPopupMenu menu = new StyledJPopupMenu(
					WoodStyle.getInstance(), buddies.get(i));

			ActionListener listener = new ActionSelectedCB(buddies.get(i));

			JMenuItem mi;

			if (isOnline) {
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
		if ("talk".equals(command)) {
			// Compatibility to grandfathered accounts with a ' '
			// New accounts cannot contain a space anymore.
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/tell " + buddieName + " ");
		} else if ("leave-message".equals(command)) {
			// Compatibility to grandfathered accounts with a ' '
			// New accounts cannot contain a space anymore.
			if (buddieName.indexOf(' ') > -1) {
				buddieName = "'" + buddieName + "'";
			}

			ui.setChatLine("/msg postman tell " + buddieName + " ");
		} else if ("where".equals(command)) {
			RPAction where = new RPAction();
			where.put("type", "where");
			where.put("target", buddieName);
			ui.getClient().send(where);
		} else if ("remove".equals(command)) {
			RPAction where = new RPAction();
			where.put("type", "removebuddy");
			where.put("target", buddieName);
			ui.getClient().send(where);
		}
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(Graphics2D g) {
		super.drawContent(g);

		int i = 0;
		RPObject object = ui.getClient().getPlayer();

		if (object != null) {
			RPSlot slot = object.getSlot("!buddy");
			RPObject buddy = slot.getFirst();
			buddies.clear();

			for (String key : buddy) {
				if (key.startsWith("_")) {
					buddies.add(key.substring(1));

					if (buddy.getInt(key) == 0) {
						g.setColor(Color.RED);
						offline.draw(g, 3, 2 + i * 20);
						g.drawString(key.substring(1), 24, 16 + i * 20);
					} else {
						g.setColor(Color.GREEN);
						online.draw(g, 3, 2 + i * 20);
						g.drawString(key.substring(1), 24, 16 + i * 20);
					}

					i++;
				}
			}
		}

		resizeToFitClientArea(132, i * 20 + 3);
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
		protected String buddy;

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
