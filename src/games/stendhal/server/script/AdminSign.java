/* $Id$ */
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
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;

/**
 * Enables admins to create (list and remove) signs.
 *
 * @author hendrik
 */
public class AdminSign extends ScriptImpl {

	private final Map<Integer, Sign> storage = new HashMap<Integer, Sign>();

	private int signcounter;

	/**
	 * Adds a sign.
	 *
	 * @param player
	 *            admin who put the sign
	 * @param args
	 *            zone x y text
	 */
	public void add(final Player player, final List<String> args) {
		if (args.size() >= 3) {

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

			final games.stendhal.server.entity.mapstuff.sign.Sign sign = new games.stendhal.server.entity.mapstuff.sign.Sign();
			sign.setPosition(x, y);

			// concat text ignoring first 3 args
			final StringBuilder sb = new StringBuilder();
			for (int i = 3; i < args.size(); i++) {
				sb.append(args.get(i) + " ");
			}
			sign.setText(sb.toString().trim().replace("|", "\n"));

			// add sign to game
			sandbox.add(sign);

			// put it into our storage for later "list" or "del" commands
			signcounter++;
			storage.put(Integer.valueOf(signcounter), sign);
		} else {
			// syntax error, print help text
			sandbox.privateText(
					player,
					"This script creates, lists or removes signs. Syntax: \r\nAdminSign.class <zone> <x> <y> <text> The first 3 parameters can be \"-\".\r\nAdminSign.class list\r\nAdminSign.class del <n>");
		}
	}

	/**
	 * Removes the specified sign.
	 *
	 * @param player
	 *            admin
	 * @param args
	 *            sign number at index 1
	 */
	public void delete(final Player player, final List<String> args) {
		int i;
		try {
			i = Integer.parseInt(args.get(1));
		} catch (final NumberFormatException e) {
			sandbox.privateText(player, "Please specify a number");
			return;
		}

		final Sign sign = storage.get(Integer.valueOf(i));
		if (sign != null) {
			storage.remove(Integer.valueOf(i));
			sandbox.remove(sign);
			final StringBuilder sb = new StringBuilder();
			sb.append("Removed sign ");
			signToString(sb, sign);
			sandbox.privateText(player, sb.toString());
		} else {
			sandbox.privateText(player, "Sign " + i + " does not exist");
		}
	}

	private void signToString(final StringBuilder sb, final Sign sign) {
		sb.append(sign.getZone().getName());
		sb.append(" ");
		sb.append(sign.getX());
		sb.append(" ");
		sb.append(sign.getY());
		sb.append(" ");
		sb.append("\"" + sign.getText() + "\"");
	}

	/**
	 * Lists all signs.
	 *
	 * @param player
	 *            admin invoking this script
	 */
	public void list(final Player player) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Listing signs:");

		int i = 1;
		while (i <= signcounter) {
			final Sign sign = storage.get(Integer.valueOf(i));
			if (sign != null) {
				sb.append("\r\n");
				sb.append(i);
				sb.append(". ");
				signToString(sb, sign);
			}
			i++;
		}
		sandbox.privateText(player, sb.toString());
	}

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() == 0) {
			admin.sendPrivateText("/script AdminSign.class zone x y text (the first three parameters may be \"-\"\n/script AdminSign.class list\n/script AdminSign.class del <n>");
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
