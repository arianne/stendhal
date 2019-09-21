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
 * Provides A man hoeing the farm ground near the Mill north of Semos
 * @see games.stendhal.server.maps.quests.AdMemoriaInPortfolio
 *
 * Jingo Radish offers a quest to unlock the portfolio container
 * Involves Hazen in Kirdneh
 *
 * @author omero
 * 
 */
public class HoeingManNPC implements ZoneConfigurator {

	@Override
	public void configureZone(
			final StendhalRPZone zone,
			final Map<String, String> attributes) {

		// TODO: NPC Hazen does not exist, and should probably not be named so similar to Haizen.
		/*
		final String[] rambles = {
			"... This is so relaxing... Hoeing and seeding... Hoeing and seeding... ",
			"... This is so relaxing... #Hazen... #Kirdneh... Ohh my poor #memory... ",
			"... This is so relaxing... #Kirdneh... #Hazen... Ahh my fainting  #memory...",
			"... This is so relaxing... Hoeing and seeding... #Hazen... #Memory... #Kirdneh...",
			"... This is so relaxing... Hoeing and seeding... #Kirdneh... Oh poor #memory... #Hazen...",
			"... This is so relaxing... Hoeing and seeding... #Hazen... #Kirdneh...  Where is my #memory gone... ",
            "... This is so relaxing... Hoeing and seeding... If only I could remember... What happened to #Hazen... Where is #Kirdneh... Ah my poor #memory..."
		};
		//1,2,3,4,5 minutes
		
		new MonologueBehaviour(buildNPC(zone),rambles, 1);
		*/
	}

	private SpeakerNPC buildNPC(
        final StendhalRPZone zone) {
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
				// TODO: NPC Hazen does not exist, and should probably not be named so similar to Haizen.
				// addGreeting("Well met... Names... Names are important... Do you have a #name? I have a #hoe... ");

				addJob("You see? I keep freeing the soil from weeds with my #hoe but those grow back every time...");
				addHelp("Take your time and check the area around... There's a mill somewhat north and a really nice farm to the east... Nice and rich country, you could go hunting for food!");
				addReply("hoe",
                    "Oh well, there's nothing special about my hoe... " +
                    "If you need some good farming tools like a #scythe, it might help visiting the nearby Semos city blacksmith shop!");
				addReply("scythe",
	                    "Ah well... If you need some good farming tools like a scythe, it might help visiting the nearby Semos city blacksmith shop!");
				
				
				// TODO: NPC Hazen does not exist, and should probably not be named so similar to Haizen.
				/*
					addReply("hazen", "I... I have a sister... I remember the name... Kirdneh");
					addReply("kirdneh", "I... I remember the name... Kirdneh... I have a sister... Hazen!");
					addReply("name",
		                    "I... I do not remember... What was it... A name... A name for a #quest...");
					addOffer("Will you do a #task for me?");
				*/
				addGoodbye("Goodbye and may your path be clear of weeds!");
				
				/**
                 * Additional behavior code is in games.stendhal.server.maps.quests.AdMemoriaInPortfolio
                 */
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
