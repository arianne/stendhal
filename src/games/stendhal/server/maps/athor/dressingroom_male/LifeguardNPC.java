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
package games.stendhal.server.maps.athor.dressingroom_male;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * Dressing rooms at the Athor island beach (Inside / Level 0).
 *
 * @author daniel
 */

public class LifeguardNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDressingRoom(zone);
	}

	private void buildDressingRoom(final StendhalRPZone zone) {
		final SpeakerNPC david = new SpeakerNPC("David") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			public void createDialog() {
				addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the men's dressing room.");
				addHelp("Just tell me if you want to #borrow #trunks!");
				addGoodbye("Have fun!");

				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("trunks", 5);
				final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList);
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "borrow");

				// stuff needed for the SuntanCreamForZara quest
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("arandula", 1);
				requiredResources.put("kokuda", 1);
				requiredResources.put("minor potion", 1);

				final ProducerBehaviour mixerBehaviour = new ProducerBehaviour("david_mix_cream",
						"mix", "suntan cream", requiredResources, 10 * 60);

				new ProducerAdder().addProducer(this, mixerBehaviour, "Hallo!");

				addReply(
						Arrays.asList("suntan", "cream", "suntan cream"),
				"Pam's and mine suntan cream is famous all over the island. But the way to the labyrinth entrance is blocked, so we can't get all the ingredients we need. If you bring me the things we need, I can #mix our special suntan cream for you.");

				addReply("arandula", "Arandula is a herb which is growing around Semos.");

				addReply(
						"kokuda",
				"We can't find the Kokuda herb which is growing on this island, because the entrance of the labyrinth, where you can find this herb, is blocked.");

				addReply("minor potion", "It's a small bottle full of potion. You can buy it at several places.");

			}
		};

		david.setEntityClass("lifeguardmalenpc");
		david.setDirection(Direction.RIGHT);
		david.setPosition(3, 11);
		david.initHP(100);
		david.setDescription("You see David. He is bronzed from his time on the beach.");
		zone.add(david);
	}
}
