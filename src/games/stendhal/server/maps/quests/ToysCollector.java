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
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

/**
 * QUEST: Toys Collector
 *
 * PARTICIPANTS: <ul>
 * <li> Anna, a girl who live in Ados </ul>
 *
 * STEPS:
 * <ul><li> Anna asks for some toys
 * <li> You guess she might like a teddy, dice or dress
 * <li> You bring the toy to Anna
 * <li> Repeat until Anna received all toys. (Of course you can bring several
 * toys at the same time.)
 * <li> Anna gives you a reward
 * </ul>
 * REWARD:<ul>
 * <li> 3 pies
 * <li> 100 XP
 * <li> 10 Karma
 * </ul>
 * REPETITIONS: <ul><li> None.</ul>
 */
public class ToysCollector extends AbstractQuest implements
		BringListOfItemsQuest {

	private static final String QUEST_SLOT = "toys_collector";

	private static final List<String> neededToys =
		Arrays.asList("teddy", "dice", "dress");

	// don't want to use the standard history for this kind of quest for anna as we dont want to say what she needs.
	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			final String questState = player.getQuest(QUEST_SLOT);
			if (!"done".equals(questState)) {
				res.add("Anna wants some toys and I need to think about what might make a little girl happy!");
			} else {
				res.add("I got some fun toys for Anna, Jens and George to play with.");
			}
			return res;
	}

	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Toys Collector",
				"Anna is bored, and searches for toys for her and her friends to play with.",
				false);
		setupAbstractQuest();
		specialStuff();
	}

	private void specialStuff() {
		getNPC().add(
				ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Then you should go away before I get in trouble for talking to you. Bye.",
				null);
	}

	@Override
	public SpeakerNPC getNPC() {
		return npcs.get("Anna");
	}

	@Override
	public List<String> getNeededItems() {
		return neededToys;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return ConversationPhrases.EMPTY;
	}

	@Override
	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("toys");
	}

	@Override
	public double getKarmaDiffForQuestResponse() {
		return 8.0;
	}

	@Override
	public String welcomeBeforeStartingQuest() {
		return "Mummy said, we are not allowed to talk to strangers. But I'm bored. I want some #toys!";
	}

	@Override
	public String welcomeDuringActiveQuest() {
		return "Hello! I'm still bored. Did you bring me toys?";
	}

	@Override
	public String welcomeAfterQuestIsCompleted() {
		return "Hi! I'm busy playing with my toys, no grown ups allowed.";
	}

	@Override
	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return true;
	}

	@Override
	public String respondToQuest() {
		return "I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?";
	}

	@Override
	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "The toys are great! Thanks!";
	}

	@Override
	public String respondToQuestAcception() {
		return "Hooray! How exciting. See you soon.";
	}

	@Override
	public String respondToQuestRefusal() {
		return "Oh ... you're mean.";
	}

	// not used
	@Override
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?";
	}

	@Override
	public String askForMissingItems(final List<String> missingItems) {
		return "What toys did you bring?";
	}

	@Override
	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	@Override
	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "What did you bring?!";
	}

	@Override
	public String respondToItemBrought() {
		return "Thank you very much! What else did you bring?";
	}

	@Override
	public String respondToLastItemBrought() {
		return "These toys will keep me happy for ages! Please take these pies. Arlindo baked them for us but I think you should have them.";
	}

	@Override
	public void rewardPlayer(final Player player) {
		final StackableItem pie = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"pie");
		pie.setQuantity(3);
		player.equipOrPutOnGround(pie);
		player.addXP(100);
		player.addKarma(10.0);
	}

	@Override
	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Hey! It's bad to lie! You don't have "
				+ Grammar.a_noun(itemName) + " with you.";
	}

	@Override
	public String respondToOfferOfNotMissingItem() {
		return "I already have that toy!";
	}

	@Override
	public String respondToOfferOfNotNeededItem() {
		return "That's not a good toy!";
	}

	@Override
	public String getName() {
		return "ToysCollector";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Anna";
	}
}
