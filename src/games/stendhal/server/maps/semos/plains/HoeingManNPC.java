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
package games.stendhal.server.maps.semos.plains;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Provides a Hoeing Man, hoeing farm ground north of Semos,
 * near Jenny's Mill
 *
 * @author omero
 */
public class HoeingManNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jingo Radish") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(48, 62));
				nodes.add(new Node(43, 76));
				nodes.add(new Node(43, 62));

				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Well met, wayfarer!");
				addJob(	"You see? I keep freeing the soil from weeds with my #hoe but... " +
				        "Weeds keep growing back... Of course...");
				addHelp("Take your time and check the area around... " +
						"There's a mill somewhat north and a really nice farm to the east... " +
						"Nice and rich country, you could go hunting for food!");
				addReply("hoe",
	                "Ah well, there's nothing special about my hoe... " +
	                "If you need some good farming tools like a #scythe or a #sickle... " +
	                "Visiting the nearby Semos city blacksmith shop would be a good idea!");
				addReply("scythe",
	                "Oh well... If you need some good farming tools like a scythe, " +
					"it might help visiting the nearby Semos city blacksmith shop!");
				addReply("sickle",
		             "Oh well... If you need some good farming tools like a sickle, " +
				     "it might help visiting the nearby Semos city blacksmith shop!");
				addGoodbye("Goodbye and may your path be clear of weeds!");
			}
		};

		// Finalize Jingo Radish, the hoeing man near the Mill north of Semos
		npc.setEntityClass("hoeingmannpc");
		npc.setDescription("You see a man with a hoe, he's busy weeding the soil...");
		npc.setPosition(48,62);
		npc.initHP(100);
		zone.add(npc);
		return npc;
	}
}
