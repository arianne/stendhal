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
package games.stendhal.server.maps.quests.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;

/**
 * An abstract quest which is based on bringing a list of items to an NPC.
 * The NPC keeps track of the items already brought to him.
 */
public class BringListOfItemsQuestLogic {
	/** The concrete quest information (which items?, which npc?, what does it say?). */
	protected BringListOfItemsQuest concreteQuest;

	/**
	 * Creates a new BringItems quest.
	 *
	 * @param concreteQuest the real quest
	 */
	public BringListOfItemsQuestLogic(final BringListOfItemsQuest concreteQuest) {
		this.concreteQuest = concreteQuest;
	}

	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(concreteQuest.getSlotName())) {
			return res;
		}
		final String npcName = concreteQuest.getNPC().getName();
		final String questState = player.getQuest(concreteQuest.getSlotName());
		if (!"done".equals(questState)) {
			res.add("I need to collect " + Grammar.enumerateCollection(getListOfStillMissingItems(player, false)) +  " for " + npcName + ".");
		} else {
			res.add("I collected everything that " +  npcName + " needed!");
		}
		return res;
	}

	/**
	 * Returns a list of the names of all items that the given player still
	 * has to bring to fulfil the quest.
	 *
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name and puts it in quotes
	 * @return A list of item names
	 */
	protected List<String> getListOfStillMissingItems(final Player player, final boolean hash) {
		final List<String> result = new LinkedList<String>();

		String doneText = player.getQuest(concreteQuest.getSlotName());
		if (doneText == null) {
			doneText = "";
		}

		final List<String> done = Arrays.asList(doneText.split(";"));
		for (String item : concreteQuest.getNeededItems()) {
			if (!done.contains(item)) {
				if (hash) {
					item = "#" + item;
				}
				result.add(item);
			}
		}

		return result;
	}

	/**
	 * player says 'hi' before starting the quest.
	 */
	protected void welcomeNewPlayer() {
		concreteQuest.getNPC().add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(concreteQuest.getNPC().getName()),
					new QuestNotStartedCondition(concreteQuest.getSlotName())),
			ConversationStates.ATTENDING,
			concreteQuest.welcomeBeforeStartingQuest(),
			null);
	}

	/**
	 * Player asks about quest.
	 */
	protected void tellAboutQuest() {
 		final List<String> questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		final List<String> additionalTrigger = concreteQuest.getAdditionalTriggerPhraseForQuest();
		if (additionalTrigger != null) {
			questTrigger.addAll(additionalTrigger);
		}
		concreteQuest.getNPC().add(ConversationStates.ATTENDING,
			questTrigger,
			new QuestNotStartedCondition(concreteQuest.getSlotName()),
			ConversationStates.QUEST_OFFERED,
			concreteQuest.respondToQuest(),	null);
	}

	/**
	 * Player is willing to help.
	 */
	protected void acceptQuest() {
		concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, concreteQuest.respondToQuestAcception(),
			new SetQuestAction(concreteQuest.getSlotName(), ""));
	}

	/**
	 * Player is not willing to help.
	 */
	protected void rejectQuest() {
		concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE,
			concreteQuest.respondToQuestRefusal(),
			new DecreaseKarmaAction(concreteQuest.getKarmaDiffForQuestResponse()));
	}

	/**
	 * Player asks what exactly is missing.
	 */
	protected void listMissingItemsDuringQuestOffer() {
		if (concreteQuest.getTriggerPhraseToEnumerateMissingItems() != ConversationPhrases.EMPTY) {
			concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED,
				concreteQuest.getTriggerPhraseToEnumerateMissingItems(),
				null, ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final List<String> missingItems = getListOfStillMissingItems(player, false);
						raiser.say(concreteQuest.firstAskForMissingItems(missingItems));
					}

					@Override
					public String toString() {
						return "list items";
					}
				});
		}
	}

	/**
	 * Player asks what exactly is missing.
	 */
	protected void listMissingItems() {
		// List missing items at the beginning of the conversation and during
		// the "giving items" states. Unless the trigger phrase list is empty
		// In this case it is ignored during the "giving items" state because
		// there is already a yes-trigger defined elsewhere.
		ConversationStates[] states;
		if (concreteQuest.getTriggerPhraseToEnumerateMissingItems() != ConversationPhrases.EMPTY) {
			states = new ConversationStates[] {ConversationStates.ATTENDING, ConversationStates.QUESTION_1};
		} else {
			states = new ConversationStates[] {ConversationStates.ATTENDING};
		}

		concreteQuest.getNPC().add(states,
			concreteQuest.getTriggerPhraseToEnumerateMissingItems(),
			new QuestActiveCondition(concreteQuest.getSlotName()),
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final List<String> missingItems = getListOfStillMissingItems(player, true);
					raiser.say(concreteQuest.askForMissingItems(missingItems));
				}

				@Override
				public String toString() {
					return "list items as triggers";
				}
			});
	}

	/**
	 * Player says he doesn't have required items with him.
	 */
	protected void playerDoesNotWantToGiveItems() {
	    final ConversationStates[] states = new ConversationStates[] {ConversationStates.ATTENDING, ConversationStates.QUESTION_1};
		concreteQuest.getNPC().add(states, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final List<String> missingItems = getListOfStillMissingItems(player, false);
					raiser.say(concreteQuest.respondToPlayerSayingHeHasNoItems(missingItems));
				}

				@Override
				public String toString() {
					return "respond to player not wanting to give any items";
				}
			});
	}

	/**
	 * Player says he has a required item with him.
	 */
	protected void playerWantsToGiveItems() {
		final ConversationStates[] states = new ConversationStates[] {ConversationStates.ATTENDING, ConversationStates.QUESTION_1};
		concreteQuest.getNPC().add(states,
			ConversationPhrases.YES_MESSAGES,
			new QuestActiveCondition(concreteQuest.getSlotName()),
			ConversationStates.QUESTION_1, concreteQuest.askForItemsAfterPlayerSaidHeHasItems(),
			null);
	}

	/**
	 * Player offers an item.
	 */
	protected void offerItem() {
		for(final String itemName : concreteQuest.getNeededItems()) {
			concreteQuest.getNPC().add(ConversationStates.QUESTION_1, itemName, null,
				ConversationStates.QUESTION_1, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						List<String> missing = getListOfStillMissingItems(player, false);

						if (missing.contains(itemName)) {
						    if (!player.drop(itemName)) {
        						raiser.say(concreteQuest.respondToOfferOfNotExistingItem(itemName));
        						return;
						    }

						    // register item as done
						    final String doneText = player.getQuest(concreteQuest.getSlotName());
						    player.setQuest(concreteQuest.getSlotName(), doneText + ";" + itemName);

						    // check if the player has brought all items
						    missing = getListOfStillMissingItems(player, false);

						    if (!missing.isEmpty()) {
        						raiser.say(concreteQuest.respondToItemBrought());
						    } else {
        						concreteQuest.rewardPlayer(player);
        						player.notifyWorldAboutChanges();
        						raiser.say(concreteQuest.respondToLastItemBrought());
        						player.setQuest(concreteQuest.getSlotName(), "done");
        						raiser.setCurrentState(ConversationStates.ATTENDING);
						    }
						} else {
						    raiser.say(concreteQuest.respondToOfferOfNotMissingItem());
						}
					}

				    @Override
					public String toString() {
				    	return "accept or reject offered item";
				    }
				}
			);
		}
	}

    /**
     * player tries to offer an unwanted item
    */
    protected void offerNotNeededItem() {
	concreteQuest.getNPC().add(ConversationStates.QUESTION_1, "",
				   new NotCondition(new TriggerInListCondition(concreteQuest.getNeededItems())),
				   ConversationStates.QUESTION_1,
				   concreteQuest.respondToOfferOfNotNeededItem(),
				   null);
    }

    /**
     * player tries to say bye
    */
		// allow to say goodbye while listening for items
    protected void sayByeWhileInQuestion1() {
	concreteQuest.getNPC().add(ConversationStates.QUESTION_1, ConversationPhrases.GOODBYE_MESSAGES, null,
		ConversationStates.IDLE, "Bye.", null);
    }

    /**
	 * Player returns while quest is still active.
	 */
	protected void welcomeKnownPlayer() {
		concreteQuest.getNPC().add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(concreteQuest.getNPC().getName()),
					new QuestActiveCondition(concreteQuest.getSlotName())),
			ConversationStates.ATTENDING,
			concreteQuest.welcomeDuringActiveQuest(),
			null);
	}

	/**
	 * Player returns after finishing the quest.
	 */
	protected void welcomePlayerAfterQuest() {
		if (concreteQuest.shouldWelcomeAfterQuestIsCompleted()) {
			concreteQuest.getNPC().add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(concreteQuest.getNPC().getName()),
						new QuestCompletedCondition(concreteQuest.getSlotName())),
				ConversationStates.ATTENDING,
				concreteQuest.welcomeAfterQuestIsCompleted(),
				null);
		}
	}

	/**
	 * Adds the quest to the world.
	 */
	public void addToWorld() {

		// talk about quest
		welcomeNewPlayer();
		tellAboutQuest();
		listMissingItemsDuringQuestOffer();
		acceptQuest();
		rejectQuest();

		// accept items
		welcomeKnownPlayer();
		listMissingItems();
		playerDoesNotWantToGiveItems();
		playerWantsToGiveItems();
		offerItem();

	        offerNotNeededItem();
		sayByeWhileInQuestion1();
		welcomePlayerAfterQuest();
	}
}
