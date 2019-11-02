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
 * Provides StrandedWizardNPC
 *
 * @author omero
 */
public class StrandedWizardNPC implements ZoneConfigurator {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Brosoklelo") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(72, 123));
				nodes.add(new Node(74, 124));
				nodes.add(new Node(77, 124));
				nodes.add(new Node(82, 124));
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
                        "That's my job and I have probably lost my #memory..." + " " +
                        "Again...");
				addHelp(
						"I am stranded here, I can not help you with anything..." + " " +
						"My #memory is nagging me with a name... Blasyklela!"
				);
				addOffer(
						"I am stranded here, I can not offer you anything..." + " " +
						"My #memory seems in disarray but I remember a place... Kirdneh!"
				);
				addReply(
	                    "apple",
	                    "You would not think about an apple..." + " " +
	                    "Unless you are bestowed a special apple!" + " " +
	                    "Blasyklela! Kirdneh! My #memory...",
	                    null
	            );
				addReply(
	                    "Kirdneh",
	                    "Kirdneh... A lovely place indeed..." + " " +
	                    "My #memory... A special #apple... #Kirdneh... A lovely place!",
	                    null
	            );
				addReply(
	                    "Blasyklela",
	                    "Blasyklela my beloved stepsister..." + " " +
	                    "My #memory... A special #apple... #Blasyklela... My beloved stepsister!",
	                    null
	            );

				/**
				 * additional behavior defined in AdMemoriaInPortfolio quest 
				 */
				
			}
		};
		
		// Finalize Brosoklelo
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(65,120);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see Brosoklelo... He seems somewhat confused like he had lost his memory!");
		zone.add(npc);
		return npc;
	}
}
