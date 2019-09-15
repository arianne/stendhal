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
package games.stendhal.server.maps.deniran.cityinterior.bakery;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A woman who bakes bread for players.
 *
 * @author daniel / kymara
 */
public class ShopAssistantNPC implements ZoneConfigurator  {


	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Christina") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Hello and welcome to Deniran Bakery.");
				addJob("I'm the shop assistant at Deniran Bakery.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat.");
				addGoodbye();
				addOffer("I'd like to sell you §bread, but for today all our remaining gooods have been reserved by the army.");

				/*
				// Christina bakes bread if you bring her supplies from the port.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", 2);

				final ProducerBehaviour behaviour = new ProducerBehaviour("christina_bake_bread",
						"bake", "bread", requiredResources, 10 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");

				addOffer("Psss. I am not allowed to sell you any §bread officially because it is all needed by the army."); // TODO
				*/


			}};
			npc.setPosition(24, 4);
			npc.setDirection(Direction.DOWN);
			npc.setEntityClass("housewifenpc");
			npc.setDescription("You see Christina. She has a beautiful smell.");
			zone.add(npc);
	}
}
