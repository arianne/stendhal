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
package games.stendhal.server.maps.kotoch;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class SmithNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildKotochSmitherArea(zone);
	}

	private void buildKotochSmitherArea(final StendhalRPZone zone) {
		final SpeakerNPC smith = new SpeakerNPC("Vulcanus") {

			@Override
			// he doesn't move.
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Chairetismata! I am Vulcanus the smither.");
				addGoodbye("Farewell");
				addHelp("I may help you to get a very #special item for only a few others...");
				addJob("I used to forge weapons for the King of Faiumoni, but this was long ago, since now the way is blocked.");

				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("special"),
				        null,
				        ConversationStates.ATTENDING,
				        "Who told you that!?! *cough* Anyway, yes, I can forge a very special item for you. But you will need to complete a #quest",
				        null);
			}
		};

		smith.setDescription("You see Vulcanus. You feel a strange sensation near him.");
		smith.setEntityClass("transparentnpc");
		smith.setAlternativeImage("vulcanus");
		smith.setPosition(62, 115);
		smith.setDirection(Direction.DOWN);
		smith.initHP(100);
		zone.add(smith);
	}
}
