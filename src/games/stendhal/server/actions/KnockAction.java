/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import static games.stendhal.common.constants.Actions.KNOCK;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

/**
 * Knocks on a HousePortal - sends a message to all players inside the House
 * and a feedback message to the knocker.
 *
 * @author kymara
 */
public class KnockAction implements ActionListener {

	public static void register() {
		final KnockAction knock = new KnockAction();
		CommandCenter.register(KNOCK, knock);
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		// evaluate the target parameter
		final Entity entity = EntityHelper.entityFromTargetName(
				action.get(TARGET), player);

		if ((entity == null) || !(entity instanceof HousePortal)) {
			// unlikely to happen since players can only see Knock on HousePortal right click menus, but you never know ...
			player.sendPrivateText("Hmm, that's not something you can knock on effectively.");
			return;
		}

		if (player.nextTo(entity)) {
			final HousePortal houseportal = (HousePortal) entity;
			knock(player, houseportal);
		} else {
			player.sendPrivateText("You can't reach to knock from here.");
		}
	}

	/**
	 * Knocks on the door.
	 * @param player who is knocking?
	 * @param houseportal HousePortal which was knocked
	 */
	private void knock(final Player player, final HousePortal houseportal) {
		String message = player.getName() + " is outside knocking on the door!";

		// get the destination zone of the portal - that is where to shout to
		final StendhalRPZone zone =  SingletonRepository.getRPWorld().getZone(houseportal.getDestinationZone());
		if (zone != null) {
			for (Player houseplayer : zone.getPlayers()) {
				houseplayer.sendPrivateText(message);
			}
			player.sendPrivateText("rat a tat-tat, you knocked on the door! Hope someone is home ...");
		} else {
			// should not happen
			player.sendPrivateText("How strange, there is nothing behind this door!");
		}
	}
}
