/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Hat For Monogenes
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Monogenes, an old man in Semos city.</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Monogenes asks you to buy a hat for him.</li>
 * <li> Xin Blanca sells you a leather helmet.</li>
 * <li> Monogenes sees your leather helmet and asks for it and then thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS: - None.
 */
public class HatForMonogenes extends AbstractQuest {
	private static final String QUEST_SLOT = "hat_monogenes";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest(QUEST_SLOT)) {
			res.add("I have met Monogenes at the spring in Semos village");
		}
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have to find a hat, something leather to keep his head warm.");
		if (player.isQuestInState(QUEST_SLOT, "start")
				&& player.isEquipped("leather helmet")
				|| player.isQuestCompleted(QUEST_SLOT)) {
			res.add("I have found a hat.");
		}
		if (player.isQuestCompleted(QUEST_SLOT)) {
			res.add("I gave the hat to Monogenes to keep his bald head warm.");
		}
		return res;
	}

	private void createRequestingStep() {
		final SpeakerNPC monogenes = npcs.get("Monogenes");

		monogenes.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...",
			null);

		monogenes.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thanks for the offer, good friend, but this hat will last me five winters at least, and it's not like I need more than one.",
			null);

		monogenes.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thanks, my good friend. I'll be waiting here for your return!",
			new SetQuestAction(QUEST_SLOT, "start"));

		monogenes.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"You surely have more importants things to do, and little time to do them in. I'll just stay here and freeze to death, I guess... *sniff*",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		monogenes.add(
			ConversationStates.QUEST_OFFERED,
			"hat",
			null,
			ConversationStates.QUEST_OFFERED,
			"You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?",
			null);
	}

	private void createBringingStep() {
		final SpeakerNPC monogenes = npcs.get("Monogenes");

		monogenes.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(monogenes.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new PlayerHasItemWithHimCondition("leather helmet")),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Hey! Is that leather hat for me?", null);

		monogenes.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(monogenes.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new NotCondition(new PlayerHasItemWithHimCondition("leather helmet"))),
			ConversationStates.ATTENDING,
			"Hey, my good friend, remember that leather hat I asked you about before? It's still pretty chilly here...",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("leather helmet"));
		reward.add(new IncreaseXPAction(50));
		reward.add(new IncreaseKarmaAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		// make sure the player isn't cheating by putting the
		// helmet away and then saying "yes"
		monogenes.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("leather helmet"),
			ConversationStates.ATTENDING,
			"Bless you, my good friend! Now my head will stay nice and warm.",
			new MultipleActions(reward));

		monogenes.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"I guess someone more fortunate will get his hat today... *sneeze*",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Hat for Monogenes",
				"Monogenes wants a hat to help him keep warm during the winter.",
				false);
		createRequestingStep();
		createBringingStep();
	}

	@Override
	public String getName() {
		return "HatForMonogenes";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}
	@Override
	public String getNPCName() {
		return "Monogenes";
	}
}
