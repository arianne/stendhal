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

package games.stendhal.server.maps.semos.mountain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
//import games.stendhal.server.entity.npc.action.StoreMessageAction;

/**
 * Provides Brosoklelo
 *
 * @author omero
 */
public class BefuddledSorcerorNPC implements ZoneConfigurator {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Brosoklelo") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(75, 124));
				nodes.add(new Node(79, 124));
				nodes.add(new Node(79, 122));
				nodes.add(new Node(75, 122));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting(
						"Ave");
				addGoodbye(
						"Fortvna");
				addJob(
                        "I like dueling in magical duels..." + " " +
                        "Oh... My poor #memory..." + " " +
                        "I am confused... Look, I have a purple apple!");
				addHelp(
						"I can not help you with anything..." + " " +
						"My #memory is nagging at me..." + " " +
						"All I have is a purple apple!");
				addOffer(
						"I can not offer you anything..." + " " +
						"My #memory seems in disarray..." + " " +
						"I can offer you a purple apple!");
				addReply(
	                    "Kirdneh",
	                    "My #memory... A purple #apple... #Kirdneh... Ah, that is place!",
	                    null
	            );
				addReply(
	                    "Vlamyklela",
	                    "My #memory... A purple #apple... #Vlamyklela... Ah, beloved stepsister!",
	                    null
	            );
				addReply(
	                    "apple",
	                    "You would not think about an apple..." + " " +
	                    "Unless you are bestowed a special kind of an apple!" + " " +
	                    "My #memory... Vlamyklela... Kirdneh...",
	                    null
	            );

				/**
				 * additional behavior defined in AdMemoriaInPortfolio quest
				 */
			}
		};

		// Finalize Brosoklelo
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(77,127);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see Brosoklelo... He seems somewhat confused!");
		zone.add(npc);
		return npc;
	}
}
