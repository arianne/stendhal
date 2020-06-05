/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.dbcommand.BanAccountCommand;

/**
 * Bans an account
 *
 * @author hendrik
 */
public class BanAction extends AdministrationAction {

	@Override
	protected void perform(final Player player, final RPAction action) {
		if (action.has("target")) {
			String bannedName = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			int hours = 1;

			try {
				hours = Integer.parseInt(action.get("hours"));
			} catch (final NumberFormatException e) {
				player.sendPrivateText(NotificationType.ERROR, "Please ban for a whole number of hours, or -1 hours for a permanent ban. Shorter times than 1 hour can use /jail.");
				return;
			}

			String sender = player.getName();
			if (action.has("sender") && (player.getName().equals("postman"))) {
				sender = action.get("sender");
			}


			try {
				// parse expire
				Timestamp expire = null;
				String expireStr = "end of time";
				if (hours > 0) {
					Calendar date = new GregorianCalendar();
					date.add(Calendar.HOUR, hours);
					expire = new Timestamp(date.getTimeInMillis());
					expireStr = expire.toString();
				}

				// look up username
				String username = DAORegister.get().get(CharacterDAO.class).getAccountName(bannedName);
				if (username == null) {
					player.sendPrivateText(NotificationType.ERROR, "No such character");
					return;
				}
				DBCommandQueue.get().enqueue(new BanAccountCommand(username, reason, expire));
				player.sendPrivateText("You have banned account " + username + " (character: " + bannedName + ") until " + expireStr + " for: " + reason);

				// logging
				logger.info(sender + " has banned  account " + username + " (character: " + bannedName + ") until " + expireStr + " for: " + reason);
				new GameEvent(sender, "ban",  bannedName, expireStr, reason).raise();

				SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
						sender + " banned account " + username + " (character: " + bannedName + ") until " + expireStr
						+ ". Reason: " + reason	+ ".");
				logoutAccount(username);
			} catch (SQLException e) {
				logger.error("Error while trying to ban user", e);
			}
		}
	}

	private void logoutAccount(String username) {
		List<String> characters = new LinkedList<String>();
		final PlayerEntryContainer playerContainer = PlayerEntryContainer.getContainer();
		for (PlayerEntry entry : playerContainer) {
			if (username.equalsIgnoreCase(entry.username)) {
				characters.add(entry.character);
			}
		}

		for (String character : characters) {
			final Player player = SingletonRepository.getRuleProcessor().getPlayer(character);
			if (player != null) {
				SingletonRepository.getRuleProcessor().getRPManager().disconnectPlayer(player);
			}
		}
	}

	/**
	 * registers the ban action
	 */
	public static void register() {
		CommandCenter.register("ban", new BanAction(), 1000);
	}
}
