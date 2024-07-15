/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.tannery;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestRegisteredCondition;


public class LeatherCrafterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC npc = new SpeakerNPC("Tinny");
		npc.setDescription("You see Tinny, the leather crafter.");

		buildOutfit(npc);
		buildPath(npc);
		buildDialogue(npc);

		return npc;
	}

	private void buildOutfit(final SpeakerNPC npc) {
		npc.setOutfit("body=0,head=0,eyes=3,hair=57,dress=968,mask=5,detail=16");
		npc.setOutfitColor("body", SkinColor.LIGHT);
		npc.setOutfitColor("eyes", Color.BLUE);
	}

	private void buildPath(final SpeakerNPC npc) {
		final List<Node> nodes = new LinkedList<>();
		nodes.add(new Node(30, 4));
		nodes.add(new Node(20, 4));
		nodes.add(new Node(20, 13));
		nodes.add(new Node(30, 13));
		npc.setPath(new FixedPath(nodes, true));
		npc.setPosition(30, 4);
		npc.setCollisionAction(CollisionAction.STOP);
	}

	private void buildDialogue(final SpeakerNPC npc) {
		npc.addGreeting("Hello, how can I help you?");
		npc.addGoodbye("Goodbye.");
		npc.addJob("I am a leather crafter. I recently completed my apprenticeship under Skinner and"
				+ " will one day take over responsibility of the tannery.");

		final String questSlot = "items_for_tinny";
		final ChatCondition questNotRegistered = new NotCondition(new QuestRegisteredCondition(questSlot));

		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.HELP_MESSAGES,
				questNotRegistered,
				ConversationStates.ATTENDING,
				"If you are interested in a pouch to carry your money in, speak to Skinner.",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				questNotRegistered,
				ConversationStates.ATTENDING,
				"There is nothing I need help with at this time.",
				null);
	}
}
