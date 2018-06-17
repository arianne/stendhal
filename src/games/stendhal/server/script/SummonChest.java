/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.player.Player;

/**
 * Enables admins to create (list and remove) chests.
 *
 * @author yoriy
 */
public class SummonChest extends ScriptImpl {

	private final Map<Integer, Chest> storage = new HashMap<Integer, Chest>();

	private int chestcounter;

	/**
	 * Adds a chest.
	 *
	 * @param player
	 *            admin who put the chest
	 * @param args
	 *            zone x y
	 */
	public void add(final Player player, final List<String> args) {
		if (args.size() == 3) {

			// read zone and x,y. Use player's data as default on "-".
			final String myZone = args.get(0);
			if ("-".equals(myZone)) {
				sandbox.setZone(sandbox.getZone(player));
			} else {
				if (!sandbox.setZone(myZone)) {
					sandbox.privateText(player, "Zone not found.");
					return;
				}
			}
			int x = 0;
			if ("-".equals(args.get(1))) {
				x = player.getX();
			} else {
				x = MathHelper.parseInt(args.get(1));
			}
			int y = 0;
			if ("-".equals(args.get(2))) {
				y = player.getY();
			} else {
				y = MathHelper.parseInt(args.get(2));
			}

			final Chest chest = new Chest();
			chest.setPosition(x, y);


			// add chest to game
			sandbox.add(chest);

			// put it into our storage for later "list" or "del" commands
			chestcounter++;
			storage.put(Integer.valueOf(chestcounter), chest);
		} else {
			// syntax error, print help text
			sandbox.privateText(
					player,
					"This script creates, lists or removes chests. Syntax: \r\nSummonChest.class <zone> <x> <y>. The first 3 parameters can be \"-\".\r\nSummonChest.class list\r\nSummonChest.class del <n>");
		}
	}

	/**
	 * Removes the specified chest.
	 *
	 * @param player
	 *            admin
	 * @param args
	 *            chest number at index 1
	 */
	public void delete(final Player player, final List<String> args) {
		int i;
		try {
			i = Integer.parseInt(args.get(1));
		} catch (final NumberFormatException e) {
			sandbox.privateText(player, "Please specify a number");
			return;
		}

		final Chest chest = storage.get(Integer.valueOf(i));
		if (chest != null) {
			storage.remove(Integer.valueOf(i));
			sandbox.remove(chest);
			final StringBuilder sb = new StringBuilder();
			sb.append("Removed chest ");
			chestToString(sb, chest);
			sandbox.privateText(player, sb.toString());
		} else {
			sandbox.privateText(player, "Chest " + i + " does not exist");
		}
	}

	private void chestToString(final StringBuilder sb, final Chest chest) {
		sb.append(chest.getZone().getName());
		sb.append(" ");
		sb.append(chest.getX());
		sb.append(" ");
		sb.append(chest.getY());
		sb.append(" ");
		sb.append("\"" + chest.toString() + "\"");
	}

	/**
	 * Lists all chests.
	 *
	 * @param player
	 *            admin invoking this script
	 */
	public void list(final Player player) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Listing chests:");

		int i = 1;
		while (i <= chestcounter) {
			final Chest chest = storage.get(Integer.valueOf(i));
			if (chest != null) {
				sb.append("\r\n");
				sb.append(i);
				sb.append(". ");
				chestToString(sb, chest);
			}
			i++;
		}
		sandbox.privateText(player, sb.toString());
	}

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() == 0) {
			admin.sendPrivateText("/script SummonChest.class zone x y (the first three parameters may be \"-\"\n/script SummonChest.class list\n/script SummonChest.class del <n>");
			return;
		}

		final String temp = args.get(0);
		if ("list".equals(temp)) {
			list(admin);
		} else if ("del".equals(temp) || "delete".equals(temp) || "remove".equals(temp)) {
			delete(admin, args);
		} else {
			add(admin, args);
		}
	}

}
