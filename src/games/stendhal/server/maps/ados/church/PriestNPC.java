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
package games.stendhal.server.maps.ados.church;

import static games.stendhal.server.maps.quests.AGrandfathersWish.canRequestHolyWater;
import static games.stendhal.server.maps.quests.AGrandfathersWish.QUEST_SLOT;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;


/**
 * Priest to make holy water for An Old Man's Wish quest.
 */
public class PriestNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC priest = new SpeakerNPC("Father Calenus");
		priest.setOutfit("body=0,head=0,eyes=9,dress=56,hair=8");
		priest.setOutfitColor("eyes", 0x1f2f9e);
		priest.setOutfitColor("hair", 0x59260b);
		priest.setDescription("You see a priest preparing to give a"
			+ " sermon.");

		priest.addGreeting("Hello my child. What can I #help you with?");
		priest.addGoodbye("Go in peace.");
		priest.addJob("I am steward over this holy house.");
		priest.addHelp("If you are in need of blessings, I can offer you"
			+ " some #'holy water'.");
		priest.addOffer("Find inner peace. Only then will you understand"
			+ " the value of life.");

		priest.add(
			ConversationStates.ATTENDING,
			"holy water",
			new AndCondition(
				new NotCondition(canRequestHolyWater()),
				new QuestNotInStateCondition(QUEST_SLOT, 2, "holy_water:bring_items")),
			ConversationStates.ATTENDING,
			"Holy water is consecrated to help those that are afflicted and"
				+ " in need of blessings.",
			null);

		final List<Node> nodes = new LinkedList<Node>();
		nodes.add(new Node(16, 4));
		nodes.add(new Node(24, 4));
		priest.setPathAndPosition(new FixedPath(nodes, true));
		priest.setCollisionAction(CollisionAction.STOP);

		return priest;
	}
}
