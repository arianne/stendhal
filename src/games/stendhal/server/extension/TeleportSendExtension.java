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
package games.stendhal.server.extension;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Stendhal TeleportSend Extenstion
 *
 * This extension adds teleportsend to the game world. there is 1 command:
 * /teleportsend [Player] [Player|NPC_Destination] which will teleport the first
 * player to the second player/npc This command is an admin command of the same
 * access level as /teleport To enable this extension, add it to the
 * server.ini file: # load StendhalServerExtension(s)
 * teleportsend=games.stendhal.server.extension.TeleportSendExtension
 * server_extension=...,teleportsend
 *
 * @author Seather
 */
public class TeleportSendExtension extends StendhalServerExtension implements
		ActionListener {

	private final String CMD_NAME = "teleportsend";

	private final String CMD_USAGE = "Usage: #/" + CMD_NAME
			+ " #<Player> #<Player|NPC_Destination>";

	private static final Logger logger = Logger.getLogger(TeleportSendExtension.class);

	public TeleportSendExtension() {
		super();
		logger.info("TeleportSendExtension starting...");
		CommandCenter.register(CMD_NAME, this, 400);

	}

	@Override
	public void init() {
		// this extension has no specific init code, everything is
		// implemented as /commands that are handled onAction
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals(CMD_NAME)) {
			onTeleportSend(player, action);
		}
	}

	private void onTeleportSend(final Player admin, final RPAction action) {

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(admin,
				CMD_NAME, true)) {
			return;
		}

		if (action.has("target") && action.has("args")) {
			// Parse Player1
			final String name1 = action.get("target");
			final Player player1 = SingletonRepository.getRuleProcessor().getPlayer(name1);
			if (player1 == null) {
				final String text = "Player \"" + name1 + "\" not found";
				admin.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			// Parse Player2 (player/npc)
			final String name2 = action.get("args");
			RPEntity player2 = SingletonRepository.getRuleProcessor().getPlayer(name2);
			if (player2 == null) {
				player2 = SingletonRepository.getNPCList().get(name2);
				if (player2 == null) {

					final String text = "Player \"" + name2 + "\" not found";
					admin.sendPrivateText(text);
					logger.debug(text);
					return;
				}
			}

			final StendhalRPZone zone = player2.getZone();
			final int x = player2.getX();
			final int y = player2.getY();

			player1.teleport(zone, x, y, null, admin);

			/*
			 * StendhalRPRuleProcessor.get().addGameEvent(admin.getName(),
			 * "teleportsend", action.get("target") + " -> " +
			 * action.get("args"), zone.getName(), Integer.toString(x),
			 * Integer.toString(y));
			 */
		} else {
			admin.sendPrivateText(CMD_USAGE);
		}
	}
}
