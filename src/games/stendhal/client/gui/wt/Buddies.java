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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.util.LinkedList;

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
		super("Buddies", j2DClient.SCREEN_WIDTH - 132, 265, 132, 200);
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
		String[] actions = { "Talk", "Where", "Remove" };

		int i = ((int) p.getY() - 2) / 20 - 1;

		if (i < buddies.size() && i >= 0) {
			WtList list = new WtList(buddies.get(i), actions, -100, 0, 100, 100) {
				@Override
				public void onClick(String name, Point point) {
					StendhalClient client = StendhalClient.get();

					if (name.equals("Talk")) {
						client.getTextLineGUI().setText(
								"/tell " + getName() + " ");
					} else if (name.equals("Where")) {
						RPAction where = new RPAction();
						where.put("type", "where");
						where.put("target", getName());
						client.send(where);
					} else if (name.equals("Remove")) {
						RPAction where = new RPAction();
						where.put("type", "removebuddy");
						where.put("target", getName());
						client.send(where);
					}
				}
			};
			setContextMenu(list);
		}

		return true;
	}

	/** refreshes the player stats and draws them */
	public Graphics draw(Graphics g) {
		Graphics clientg = super.draw(g);

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

		return clientg;
	}
}
