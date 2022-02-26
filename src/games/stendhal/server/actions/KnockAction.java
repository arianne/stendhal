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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.validator.ActionData;
import games.stendhal.server.actions.validator.ActionValidation;
import games.stendhal.server.actions.validator.ExtractEntityValidator;
import games.stendhal.server.actions.validator.ZoneNotChanged;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Knocks on a HousePortal - sends a message to all players inside the House
 * and a feedback message to the knocker.
 *
 * @author kymara
 */
public class KnockAction implements ActionListener {

	private static final Logger logger = Logger.getLogger(KnockAction.class);

	private static final ActionValidation VALIDATION = new ActionValidation();
	static {
		VALIDATION.add(new ZoneNotChanged());
		VALIDATION.add(new ExtractEntityValidator());
	}

	public static void register() {
		final KnockAction knock = new KnockAction();
		CommandCenter.register(KNOCK, knock);
	}

	@Override
	public void onAction(final Player player, final RPAction action) {
		ActionData data = new ActionData();
		if (!VALIDATION.validateAndInformPlayer(player, action, data)) {
			return;
		}

		Entity entity = data.getEntity();
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
		final String message = player.getName() + " is outside knocking on the door!";
		boolean knocked = false;

		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final List<StendhalRPZone> zList = new ArrayList<>();

		final StendhalRPZone mainZone = world.getZone(houseportal.getDestinationZone());
		if (mainZone != null) {
			zList.add(mainZone);
			for (final String zoneName: mainZone.getAssociatedZonesList()) {
				final StendhalRPZone subZone = world.getZone(zoneName);
				if (subZone != null) {
					zList.add(subZone);
				}
			}
		}

		// get the destination & associated zones - that is where to shout to
		for (final StendhalRPZone zone: zList) {
			if (zone != null) {
				knocked = true;
				for (final Player houseplayer : zone.getPlayers()) {
					houseplayer.sendPrivateText(message);
				}
			} else {
				logger.debug("Invalid zone associated with " + mainZone.getName());
			}
		}

		if (knocked) {
			player.sendPrivateText("rat a tat-tat, you knocked on the door! Hope someone is home ...");
		} else {
			// should not happen
			player.sendPrivateText("How strange, there is nothing behind this door!");
		}
	}
}
