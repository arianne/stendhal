/***************************************************************************
 *                    Copyright © 2019 - Stendhal                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.apothecary_lab;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * @author AntumDeluge
 *
 */
public class ApothecaryNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Jameson") {

			@Override
			protected void createPath() {
				List<Node> nodes=new LinkedList<Node>();
				nodes.add(new Node(7,9));
				nodes.add(new Node(16,9));
				nodes.add(new Node(16,12));
				nodes.add(new Node(19,12));
				nodes.add(new Node(19,16));
				nodes.add(new Node(19,12));
				nodes.add(new Node(15,12));
				nodes.add(new Node(15,17));
				nodes.add(new Node(15,12));
				nodes.add(new Node(7,12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, welcome to my lab.");
				addJob("I used to be an #apothecary, but now I have retired.");
				addHelp("I'm sorry, but I don't think there is anything I can help you with. Perhaps my friend #Klaas could use "
						+ "your assistance.");
				addOffer("I have nothing to offer.");
				addQuest("I'm sorry, but I don't have anything for you to do at this time."); // Antivenom Ring quest not loaded
				addGoodbye("Please keep my lab a secret.");
				addReply("Apothecary", "I was head researcher of a team that worked for one of Faimouni's most powerful leaders. "
						+ "However this leader became corrupt and demanded that I use my skills to make deadly weapons of war. "
						+ "Anyway, I escaped and have been hiding out here ever since.");
				addReply("Klaas",
						"Oh yes, my good old friend. I used to travel to #Athor quite often to gather the very rare "
						+ "#kokuda herb. I got to know Klaas very well as a result.");
				addReply("Athor",
						"You mean you haven't visited Athor? It is a beautiful island. A great place to get away. But "
						+ "stay away from the cannibals' territory. If they invite you for dinner, you might never make it home.");
				/* this is a required ingredient for Antivenom Ring quest, but reply is added here because Jameson highlights
				 * keyword "kokuda" even if quest is not active
				 */
				addReply("kokuda",
						"The kokuda is an herb that can only be found inside #Athor Island's labyrinth.");
			}
		};

		// The NPC sprite from data/sprites/npc/
		npc.setEntityClass("apothecarynpc");
		// set a description for when a player does 'Look'
		npc.setDescription("You see Jameson, he steadily works away.");
		// Set the initial position to be the first node on the Path you defined above.
		npc.setPosition(7, 9);
		npc.initHP(100);

		zone.add(npc);
	}
}
