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
package games.stendhal.server.maps.orril.dungeon;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public class DarkElfNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildTunnelArea(zone);
	}

	private void buildTunnelArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Waerryna") {
				// name means deep and hidden hired mercenary according to http://www.angelfire.com/rpg2/vortexshadow/drownames.html
			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						String reply = "If you're going to the filthy rat city, don't come past me. ";
						if (player.getLevel() < 60) {
							reply += " In fact, just don't come this way at all, you wouldn't survive the mighty dark elves... ";
						} else {
							reply += " This passage east leads to the drow tunnels.";
						}
						raiser.say(reply);
					}
				});
				addJob("I'm keeping an eye on these rats. Us dark elves don't want the rat men interfering in our business.");
				addHelp("If you seek to kill some repulsive rat men, follow that wiggling path through to another huge cavern. Cross the cavern, go through the skull statues and you will find the rat city. Follow the pathetic corpses and you'll know you're on the right path.");
				addQuest("If you want #help ... just say.");
				addGoodbye("So long!");
			}
		};

		npc.addInitChatMessage(null, new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("WaerrynaFirstChat")) {
					player.setQuest("WaerrynaFirstChat", "done");
					player.addXP(300);
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});
		npc.setDescription("You see the powerful drow Waerryna. Do not cross her.");
		npc.setEntityClass("blackwizardpriestnpc");
		npc.setPosition(49, 105);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(25);
		zone.add(npc);
	}
}
