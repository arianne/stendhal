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
package games.stendhal.server.maps.athor.ship;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

/** Factory for cargo worker on Athor Ferry. */

public class CargoWorkerNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Klaas") {

			/*@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// to the bucket
				nodes.add(new Node(24,42));
				// along the corridor
				nodes.add(new Node(24,35));
				// walk between barrels and boxes
				nodes.add(new Node(17,35));
				// to the stairs
				nodes.add(new Node(17,39));
				// walk between the barrels
				nodes.add(new Node(22,39));
				// towards the bow
				nodes.add(new Node(22,42));
				setPath(new FixedPath(nodes, true));
			}*/

			@Override
			public void createDialog() {
				addGreeting("Ahoy! Nice to see you in the cargo hold!");
				addJob("I'm taking care of the cargo. My job would be much easier without all these #rats.");
				addHelp("You could earn some money if you'd #offer me something to poison these damn #rats.");
				addReply(Arrays.asList("rat", "rats"),
				"These rats are everywhere. I wonder where they come from. I can't even kill them as fast as they come up.");

				new BuyerAdder().addBuyer(this,
						new BuyerBehaviour(SingletonRepository.getShopList().get("buypoisons")), true);

				addGoodbye("Please kill some rats on your way up!");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

			new AthorFerry.FerryListener() {


				@Override
				public void onNewFerryState(final Status status) {
					switch (status) {
					case ANCHORED_AT_MAINLAND:
					case ANCHORED_AT_ISLAND:
						npc.say("Attention: We have arrived!");
						break;

					default:
						npc.say("Attention: We have set sail!");
						break;
					}
				}
			};

			npc.setPosition(25, 38);
			npc.setEntityClass("seller2npc");
			npc.setDescription ("You see Klaas who takes care of the cargo. He hates rats!");
			npc.setDirection(Direction.DOWN);
			zone.add(npc);
	}
}
