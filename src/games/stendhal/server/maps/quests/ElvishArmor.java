/* $Id$ */
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

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

/**
 * QUEST: ElvishArmor
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Lupos, an albino elf who live in Fado Forest</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Lupos wants to see every piece of elvish equipment you can bring him</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 20000 XP</li>
 * <li> Karma:25</li>
 * <li> ability to sell elvish stuff and also drow sword</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class ElvishArmor extends AbstractQuest implements
		BringListOfItemsQuest {

	private static final String QUEST_SLOT = "elvish_armor";

	private BringListOfItemsQuestLogic bringItems;

	private static final List<String> NEEDEDITEMS = Arrays.asList(
			"elvish armor", "elvish legs", "elvish boots", "elvish sword",
			"elvish cloak", "elvish shield", "elvish hat");

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return bringItems.getHistory(player);
	}


	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

  	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Elvish Armor",
				"Lupos, an albino elf, wants to get the knowledge of how he can make elvish armor parts himself. Therefore, he asks young travellers to bring him some examples.",
				true);
		offerSteps();
		setupAbstractQuest();
	}

	@Override
	public SpeakerNPC getNPC() {
		return npcs.get("Lupos");
	}

	@Override
	public List<String> getNeededItems() {
		return NEEDEDITEMS;
	}

	@Override
	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return Arrays.asList("equipment");
	}

	@Override
	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("secrets");
	}

	@Override
	public double getKarmaDiffForQuestResponse() {
		return 5.0;
	}

	@Override
	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return true;
	}

	@Override
	public String welcomeBeforeStartingQuest() {
		return "Greetings, traveller. I see that you have come far to be here. "
			+ "I am interested in anyone who has encountered our kin, the green elves of Nalwor. They guard their #secrets closely.";
	}

	@Override
	public String respondToQuest() {
		return "They won't share knowledge of how to create the green armor, shields and the like. You would call them elvish items. "
			+ "I wonder if a traveller like you could bring me any?";
	}

	@Override
	public String respondToQuestAcception() {
		return "The secrets of the green elves shall be ours at last! Bring me all elvish equipment you can find, I'll reward you well!";
	}

	@Override
	public String respondToQuestRefusal() {
		return "Another unhelpful soul, I see.";
	}

	@Override
	public String welcomeDuringActiveQuest() {
		return "Hello! I hope your search for elvish #equipment is going well?";
	}

	// this one not actually used here
	@Override
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I have heard descriptions of "
								+ Grammar.quantityplnoun(missingItems.size(), "item", "a")
								+ " in all. They are: "
								+ Grammar.enumerateCollection(missingItems)
								+ ". Will you collect them?";
	}

	@Override
	public String askForMissingItems(final List<String> missingItems) {
		return "I have heard descriptions of "
								+ Grammar.quantityplnoun(missingItems.size(), "item", "a")
								+ " in all. They are: "
								+ Grammar.enumerateCollection(missingItems)
								+ ". Have you looted any?";
	}

	@Override
	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "Felicitations! What #equipment did you pillage?";
	}

	@Override
	public String respondToItemBrought() {
		return "Excellent work. Is there more that you plundered?";
	}


	@Override
	public String respondToLastItemBrought() {
		return "I will study these! The albino elves owe you a debt of thanks.";
	}

	@Override
	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Liar! You don't really have "
										+ Grammar.a_noun(itemName)
										+ " with you.";
	}
	@Override
	public String respondToOfferOfNotMissingItem() {
		return "You've already brought that elvish item to me.";
	}

	@Override
	public String respondToOfferOfNotNeededItem() {
		return	"I don't think that's a piece of elvish armor...";
	}

	@Override
	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "I understand, the green elves protect themselves well. If there's anything else I can do for you, just say.";
	}

	@Override
	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "I'm now busy studying the properties of the elvish armor you brought me. It really is intriguing. Until I can reproduce it, I would buy similar items from you.";
	}

	@Override
	public void rewardPlayer(final Player player) {
		player.addKarma(20.0);
		player.addXP(20000);
	}

	@Override
	public String welcomeAfterQuestIsCompleted() {
		return "Greetings again, old friend.";
	}

	// the bring list of items quest doesn't include this logic:
		// player returns when the quest is in progress and says quest
		//				"As you already know, I seek elvish #equipment.";


	private void offerSteps() {
  		final SpeakerNPC npc = npcs.get("Lupos");

		// player returns after finishing the quest and says offer
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"If you have found any more elvish items, I'd be glad if you would #sell them to me. I would buy elvish armor, shield, legs, boots, cloak, sword, or hat. I would also buy a drow sword if you have one.",
				null);


		// player returns when the quest is in progress and says offer
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I don't think I trust you well enough yet ... ", null);
	}



	@Override
	public String getName() {
		return "ElvishArmor";
	}

	@Override
	public int getMinLevel() {
		return 60;
	}

	@Override
	public String getNPCName() {
		return "Lupos";
	}

	// it's technically in Fado forest but much nearer Kirdneh city
	@Override
	public String getRegion() {
		return Region.KIRDNEH;
	}
}
