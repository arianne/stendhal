/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.market;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayNPCNamesForUnstartedQuestsAction;
import games.stendhal.server.entity.npc.action.SayUnstartedQuestDescriptionFromNPCNameAction;
import games.stendhal.server.entity.npc.condition.TriggerIsNPCNameForUnstartedQuestCondition;
import games.stendhal.server.maps.Region;

/**
 * An oracle who lets players know how they can help others.
 */
public class OracleNPC implements ZoneConfigurator {

	/**
	 * region that this NPC can give information about
	 */
	private final List<String> regions = Arrays.asList(Region.ADOS_SURROUNDS, Region.ADOS_CITY);

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Calla") {

			@Override
			public void createDialog() {
				addGreeting("What power the little flower! What power have you? Ados people are looking for #help...");

				// use a standard action to list the names of NPCs for quests which haven't been started in this region
				addReply(ConversationPhrases.HELP_MESSAGES, null, new SayNPCNamesForUnstartedQuestsAction(regions));

				// if the player says an NPC name, describe the quest (same description as in the travel log)
				add(ConversationStates.ATTENDING,
						"",
						new TriggerIsNPCNameForUnstartedQuestCondition(regions),
						ConversationStates.ATTENDING,
						null,
						new SayUnstartedQuestDescriptionFromNPCNameAction(regions));
				addQuest("Oh, there are so many others in " + Grammar.enumerateCollection(regions) + " who might need #help, I wouldn't ask you anything new.");
				addJob("I don't work, but I can show you how to #help others, particularly in " + Grammar.enumerateCollection(regions) + ".");
				addOffer("*giggles* I don't sell anything. I can tell you about my #sisters or my #name, if you like.");
				addReply("sisters", "My sisters live in other cities. Find them to learn how to #help those nearest them.");
				addReply("name", "Me and my #sisters all have names of flowers. " +
						"My name, Calla, is a kind of lily which can have the same colour as my dress. It's so pretty.");

				// just to be nice :)
				addEmotionReply("hugs", "hugs");
				addGoodbye("Thank you, nice to see you.");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(62, 86));
				nodes.add(new Node(62, 87));
				nodes.add(new Node(64, 87));
				nodes.add(new Node(64, 88));
				nodes.add(new Node(66, 88));
				nodes.add(new Node(66, 89));
				nodes.add(new Node(67, 89));
				nodes.add(new Node(67, 92));
				nodes.add(new Node(66, 92));
				nodes.add(new Node(66, 95));
				nodes.add(new Node(64, 95));
				nodes.add(new Node(64, 97));
				nodes.add(new Node(63, 97));
				nodes.add(new Node(63, 99));
				nodes.add(new Node(56, 99));
				nodes.add(new Node(56, 101));
				nodes.add(new Node(52, 101));
				nodes.add(new Node(52, 100));
				nodes.add(new Node(51, 100));
				nodes.add(new Node(51, 99));
				nodes.add(new Node(50, 99));
				nodes.add(new Node(50, 98));
				nodes.add(new Node(49, 98));
				nodes.add(new Node(49, 96));
				nodes.add(new Node(50, 96));
				nodes.add(new Node(50, 91));
				nodes.add(new Node(54, 91));
				nodes.add(new Node(54, 90));
				nodes.add(new Node(56, 90));
				nodes.add(new Node(56, 88));
				nodes.add(new Node(58, 88));
				nodes.add(new Node(58, 87));
				nodes.add(new Node(60, 87));
				nodes.add(new Node(60, 86));
				setPath(new FixedPath(nodes, true));
			}
		};
		npc.setPosition(62, 86);
		npc.setDescription("You see Calla. She smells faintly of lilies.");
		npc.setEntityClass("oracle2npc");
		npc.setShadowStyle("48x64_float");
		zone.add(npc);
	}
}
