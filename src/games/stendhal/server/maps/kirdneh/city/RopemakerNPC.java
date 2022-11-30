/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.kirdneh.city;

import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;


public class RopemakerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC giles = new SpeakerNPC("Giles");

		giles.setOutfit("body=0,head=0,eyes=0,hair=49,dress=968,hat=18");
		giles.setPosition(11, 57);
		giles.setIdleDirection(Direction.LEFT);

		giles.addGoodbye();
		giles.addJob("I am a ropemaker. My shop isn't built yet, but I can"
			+ " still #braid rope for you.");
		giles.addOffer("I can #braid rope.");
		giles.addHelp("If you need a rope I can #braid one for you.");
		giles.addReply(
			"rope",
			"My ropes are made from the finest #'horse hair'.");
		giles.addReply(
			"horse hair",
			"I believe #Karl has a supply of horse hair. But you may need to"
				+ " do something before he will sell it to you.");
		giles.addReply(
			"Karl",
			"Karl and his wife Philomena tend to the farms west of Ados.");

		// production
		final Map<String, Integer> required = new TreeMap<String, Integer>();
		required.put("money", 200);
		required.put("horse hair", 6);
		new ProducerAdder().addProducer(
			giles,
			new ProducerBehaviour(
				"ropemaker_braid_rope",
				"braid",
				"rope",
				required,
				15 * 60),
			"Hmmm... I think this spot looks good to build my shop. Oh"
				+ ", hello. How can I help you?");

		return giles;
	}
}
