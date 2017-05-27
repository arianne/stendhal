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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Speak with Hackim
 *
 * PARTICIPANTS: - Hackim Easso, the blacksmith's assistant
 *
 * STEPS: - Talk to Hackim to activate the quest and keep speaking with Hackim.
 *
 * REWARD: - 10 XP - 5 gold coins
 *
 * REPETITIONS: - As much as wanted, but you only get the reward once.
 */
public class MeetHackim extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_hackim";
	List<String> yesTrigger;


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I talked with Hackim, the very nice Semos blacksmith assistant. He wants to help me with how to buy weapon so I should ask him about that help.");
		if (isCompleted(player)) {
			res.add("I listened to his really useful information about how to deal with traders like Xin Blanca, a guy in Semos tavern.");
		}
		return res;
	}

	private void prepareHackim() {

		final SpeakerNPC npc = npcs.get("Hackim Easso");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_1,
			"We aren't allowed to sell weapons to adventurers nowadays; we're working flat-out to produce equipment for the glorious Imperial Deniran Army as they fight against Blordrough's dark legions in the south. (Sssh... can you come here so I can whisper?)",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_2,
			"*whisper* Go to the tavern and talk to a man called #Xin #Blanca... he buys and sells equipment that might interest you. Do you want to hear more?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			yesTrigger,
			null,
			ConversationStates.INFORMATION_3,
			"Ask him what he has to #offer, and look at what he will let you #buy and #sell. For instance, if you had a studded shield which you didn't want, you could #'sell studded shield'.",
			null);

		final String answer = "Guessed who supplies Xin Blanca with the weapons he sells? Well, it's me! I have to avoid raising suspicion, though, so I can only smuggle him small weapons. If you want something more powerful, you'll have to venture into the dungeons and kill some of the creatures there for items.\n";

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 5));
		reward.add(new IncreaseXPAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_3,
				Arrays.asList("buy", "sell", "offer", "sell studded shield"),
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				answer + "If anybody asks, you don't know me!",
				new MultipleActions(reward));

		npc.add(ConversationStates.INFORMATION_3,
				Arrays.asList("buy", "sell", "offer", "sell studded shield"),
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				answer + "Where did you get those weapons? A toy shop?",
				null);

		npc.add(new ConversationStates[] {
					ConversationStates.ATTENDING,
					ConversationStates.INFORMATION_1,
					ConversationStates.INFORMATION_2,
					ConversationStates.INFORMATION_3 },
				ConversationPhrases.NO_MESSAGES,
    			null,
    			ConversationStates.ATTENDING,
    			"Remember, all the weapons are counted; best to leave them alone.",
    			null);

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Hackim Easso",
				"The blacksmith assistant Hackim Easso has some useful information.",
				false);
		yesTrigger = new LinkedList<String>(ConversationPhrases.YES_MESSAGES);
		yesTrigger.add("Xin Blanca");
		yesTrigger.add("Blanca");
		yesTrigger.add("Xin");
		prepareHackim();
	}

	@Override
	public String getName() {
		return "MeetHackim";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Hackim Easso";
	}
}
