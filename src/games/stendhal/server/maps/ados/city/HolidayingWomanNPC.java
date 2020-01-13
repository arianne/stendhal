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
package games.stendhal.server.maps.ados.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ListProducedItemDetailAction;
import games.stendhal.server.entity.npc.action.ListProducedItemsOfClassAction;
import games.stendhal.server.entity.npc.condition.TriggerIsProducedItemOfClassCondition;

/**
 * Creates a woman NPC to help populate Ados
 *
 */
public class HolidayingWomanNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Alice Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(47, 90));
				nodes.add(new Node(38, 90));
				nodes.add(new Node(38, 91));
				nodes.add(new Node(3, 91));
				nodes.add(new Node(3, 64));
				nodes.add(new Node(40, 64));
				nodes.add(new Node(40, 75));
				nodes.add(new Node(47, 75));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addHelp("I walked around a bit and saw a nice looking tavern. Did you take a look inside already? It smells fantastic there!");
				addOffer("I'm quite a #food expert, after all my travels on lovely holidays!");
				addQuest("You could try all the #food available from cooks and chefs across the island. I can tell you what I've sampled on my travels.");
				addReply("food", null, new ListProducedItemsOfClassAction("food","I think I've tasted everything, [#items]. I can tell you more about each foodstuff, if you like."));
				add(
						ConversationStates.ATTENDING,
						"",
						new TriggerIsProducedItemOfClassCondition("food"),
						ConversationStates.ATTENDING,
						null,
						new ListProducedItemDetailAction()
					);
				addJob("Aaaah, I am on holiday here, only walking around.");
				addGoodbye("Bye bye.");

				}
		};

		npc.setOutfit("body=1,head=0,eyes=21,dress=971,hair=47");
		npc.setOutfitColor("skin", SkinColor.LIGHT);
		npc.setOutfitColor("dress", 0xac319c);
		npc.setPosition(47, 90);
		npc.initHP(100);
		npc.setDescription("You see Alice Farmer. She is on holidays in Ados.");
		zone.add(npc);
	}
}
