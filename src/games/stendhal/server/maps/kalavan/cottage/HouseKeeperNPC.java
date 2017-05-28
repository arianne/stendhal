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
package games.stendhal.server.maps.kalavan.cottage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * NPC who makes tea.
 *
 * @author kymara
 */
public class HouseKeeperNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Granny Graham") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 4));
				nodes.add(new Node(12, 4));
				nodes.add(new Node(12, 6));
				nodes.add(new Node(4, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm the housekeeper here. I can #brew you a nice cup of #tea, if you like.");
				addHelp("I find a lovely cup of #tea sorts out all my problems.");
				addOffer("I will #brew you a hot cup of #tea, if you like.");
				addQuest("I have such a headache and little Annie shrieking every time she goes down the slide doesn't help. Maybe you could give her something to keep her occupied? ... like a gag ...");
				addGoodbye("Bye now.");

				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("milk", 1);
				requiredResources.put("honey", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour("granny_brew_tea",
						"brew", "tea", requiredResources, 3 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hello, dear.");
				addReply("milk",
		        		"Well my dear, I expect you can get milk from a farm.");
				addReply("honey",
				        "Don't you know the beekeeper of Fado Forest?");
				addReply("tea",
				        "It's the very best drink of all. I sweeten mine with #honey. Just ask if you'd like a #brew.");
			}
		};
		npc.setDescription("You see old Granny Graham, shuffling around her kitchen and muttering to herself.");
		npc.setEntityClass("granmanpc");
		npc.setDirection(Direction.RIGHT);
		npc.setPosition(4, 4);
		npc.initHP(100);
		zone.add(npc);
	}
}
