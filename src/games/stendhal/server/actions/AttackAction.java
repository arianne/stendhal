/* $Id$ */
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

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class AttackAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(AttackAction.class);

	public static void register() {
		StendhalRPRuleProcessor.register("attack", new AttackAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		Log4J.startMethod(logger, "attack");
		if (action.has("target")) {
			int targetObject = action.getInt("target");

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player
					.getID());
			RPObject.ID targetid = new RPObject.ID(targetObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);

				if (object instanceof RPEntity) // Disabled Player
				{
					RPEntity entity = (RPEntity) object;

					if (!player.equals(entity)) {
						// Disable attacking NPCS.
						// Just make sure no creature is instanceof
						// SpeakerNPC...
						if (entity instanceof SpeakerNPC) {
							logger.info("REJECTED. " + player.getName()
									+ " is attacking " + entity.getName());
							return;
						}

						// Enabled PVP
						if (entity instanceof Player || entity instanceof Sheep) {
							if (zone.isInProtectionArea(entity)) {
								logger.info("REJECTED. " + entity.getName()
										+ " is in a protection zone");
								String name = entity.getName();
								if (entity instanceof Sheep) {
									Player owner = ((Sheep) entity).getOwner();
									if (name != null) {
										name = Grammar.suffix_s(owner.getName()) + " sheep";
									} else {
										name = "that sheep";
									}
								}
								player.sendPrivateText("The powerful protective aura in this place prevents you from attacking "
										       + name
										       + ".");
								return;
							}

							logger.info(player.getName() + " is attacking "
									+ entity.getName());
						}

						StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "attack", entity
								.getName());

						player.attack(entity);
						player.faceTo(entity);
						player.notifyWorldAboutChanges();
						player.applyClientDirection(false);
					}
				}
			}
		}

		Log4J.finishMethod(logger, "attack");
	}
}
