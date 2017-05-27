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
package games.stendhal.client.gui.wt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import games.stendhal.client.gui.wt.core.WtPopupMenu;

/**
 * This is a command list .
 *
 * @author mtotz
 */
abstract class CommandList extends WtPopupMenu {
	private static final long serialVersionUID = -1607102841664745919L;

	/**
	 * Create a command list.
	 *
	 * @param name
	 *            The menu name (needed?).
	 * @param items
	 *            The action names.
	 */
	protected CommandList(final String name, final String[] items) {
		super(name);
		populate(items);
	}

	/**
	 * Populate the menu.
	 *
	 * @param items menu items
	 */
	private void populate(final String[] items) {
		ActionListener listener;
		Icon adminIcon;
		Icon icon;
		String label;

		listener = new ActionSelectedCB();
		adminIcon = new AdminIcon();

		for (String item : items) {
			// Comma separated list of commands, not a single command
			if (item.indexOf(',') > 0) {
				String[] sublist = item.split(",");
				populate(sublist);
				continue;
			}

			if (item.startsWith("(*)")) {
				icon = adminIcon;
				label = item.substring(3);
			} else {
				icon = null;
				label = item;
			}
			/*
			 * Deal with '|' definitions. (Coming from server side).
			 * Before the break is the user representation, after it is the
			 * usual representation of the actual command.
			 *
			 * That is, a teddy might have a menu item definition "Hug|Use".
			 */
			int breakPoint = label.indexOf('|');
			if (breakPoint >= 0) {
				label = item.substring(0, breakPoint);
				item = item.substring(breakPoint + 1);
			}

			final JMenuItem mi = createItem(label, icon);
			mi.setActionCommand(item);
			mi.addActionListener(listener);
			add(mi);
		}
	}

	/** an action has been chosen.
	 * @param command */
	protected abstract void doAction(final String command);

	/**
	 * Handle action selection.
	 */
	private class ActionSelectedCB implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent ev) {
			doAction(ev.getActionCommand());
		}
	}

	/**
	 * A pretty icon to indicate an admin option.
	 *
	 * <p>
	 * It looks something like:
	 *
	 * <pre>
	 *      :
	 *      :
	 *     ###
	 *   ::#:#::
	 *     ###
	 *      :
	 *      :
	 * </pre>
	 */
	private static class AdminIcon implements Icon {
		@Override
		public int getIconHeight() {
			return 7;
		}

		@Override
		public int getIconWidth() {
			return 7;
		}

		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			Color oldColor;

			oldColor = g.getColor();

			g.setColor(Color.yellow);
			g.drawLine(x + 3, y, x + 3, y + 6);
			g.drawLine(x, y + 3, x + 6, y + 3);

			g.setColor(Color.red);
			g.drawRect(x + 2, y + 2, 2, 2);

			g.setColor(oldColor);
		}
	}
}
