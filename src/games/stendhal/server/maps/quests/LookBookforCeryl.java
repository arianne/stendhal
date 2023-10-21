/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.orril.magician_house.WitchNPC;
import games.stendhal.server.maps.semos.library.LibrarianNPC;
import games.stendhal.server.util.ResetSpeakerNPC;

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
public class LookBookforCeryl implements QuestManuscript {
	private final static String QUEST_SLOT = "ceryl_book";

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Look for a Book for Ceryl")
			.description("Ceryl wants an old book that was checked out.")
			.internalName(QUEST_SLOT)
			.notRepeatable()
			.minLevel(0)
			.region(Region.SEMOS_CITY)
			.questGiverNpc("Ceryl");

		quest.history()
			.whenNpcWasMet("I have met Ceryl at the library, he's the librarian there.")
			.whenQuestWasRejected("I do not want to find the book.")
			.whenQuestWasAccepted("I promised to fetch the black book from Jynath.")
			.whenTaskWasCompleted("I have talked to Jynath and got the book.")
			.whenQuestWasCompleted("I have returned the book to Ceryl and got a little reward.");

		quest.offer()
			.respondToRequest("I am looking for a very special book. Could you ask #Jynath to return it? She has had it for months now, and people are looking for it.")
			.respondToUnrepeatableRequest("I have nothing for you now.")
			.respondToAccept("Great! Please get me it as quickly as possible... there's a huge waiting list!")
			.respondTo("jynath").saying("Jynath is the witch who lives south of Or'ril castle, southwest of here. So, will you get me the book?")
			.respondToReject("Oh... I suppose I will have to get somebody else to do it, then.")
			.rejectionKarmaPenalty(5.0)
			.remind("I really need that book now! Go to talk with #Jynath.");

		SpeakerNPC npc = NPCList.get().get("Ceryl");
		npc.addReply("jynath", "Jynath is the witch who lives south of Or'ril castle, southwest of here.");

		step2getBook();
		quest.task()
			.requestItem(1, "black book");

		quest.complete()
			.greet("Hi, did you get the book from Jynath?")
			.respondToReject("Oh... I suppose I will have to get somebody else to fetch it, then.")
			.respondToAccept("Oh, you got the book back! Phew, thanks!")
			.rewardWith(new IncreaseXPAction(100))
			.rewardWith(new IncreaseKarmaAction(10))
			.rewardWith(new EquipItemAction("money", 50));

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

		return quest;
	}



	private void step2getBook() {
		final SpeakerNPC npc = NPCList.get().get("Jynath");

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


	public boolean removeFromWorld() {
		final boolean res = ResetSpeakerNPC.reload(new LibrarianNPC(), "Ceryl")
			&& ResetSpeakerNPC.reload(new WitchNPC(), "Jynath");
		// reload other quests associated with Ceryl
		SingletonRepository.getStendhalQuestSystem().reloadQuestSlots("obsidian_knife");
		return res;
	}
}
