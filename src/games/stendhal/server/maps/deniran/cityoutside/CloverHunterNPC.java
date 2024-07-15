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
package games.stendhal.server.maps.deniran.cityoutside;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.idle.WanderIdleBehaviour;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestRegisteredCondition;


public class CloverHunterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		SpeakerNPC npc = new SpeakerNPC("Maple");
		npc.setEntityClass("cloverhunternpc");
		npc.setDescription("You see a young woman scanning the ground with her magnifying glass.");
		npc.setPosition(68, 67);
		npc.setIdleBehaviour(new WanderIdleBehaviour());

		buildDialogue(npc);

		return npc;
	}

	private void buildDialogue(SpeakerNPC npc) {
		npc.addGreeting("Hello fellow clover hunter!");
		npc.addGoodbye("May luck shine brightly o'er you!");
		npc.addJob("I'm a clover hunter. I'm searching for the lucky four-leaf #clover.");
		npc.addHelp("Four-leaf #clovers are extremely rare. If you find one, it is said you will have"
				+ " excellent luck!");
		npc.addOffer("I can tell you a little about #clovers.");
		npc.addReply("clover", "Clovers can grow just about anywhere in the sunlight. So don't go"
				+ " looking for any underground. Ones with four leaves are especially rare and are a"
				+ " challenging #task to find.");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new NotCondition(new QuestRegisteredCondition("lucky_four_leaf_clover")),
			ConversationStates.ATTENDING,
			"No, no thank you. I can find a four-leaf clover on my own.",
			null);
	}
}
