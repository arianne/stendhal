/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.maps.deniran.river;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A soldier guarding the bridge
 */
public class BridgePostNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Marcellus") {
			@Override
			public void createDialog() {
				addGreeting("Hey! You stay away! You'd get yourself killed.");
				addJob("I guard the bridge. Making sure that no thoughless civilians pass and get killed on the battle field.");
				addHelp("The capital city of Deniran is to the north.");
//				addGoodbye("/me shakes his head and mutters to himself: Thoughtless civilians.");
				addGoodbye("Thoughtless civilians.");
			}
			@Override
			protected void onGoodbye(RPEntity player) {
				super.onGoodbye(player);
				setDirection(Direction.UP);
			}
		};
		npc.setDirection(Direction.UP);
		npc.setPosition(65, 25);
		npc.setEntityClass("../monsters/human/deniran_stormtrooper");
		npc.setDescription("You see a soldier guarding the bridge.");
		zone.add(npc);
	}
}
