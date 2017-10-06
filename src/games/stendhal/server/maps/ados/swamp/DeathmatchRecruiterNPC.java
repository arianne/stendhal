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
package games.stendhal.server.maps.ados.swamp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Entrance to Deathmatch.
 */
public class DeathmatchRecruiterNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDeathmatchRecruiter(zone);
	}

	private void buildDeathmatchRecruiter(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Thonatus") {

			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(15, 26));
				path.add(new Node(28, 26));
				path.add(new Node(28, 32));
				path.add(new Node(25, 32));
				path.add(new Node(25, 38));
				path.add(new Node(37, 38));
				path.add(new Node(37, 27));
				path.add(new Node(33, 27));
				path.add(new Node(33, 23));
				path.add(new Node(45, 23));
				path.add(new Node(45, 33));
				path.add(new Node(52, 33));
				path.add(new Node(52, 31));
				path.add(new Node(50, 31));
				path.add(new Node(50, 46));
				path.add(new Node(57, 46));
				path.add(new Node(57, 49));
				path.add(new Node(67, 49));
				path.add(new Node(67, 40));
				path.add(new Node(46, 40));
				path.add(new Node(46, 50));
				path.add(new Node(46, 31));
				path.add(new Node(37, 31));
				path.add(new Node(37, 38));
				path.add(new Node(25, 38));
				path.add(new Node(25, 32));
				path.add(new Node(15, 32));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there. You look like a reasonable fighter.");
				addJob("I'm recruiter for the Ados #deathmatch.");
				addHelp("I tell you about the Ados #deathmatch and send you there if you are strong enough.");
				addQuest("If you are brave, you can try the Ados #deathmatch.");
				addOffer("I'll tell you about the Ados #deathmatch.");
				add(ConversationStates.ATTENDING, "deathmatch", null, ConversationStates.ATTENDING,
				        "Many dangerous creatures will attack you in the deathmatch arena. It is only for strong #heroes.", null);
				// response to 'heroes' is defined in maps.quests.AdosDeathmatch
				// because we need here to know about who is in the deathmatch. The teleport action is done there also.
				addGoodbye("I hope you will enjoy the Ados Deathmatch!");
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(15, 26);
		npc.initHP(100);
		npc.setDescription("You see Thonatus, recruiter for the Ados Deathmatch. You are lucky that you find him, he is fast normally.");
		zone.add(npc);
	}
}
