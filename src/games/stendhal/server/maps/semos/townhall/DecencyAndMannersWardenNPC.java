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
package games.stendhal.server.maps.semos.townhall;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class DecencyAndMannersWardenNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosVillageBench(zone);
	}

	private void buildSemosVillageBench(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ketteh Wehoh") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addHelp("I am the town Decency and Manners Warden. I can advise you on how to conduct yourself in many ways; like not wandering around naked, for instance. I can tell you how to #colour your #outfit, too.");
				addReply(Arrays.asList("outfit", "colour", "outfit colouring"),
						"You can choose your favourite colour for your outfit. Choose \'Set Outfit\' after right clicking on yourself and colour your hair and dress as you want!");
				addJob("My job is to maintain a civilized level of behaviour in Semos. I know the protocol for every situation, AND all the ways of handling it wrong. Well, sometimes I get confused on whether to use a spoon or a fork; but then, nobody really uses cutlery in Semos anyway.");
				addQuest("The only task I have for you is to behave nicely towards others.");
				addGoodbye();
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				super.onGoodbye(player);
				setDirection(Direction.DOWN);
			}
		};

		npc.setDescription("Ketteh Wehoh is the town Decency and Manners Warden who stands in the Townhall of Semos.");
		npc.setEntityClass("elegantladynpc");

		npc.setDirection(Direction.RIGHT);
		npc.setPosition(13, 38);

		npc.initHP(100);
		zone.add(npc);
	}
}
