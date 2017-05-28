/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.LISTPRODUCERS;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Lists the producers with have open tasks for the asking player.
 */
public class ListProducersAction implements ActionListener {


	public static void register() {
		CommandCenter.register(LISTPRODUCERS, new ListProducersAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		final StringBuilder st = new StringBuilder();
		st.append(SingletonRepository.getProducerRegister().listWorkingProducers(player));

		player.sendPrivateText(st.toString());
		player.notifyWorldAboutChanges();

	}

}
