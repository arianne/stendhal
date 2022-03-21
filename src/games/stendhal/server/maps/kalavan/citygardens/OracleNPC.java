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
package games.stendhal.server.maps.kalavan.citygardens;

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
	private final List<String> regions = Arrays.asList(Region.KALAVAN, Region.KIRDNEH, Region.FADO_CITY, Region.FADO_CAVES);

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Lobelia") {
			@Override
			public void createDialog() {
				addGreeting("Hello. You caught me enjoying the flowers here.");

				// use a standard action to list the names of NPCs for quests which haven't been started in this region
				addReply(ConversationPhrases.HELP_MESSAGES, null, new SayNPCNamesForUnstartedQuestsAction(regions));

				// if the player says an NPC name, describe the quest (same description as in the travel log)
				add(ConversationStates.ATTENDING,
						"",
						new TriggerIsNPCNameForUnstartedQuestCondition(regions),
						ConversationStates.ATTENDING,
						null,
						new SayUnstartedQuestDescriptionFromNPCNameAction(regions));
				addQuest("There are friends in " + Grammar.enumerateCollection(regions) + " who could need #help from adventurers.");
				addJob("I sometimes tend the beautiful flowers here, but really that is a job for lovely Sue, the gardener.");
				addOffer("Like my #sisters in other areas, I'm here to guide you on how to #help others.");
				addReply("sisters", "My sisters and I all have the #name of a flower. Find them to learn how to #help those nearest them.");
				addReply("name", "Lobelia are tiny purple flowers. Perhaps you can see some in the flowerbeds. I just love it here, Sue is so clever.");

				// just to be nice :)
				addEmotionReply("hugs", "hugs");
				addGoodbye("Thank you, nice to see you.");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 111));
				nodes.add(new Node(58, 111));
				nodes.add(new Node(58, 109));
				nodes.add(new Node(61, 109));
				nodes.add(new Node(61, 100));
				nodes.add(new Node(56, 100));
				nodes.add(new Node(56, 101));
				nodes.add(new Node(54, 101));
				nodes.add(new Node(54, 105));
				nodes.add(new Node(27, 105));
				nodes.add(new Node(27, 107));
				nodes.add(new Node(22, 111));
				setPath(new FixedPath(nodes, true));
			}
		};
		npc.setPosition(22,111);
		npc.setDescription("You see Lobelia. She's gazing intently at the flowerbeds around her.");
		npc.setEntityClass("oracle4npc");
		npc.setShadowStyle("48x64_float");
		zone.add(npc);
	}
}
