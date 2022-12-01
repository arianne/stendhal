/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.brelandhouse;

import static games.stendhal.server.maps.quests.AGrandfathersWish.QUEST_SLOT;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestRegisteredCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;


public class GrandfatherNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC elias = new SpeakerNPC("Elias Breland");

		elias.addGreeting("Hello young one.");
		elias.addGoodbye("Goodbye.");
		elias.addHelp("I wish I had help to offer. But sadly I do not.");
		elias.addJob("I am an old man that enjoys tending to my garden.");
		elias.addOffer("I have nothing to offer.");
		elias.add(ConversationStates.ANY,
			ConversationPhrases.QUEST_MESSAGES,
			new NotCondition(new QuestRegisteredCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"There is something that weighs heavy on me. But I am not ready"
				+ " for help. Perhaps you could come back later.",
			null);

		final List<Node> nodes = new LinkedList<Node>();
		nodes.add(new Node(11, 16));
		nodes.add(new Node( 3, 16));
		nodes.add(new Node( 3,  3));
		nodes.add(new Node(11,  3));
		nodes.add(new Node(11,  6));
		nodes.add(new Node(27,  6));
		nodes.add(new Node(11,  6));
		elias.setPathAndPosition(new FixedPath(nodes, true));
		elias.setCollisionAction(CollisionAction.STOP);

		elias.setOutfit("body=986,head=996,eyes=999,dress=965,hair=999");
		elias.setDescription("You see " + elias.getName() + " pacing around his home.");

		zone.add(elias);
	}
}
