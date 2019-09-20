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
package games.stendhal.server.maps.deniran.cityinterior.potionsshop;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.actions.admin.AdministrationAction;
*/

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class DeniranPotionDealerNPC1 implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Lucretia Borgia") {

			@Override
			public void createDialog() {
				addGreeting();
				addJob("My name is Lucretia. Pleased to meet you... I Sell potions...");
				addHelp("My name is Lucretia... If you are interested in potions, you are in the right place...");
				addGoodbye("My name is Lucretia... Never forget it and come back... Goodbye for now");
				addOffer("I can offer you my name... Lucretia. Pleased to meet you...");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(12, 04));
				nodes.add(new Node(12, 12));
				setPath(new FixedPath(nodes, true));
			}

		};
		npc.setEntityClass("deniranpotiondealernpc1");
		npc.setPosition(9, 12);
		npc.setCollisionAction(CollisionAction.REROUTE);

		npc.setDescription("Lucretia Borgia");
		zone.add(npc);
	}
}
