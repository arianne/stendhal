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
package games.stendhal.server.maps.ados.market;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/**
 * Creates the NPCs and portals in Ados City.
 *
 * @author hendrik
 */
public class WeaponryTraderNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildAlexander(zone);
	}

	private void buildAlexander(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Alexander") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(52, 34));
				nodes.add(new Node(56, 34));
                nodes.add(new Node(56, 48));
                nodes.add(new Node(26, 48));
                nodes.add(new Node(26, 42));
                nodes.add(new Node(10, 42));
                nodes.add(new Node(10, 29));
                nodes.add(new Node(27, 29));
                nodes.add(new Node(27, 14));
                nodes.add(new Node(44, 14));
                nodes.add(new Node(44, 32));
                nodes.add(new Node(52, 32));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, may i #help you?");
				addHelp("I would glad to help you by buying your items, it would help to us both.");

				// this is a hint that one of the items Anna wants is a dress (goblin dress)
				addQuest("I have no tasks for you.");
				addJob("I am a weaponry trader. I prefer to do my work alone.");
				//addReply("offer", "Look at the blackboard to see my offers.");
				addGoodbye("Bye, come back soon.");

				final Map<String, Integer> pricelist =
					SingletonRepository.getShopList().get("buyadosarmors");

				final BuyerBehaviour behaviour = new BuyerBehaviour(pricelist);
				new BuyerAdder().addBuyer(this, behaviour, true);
			}
		};

		npc.setEntityClass("weaponrytradernpc");
		npc.setPosition(52, 34);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		npc.setSpeed(0.1);
		npc.setDescription("Alexander is a weaponry trader. He doesn't trust everyone...");
		zone.add(npc);
	}
}
