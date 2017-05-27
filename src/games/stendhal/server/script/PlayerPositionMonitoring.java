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
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * List the players and their positions over a period of time.
 *
 * @author hendrik
 */
public class PlayerPositionMonitoring extends ScriptImpl {

	/**
	 * Listener for turn events.
	 */
	protected static class PlayerPositionListener implements TurnListener {

		// 5 10 15 30 60 120, 300
		private final int[] INTERVALS = new int[] { 5, 5, 5, 15, 30, 60, 280 };

		private final Player admin;

		private int counter;

		/**
		 * creates a new PlayerPositionListener.
		 *
		 * @param admin
		 *            the admin to notify
		 */
		protected PlayerPositionListener(final Player admin) {
			this.admin = admin;
		}

		private void list() {
			// create player list
			final StringBuilder sb = new StringBuilder(String.valueOf(counter));
			sb.append(": ");

			SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(

				new Task<Player>() {

				@Override
				public void execute(final Player player) {

					if (sb.length() > 10) {
						sb.append(", ");
					}

					sb.append(player.getTitle());
					sb.append(' ');

					final StendhalRPZone zone = player.getZone();

					if (zone != null) {
						sb.append(zone.getName());
					} else {
						sb.append("(none)");
					}

					sb.append(' ');
					sb.append(player.getX());
					sb.append(' ');
					sb.append(player.getY());



				}

			});

			admin.sendPrivateText(sb.toString());
		}

		@Override
		public void onTurnReached(final int currentTurn) {
			list();
			if (counter < INTERVALS.length) {
				SingletonRepository.getTurnNotifier().notifyInTurns(
						(int) (INTERVALS[counter] * 1000 / 300f), this);
			}
			counter++;
		}
	}

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);
		SingletonRepository.getTurnNotifier().notifyInTurns(1, new PlayerPositionListener(admin));
	}

}
