/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
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

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.constants.StandardMessages;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;


/**
 * Get info or alter player commerce exchange amounts.
 */
public class CommerceExchange extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		final int argc = args.size();
		if (argc == 0) {
			StandardMessages.missingParameter(admin);
			showUsage(admin);
			return;
		}

		final String cmd = args.get(0);
		if (cmd.equals("help") || cmd.equals("!help")) {
			showUsage(admin);
			return;
		}

		if (argc < 4) {
			StandardMessages.missingParameter(admin);
			showUsage(admin);
			return;
		}

		final String pName = args.get(1);
		String eType = args.get(2).toLowerCase();
		final String npcName = args.get(3);

		// check if player is online
		final Player player = SingletonRepository.getRuleProcessor().getPlayer(pName);
		if (player == null) {
			StandardMessages.playerNotOnline(admin, pName);
			return;
		}

		if (!eType.equals("buy") && !eType.equals("sell")) {
			StandardMessages.unknownParameter(admin, eType);
			return;
		}

		final boolean buy = eType.equals("buy");

		if (cmd.equals("show")) {
			int total;
			String msg = "Total ";
			if (buy) {
				total = player.getCommerceTransactionAmount(npcName, false);
				msg += "purchases from ";
			} else {
				total = player.getCommerceTransactionAmount(npcName, true);
				msg += "sales to ";
			}
			msg += npcName + " by " + pName + ": " + String.valueOf(total);
			admin.sendPrivateText(msg);
			return;
		} else if (cmd.equals("set")) {
			if (argc < 5) {
				StandardMessages.missingParameter(admin);
				showUsage(admin);
				return;
			}

			int newTotal;
			try {
				newTotal = Integer.parseInt(args.get(4));
			} catch (final NumberFormatException e) {
				StandardMessages.paramMustBeNumber(admin, "Amount");
				return;
			}

			eType = "npc_sales";
			String msg = " amount ";
			int oldTotal;
			if (buy) {
				eType = "npc_purchases";
				msg += "bought from ";
				oldTotal = player.getCommerceTransactionAmount(npcName, false);
			} else {
				msg += "sold to ";
				oldTotal = player.getCommerceTransactionAmount(npcName, true);
			}
			msg += npcName + " from " + oldTotal + " to " + newTotal;

			player.put(eType, npcName, String.valueOf(newTotal));

			admin.sendPrivateText(NotificationType.CLIENT, "Changed" + msg
					+ " for player " + pName + ".");
			player.sendPrivateText(NotificationType.INFORMATION, "Admin "
					+ admin.getName() + " changed" + msg + ".");
			return;
		} else {
			StandardMessages.unknownCommand(admin, cmd);
			showUsage(admin);
			return;
		}
	}

	private void showUsage(final Player admin) {
		final String usage = "/script CommerceExchange.class show <player> <buy|sell> <npc>"
				+ "\n/script CommerceExchange.class set <player> <buy|sell> <npc> <amount>";
		admin.sendPrivateText(NotificationType.CLIENT, usage);
	}
}
