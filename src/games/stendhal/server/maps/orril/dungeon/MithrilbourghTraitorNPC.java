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
package games.stendhal.server.maps.orril.dungeon;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Xavkas - mithrilbourgh traitor.
 *
 * @author kymara - modded by tigertoes
 */
public class MithrilbourghTraitorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildXavkas(zone);
	}

	private void buildXavkas(final StendhalRPZone zone) {
		final SpeakerNPC Xavkas = new SpeakerNPC("Xavkas"){

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(101, 141));
				nodes.add(new Node(106,141));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("I am innocent, I tell you.  Get me outta here!");
				addJob("I am a wizard. I was once an elder in the Mithrilbourgh council.");
				addHelp("I can't do anything to help you.");
				addQuest("I don't know what you can do for me currently.  Come back at a later time.");
				addGoodbye("Please don't forget me.");
			} //remaining behaviour defined in quest
		};

		Xavkas.setDescription("Xavkas, a Mithrilbourgh Wizard, once a mighty elder, then a traitor, now, a common prisoner.  Woe, is he.");
		Xavkas.setEntityClass("mithrilforgernpc");
		Xavkas.setPosition(101, 141);
		Xavkas.initHP(100);
		zone.add(Xavkas);
	}
}
