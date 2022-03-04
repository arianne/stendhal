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
package games.stendhal.server.maps.semos.mines;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

public class DwarfGuardianNPC implements ZoneConfigurator {
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
		buildMineArea(zone);
	}

	private void buildMineArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Phalk") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						String reply = "There is something huge there! Everyone is very nervous. ";
						if (player.getLevel() < 60) {
							reply += "You are too weak to enter there. Once you feel strong enough, push these #stones beside and enter the dark parts of the mines...";
						} else {
							reply += "Be careful! For entering the dark parts of the mines, push the #stones away which are laying infront of the entrance...";
						}
						raiser.say(reply);
					}
				});
				addReply(Arrays.asList("stone", "stones"), "You can find tons of them around Faiumoni. Some aren't that huge so you can take all of your power and push them away...but I guess anyway that a great warrior like you wouldn't have any problems with that.");
				addJob("I am a dwarf Guardian and try to abandon adventurers to their fate.");
				addHelp("Take care when you are running through the tunnels of the Semos mines. There are some strong creatures waiting! If you need a better equipment, you can ask Harold in the Semos Tavern for some offers, maybe he can help you out...");
				addGoodbye();
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.LEFT);
			}
		};

		npc.addInitChatMessage(null, new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("PhalkFirstChat")) {
					player.setQuest("PhalkFirstChat", "done");
					player.addXP(500);
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setEntityClass("../monsters/dwarf/dwarf_guardian");
		npc.setDescription("You see Phalk. He is a soldier who doesn't want to abandon adventurers to their fate.");
		npc.setPosition(118, 26);
		npc.setDirection(Direction.LEFT);
		npc.initHP(25);
		zone.add(npc);
	}
}
