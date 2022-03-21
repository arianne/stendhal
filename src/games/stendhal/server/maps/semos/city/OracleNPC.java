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
package games.stendhal.server.maps.semos.city;

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
 *
 */
public class OracleNPC implements ZoneConfigurator {

	/**
	 * region that this NPC can give information about
	 */
	private final List<String> regions = Arrays.asList(Region.SEMOS_CITY, Region.SEMOS_SURROUNDS);

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Periwinkle") {

			@Override
			public void createDialog() {
				addGreeting("Roses are red, violets are blue, Semos needs #help, what can you do?");

				// use a standard action to list the names of NPCs for quests which haven't been started in this region
				addReply(ConversationPhrases.HELP_MESSAGES, null, new SayNPCNamesForUnstartedQuestsAction(regions));

				// if the player says an NPC name, describe the quest (same description as in the travel log)
				add(ConversationStates.ATTENDING,
						"",
						new TriggerIsNPCNameForUnstartedQuestCondition(regions),
						ConversationStates.ATTENDING,
						null,
						new SayUnstartedQuestDescriptionFromNPCNameAction(regions));

				addQuest("Oh, there are so many others who may need #help in " + Grammar.enumerateCollection(regions) + ", I wouldn't ask you anything new.");
				addJob("I have no real occupation, my skill is in guiding you in how to #help others, especially in " + Grammar.enumerateCollection(regions) + ".");
				addOffer("*giggles* I don't sell anything. I can tell you about my #sisters or my #name, if you like.");
				addReply("sisters", "My sisters live in other cities. Find them to learn how to #help those nearest them.");
				addReply("name", "Me and my #sisters all have names of flowers. " +
						"My mother loved the little blooms of the forget-me-not plants.  Well, she couldn't name me Forget-me-not, so she named me Periwinkle cause the blooms looked somewhat alike.   Please, don't forget me...");

				// just to be nice :)
				addEmotionReply("hugs", "hugs");
				addGoodbye("Thank you for stopping by.");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(2, 29));
				nodes.add(new Node(2, 31));
				nodes.add(new Node(9, 31));
				nodes.add(new Node(9, 32));
				nodes.add(new Node(5, 32));
				nodes.add(new Node(5, 33));
				nodes.add(new Node(3, 33));
				nodes.add(new Node(3, 32));
				nodes.add(new Node(2, 32));
				setPath(new FixedPath(nodes, true));
			}
		};
		npc.setPosition(2, 29);
		npc.setDescription("You see Periwinkle. She looks dreamy and distracted.");
		npc.setEntityClass("oracle1npc");
		npc.setShadowStyle("48x64_float");
		zone.add(npc);
	}
}
