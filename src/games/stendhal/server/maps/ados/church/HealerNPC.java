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
package games.stendhal.server.maps.ados.church;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * The healer (original name: Valo). He makes mega potions.
 */

public class HealerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Valo") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(26, 5));
				nodes.add(new Node(29, 5));
				nodes.add(new Node(29, 3));
				nodes.add(new Node(37, 3));
				nodes.add(new Node(37, 9));
				nodes.add(new Node(22, 9));
				nodes.add(new Node(22, 12));
				nodes.add(new Node(5, 12));
				nodes.add(new Node(5, 9));
				nodes.add(new Node(26, 9));
				nodes.add(new Node(26, 3));
				nodes.add(new Node(8, 3));
				nodes.add(new Node(26, 4));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addJob("Long ago I was a priest of this church. But my #ideas were not approved of by all.");
				addReply("ideas",
				"I have read many texts and learnt of strange ways. My healing powers became so strong I can now #concoct a special #'mega potion' for warriors like you.");
				addReply("giant heart",
				"Giants dwell in caves west of here. Good luck slaying those beasts ...");
				addOffer("I can #concoct a #'mega potion' for you. I will need a #'giant heart' for this.");
				addReply("mega potion", "It is a powerful elixir. If you want one, ask me to #'concoct 1 mega potion'.");
				addReply("money", "That is your own concern. We of the cloth need not scurry around to make cash.");
				addHelp("If you want to become wise like me, you should visit a library. There is much to learn and #ideas to explore.");
				addGoodbye("Fare thee well.");

				// Valo makes mega potions if you bring giant heart and money
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("money", 20);
				requiredResources.put("giant heart", 1);
				final ProducerBehaviour behaviour = new ProducerBehaviour("valo_concoct_potion",
						"concoct", "mega potion", requiredResources, 2 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Greetings, young one. I #heal and I #help.");
				// charge (1*the player level + 1) to heal
				new HealerAdder().addHealer(this, -1);
			}
		};
		npc.setEntityClass("grandadnpc");
		npc.setDescription("You see ancient and wizened Valo. He is surrounded by some kind of glowing light.");
		npc.setPosition(26, 5);
		npc.setCollisionAction(CollisionAction.STOP);
		npc.initHP(100);
		zone.add(npc);
	}
}
