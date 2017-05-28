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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Traps for Klaas
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Klaas (the Seaman that takes care of Athor's ferry's cargo)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Klaas asks you to bring him rodent traps.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>1000 XP</li>
 * <li>5 greater antidote
 * <li>note to apothecary (disabled until Antivenom Ring quest is ready)
 * <li>Can sell rodent traps to Klaas</li>
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Every 24 hours</li>
 * </ul>
 */
public class TrapsForKlaas extends AbstractQuest {

	public final int REQUIRED_TRAPS = 20;

    // Time player must wait to repeat quest (1 day)
    private static final int WAIT_TIME = 60 * 24;

	private static final String QUEST_SLOT = "traps_for_klaas";


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have talked to Klaas.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not care to deal with rodents.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("I promised to gather " + REQUIRED_TRAPS + " rodent traps and bring them to Klaas.");
		}
		if (player.isQuestInState(QUEST_SLOT, 0, "done")) {
			res.add("I gave the rodent traps to Klaas. I got some experience and antidotes.");
		}
		if (isRepeatable(player)) {
		    res.add("I should check if Klaas needs my help again.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Klaas");

		// Player asks for quest
		npc.add(ConversationStates.ATTENDING,
		        ConversationPhrases.QUEST_MESSAGES,
		        new AndCondition(
		                new NotCondition(new QuestActiveCondition(QUEST_SLOT)),
		                new TimePassedCondition(QUEST_SLOT, 1, WAIT_TIME)
		                ),
		        ConversationStates.QUEST_OFFERED,
		        "The rats down here have been getting into the food storage. Would you help me rid us of the varmints?",
		        null);

        // Player requests quest before wait period ended
        npc.add(ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, WAIT_TIME)),
                ConversationStates.ATTENDING,
                null,
                new SayTimeRemainingAction(QUEST_SLOT, 1, WAIT_TIME, "Thanks for the traps. Now the food will be safe. But I may need your help again in"));

		// Player asks for quest after already started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I believe I already asked you to get me " + REQUIRED_TRAPS + " rodent traps.",
				null);

		// Player accepts quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thanks, I need you to bring me bring me " + REQUIRED_TRAPS + " #rodent #traps. Please hurry! We can't afford to lose anymore food.",
			new SetQuestAction(QUEST_SLOT, "start"));

		// Player rejects quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			// Klaas walks away
			ConversationStates.IDLE,
			"Don't waste my time. I've got to protect the cargo.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Player asks about rodent traps
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("rodent trap", "trap", "rodent traps", "traps"),
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I don't know of anyone who sells 'em. But I did hear a story once about a fella who killed a large rat and discovered a trap snapped shut on its foot.",
			null);

	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Klaas");

		// Reward
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("rodent trap", 20));
		// Replacing "not to apothecary" reward with antidotes until Antivenom Ring quest is done.
		//reward.add(new EquipItemAction("note to apothecary", 1, true));
		reward.add(new EquipItemAction("greater antidote", 5));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new IncreaseKarmaAction(10));
        reward.add(new SetQuestAction(QUEST_SLOT, "done"));
        reward.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));

		// Player has all 20 traps
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT),
						new PlayerHasItemWithHimCondition("rodent trap")),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Did you bring any traps?",
				null);

		// Player is not carrying any traps
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new PlayerHasItemWithHimCondition("rodent trap"))),
			ConversationStates.ATTENDING,
			"I could really use those #traps. How can I help you?",
			null);

		// Player is not carrying 20 traps
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(new PlayerHasItemWithHimCondition("rodent trap"),
						new NotCondition(new PlayerHasItemWithHimCondition("rodent trap", 20))),
				ConversationStates.ATTENDING,
				"I'm sorry but I need 20 #rodent #traps",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("rodent trap", 20),
				ConversationStates.ATTENDING,
				// Not mentioning apothecary until Antivenom Ring quest is ready
				"Thanks! I've got to get these set up as quickly as possible. Take these antidotes as a reward.",// I used to know an old #apothecary. Take this note to him. Maybe he can help you out with something.",
				new MultipleActions(reward));

        // Player says did not bring items
        npc.add(
            ConversationStates.QUEST_ITEM_BROUGHT,
            ConversationPhrases.NO_MESSAGES,
            null,
            ConversationStates.ATTENDING,
            "Please hurry! I just found another box of food that's been chewed through.",
            null);

		// Player asks about the apothecary
		/* Disabling until Antivenom Ring quest is ready
		npc.add(
			ConversationStates.ATTENDING,
			"apothecary",
			null,
			ConversationStates.ATTENDING,
			"I used to know an old apothecary, but don't know where he has settled down. Perhaps someone in Ados would know.",
			null);

		// Player has lost note
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new NotCondition(new PlayerHasItemWithHimCondition("note to apothecary")),
						new QuestCompletedCondition(QUEST_SLOT),
						new QuestNotStartedCondition("antivenom_ring")),
				ConversationStates.ATTENDING,
				"You lost the note? Well, I guess I can write you up another, but be careful this time.",
				new EquipItemAction("note to apothecary", 1, true));
		*/
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Traps for Klaas",
				"Klaas, the cargo caretaker on the Athor ferry, is in need of some rodent traps.",
				false);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "TrapsForKlaas";
	}

	public String getTitle() {

		return "TrapsForKlaas";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.ATHOR_ISLAND;
	}

	@Override
	public String getNPCName() {
		return "Klaas";
	}
}
