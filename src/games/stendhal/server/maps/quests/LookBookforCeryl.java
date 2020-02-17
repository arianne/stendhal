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
import games.stendhal.server.entity.npc.action.EquipItemAction;
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
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Look for a book for Ceryl
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Ceryl </li>
 * <li> Jynath </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Talk with Ceryl to activate the quest. </li>
 * <li> Talk with Jynath for the book. </li>
 * <li> Return the book to Ceryl. </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 100 XP </li>
 * <li> some karma (10 + (5 | -5) </li>
 * <li> 50 gold coins </li>
 * </ul>
 *
 * REPETITIONS: None
 */
public class LookBookforCeryl extends AbstractQuest {
	private static final String QUEST_SLOT = "ceryl_book";



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step1LearnAboutQuest() {

		final SpeakerNPC npc = npcs.get("Ceryl");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I am looking for a very special #book.", null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I have nothing for you now.", null);

		/** Other conditions not met e.g. quest completed */
		npc.addReply("book","If you want to learn more, chat to my friend Wikipedian in Ados library.", null);

		/** If quest is not started yet, start it. */
		npc.add(
			ConversationStates.ATTENDING,
			"book", new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Could you ask #Jynath to return her book? She's had it for months now, and people are looking for it.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Great! Please get me it as quickly as possible... there's a huge waiting list!",
			new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh... I suppose I will have to get somebody else to do it, then.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"jynath",
			null,
			ConversationStates.QUEST_OFFERED,
			"Jynath is the witch who lives south of Or'ril castle, southwest of here. So, will you get me the book?",
			null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING, "book",
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"I really need that book now! Go to talk with #Jynath.", null);

		npc.add(
			ConversationStates.ATTENDING,
			"jynath",
			null,
			ConversationStates.ATTENDING,
			"Jynath is the witch who lives south of Or'ril castle, southwest of here.",
			null);
	}

	private void step2getBook() {
		final SpeakerNPC npc = npcs.get("Jynath");

		/**
		 * If player has quest and is in the correct state, just give him the
		 * book.
		 */
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start")),
			ConversationStates.ATTENDING,
			"Oh, Ceryl's looking for that book back? My goodness! I completely forgot about it... here you go!",
			new MultipleActions(new EquipItemAction("black book", 1, true), new SetQuestAction(QUEST_SLOT, "jynath")));

		/** If player keeps asking for the book, just tell him to hurry up */
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "jynath")),
			ConversationStates.ATTENDING,
			"You'd better take that book back to #Ceryl quickly... he'll be waiting for you.",
			null);

		npc.add(ConversationStates.ATTENDING, "ceryl", null,
			ConversationStates.ATTENDING,
			"Ceryl is the librarian at Semos, of course.", null);

		/** Finally if player didn't start the quest, just ignore him/her */
		npc.add(
			ConversationStates.ATTENDING,
			"book",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Sssh! I'm concentrating on this potion recipe... it's a tricky one.",
			null);
	}

	private void step3returnBook() {
		final SpeakerNPC npc = npcs.get("Ceryl");

		/** Complete the quest */
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("black book"));
		reward.add(new EquipItemAction("money", 50));
		reward.add(new IncreaseXPAction(100));
		reward.add(new IncreaseKarmaAction(10.0));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "jynath"),
					new PlayerHasItemWithHimCondition("black book")),
			ConversationStates.ATTENDING,
			"Oh, you got the book back! Phew, thanks!",
			new MultipleActions(reward));

		// There is no other way to get the book.
		// Remove that quest slot so that the player can get
		// it again from Jynath
		// As the book is both bound and useless outside the
		// quest, this is not a problem
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "jynath"),
					new NotCondition(new PlayerHasItemWithHimCondition("black book"))),
			ConversationStates.ATTENDING,
			"Haven't you got that #book back from #Jynath? Please go look for it, quickly!",
			new SetQuestAction(QUEST_SLOT, "start"));
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met Ceryl at the library, he's the librarian there.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to find the book.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "jynath", "done")) {
			res.add("I promised to fetch the black book from Jynath.");
		}
		if (questState.equals("jynath") && player.isEquipped("black book")
				|| questState.equals("done")) {
			res.add("I have talked to Jynath, and have the book.");
		}
		if (questState.equals("jynath") && !player.isEquipped("black book")) {
			res.add("I do not have the black book Jynath has.");
		}
		if (questState.equals("done")) {
			res.add("I have returned the book to Ceryl and got a little reward.");
		}
		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Look for a Book for Ceryl",
				"Ceryl wants an old book that was checked out.",
				false);
		step1LearnAboutQuest();
		step2getBook();
		step3returnBook();
	}

	@Override
	public String getName() {
		return "LookBookforCeryl";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Ceryl";
	}
}
