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
package games.stendhal.server.maps.semos.plains;

import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SeedSellerBehaviour;

/**
 * The miller (original name: Jenny). She mills flour for players who bring
 * grain.
 */
public class MillerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Jenny") {
			@Override
			public void createDialog() {
				addJob("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.");
				addReply("grain",
				        "There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.");
				addHelp("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.");
				addGoodbye();
				addOffer("You can #plant my seeds to grow beautiful flowers.");
				addReply("plant","Your seeds should be planted on fertile ground. Look for the brown ground just over the path from the arandula patch in semos plains over yonder. Seeds will thrive there, you can visit each day to see if your flower has grown. When it is ready, it can be picked. The area is open to everyone so there's a chance someone else will pick your flower, but luckily seeds are cheap!");
			}

			/*
			 * (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#onGoodbye(games.stendhal.server.entity.RPEntity)
			 */
			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

		};
		// Jenny mills flour if you bring her grain.
		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("grain", 5);

		final ProducerBehaviour behaviour = new ProducerBehaviour("jenny_mill_flour",
				"mill", "flour", requiredResources, 2 * 60);
		new SellerAdder().addSeller(npc, new SeedSellerBehaviour());
		new ProducerAdder().addProducer(npc, behaviour,"Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.");
		npc.setPosition(19, 39);
		npc.setDescription("You see Jenny. She is the local miller.");
		npc.setDirection(Direction.DOWN);
		npc.setEntityClass("woman_003_npc");
		zone.add(npc);
	}
}
