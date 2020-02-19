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
package games.stendhal.server.maps.ados.twilightzone;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/*
 * Twilight zone is a copy of sewing room in dirty colours with a delirious sick lda (like Ida) in it
 */
public class SeamstressNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSeamstress(zone);
	}

	private void buildSeamstress(final StendhalRPZone zone) {
		// get the clone of Ida for twilight zone
		final SpeakerNPC seamstress = games.stendhal.server.maps.ados.sewingroom.SeamstressNPC.getClone();

		final List<Node> nodes = new LinkedList<Node>();
		nodes.add(new Node(7, 7));
		nodes.add(new Node(7, 14));
		nodes.add(new Node(12, 14));
		nodes.add(new Node(12, 7));

		seamstress.setPath(new FixedPath(nodes, true));

		// see through
		seamstress.setDescription("You see Ida, she looks sick and feverish");
		seamstress.setVisibility(70);
		// walk through
		seamstress.setResistance(0);
		seamstress.setPosition(7, 7);
		seamstress.initHP(40);
		zone.add(seamstress);
	}
}
