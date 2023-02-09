/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityoutside;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;


public class FletcherNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC fletcher = new SpeakerNPC("Fletcher");
		fletcher.setEntityClass("rangernpc");

		fletcher.addGreeting();
		fletcher.addGoodbye();
		fletcher.addOffer("I can #soak arrows in poison.");

		fletcher.setPosition(20, 99);

		final Map<String, Integer> required = new HashMap<String, Integer>() {{
			put("wooden arrow", 10);
			put("poison", 1);
			put("money", 500);
		}};
		new ProducerAdder().addProducer(
				fletcher,
				new ProducerBehaviour(
						"fletcher_soak_arrows",
						"soak",
						"poison arrow",
						10,
						required,
						// 1 minute per 10 arrows
						60),
				"What can I #offer you today?");

		return fletcher;
	}
}
