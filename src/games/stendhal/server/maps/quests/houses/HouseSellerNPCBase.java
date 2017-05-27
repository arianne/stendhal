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
package games.stendhal.server.maps.quests.houses;

import java.util.Arrays;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Base class for dialogue shared by all houseseller NPCs.
 *
 */
abstract class HouseSellerNPCBase extends SpeakerNPC {

	static final String QUEST_SLOT = "house";
	/**
	 * age required to buy a house. Note, age is in minutes, not seconds! So
	 * this is 300 hours.
	 */
	static final int REQUIRED_AGE = 300 * 60;
	/** percentage of initial cost refunded when you resell a house.*/
	private static final int DEPRECIATION_PERCENTAGE = 40;

	private final String location;

	private final HouseTax houseTax;
	/**
	 *	Creates NPC dialog for house sellers.
	 * @param name
	 *            the name of the NPC
	 * @param location
	 *            where are the houses?
	 * @param houseTax
	 * 		      class which controls house tax, and confiscation of houses
	*/
	HouseSellerNPCBase(final String name, final String location, final HouseTax houseTax) {
		super(name);
		this.location = location;
		this.houseTax =  houseTax;
		createDialogNowWeKnowLocation();
	}

	@Override
	protected abstract void createPath();

	private void createDialogNowWeKnowLocation() {
		addGreeting(null, new HouseSellerGreetingAction(QUEST_SLOT));

			// quest slot 'house' is started so player owns a house
		add(ConversationStates.ATTENDING,
			Arrays.asList("cost", "house", "buy", "purchase"),
			new PlayerOwnsHouseCondition(),
			ConversationStates.ATTENDING,
			"As you already know, the cost of a new house is "
				+ getCost()
			+ " money. But you cannot own more than one house, the market is too demanding for that! You cannot own another house until you #resell the one you already own.",
			null);

		// we need to warn people who buy spare keys about the house
		// being accessible to other players with a key
		add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.QUESTION_2,
			"Before we go on, I must warn you that anyone with a key to your house can enter it, and access the items in the chest in your house. Do you still wish to buy a spare key?",
			null);

		// player wants spare keys and is OK with house being accessible
		// to other person.
		add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new BuySpareKeyChatAction(QUEST_SLOT));

		// refused offer to buy spare key for security reasons
		add(ConversationStates.QUESTION_2,
			ConversationPhrases.NO_MESSAGES,
			null,
				ConversationStates.ATTENDING,
			"That is wise of you. It is certainly better to restrict use of your house to those you can really trust.",
			null);

		// refused offer to buy spare key
		add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"No problem! Just so you know, if you need to #change your locks, I can do that, and you can also #resell your house to me if you want to.",
			null);

		// player is eligible to resell a house
		add(ConversationStates.ATTENDING,
			Arrays.asList("resell", "sell"),
			new PlayerOwnsHouseCondition(),
				ConversationStates.QUESTION_3,
			"The state will pay you "
			+ Integer.toString(DEPRECIATION_PERCENTAGE)
			+ " percent of the price you paid for your house, minus any taxes you owe. You should remember to collect any belongings from your house before you sell it. Do you really want to sell your house to the state?",
			null);

		// player is not eligible to resell a house
		add(ConversationStates.ATTENDING,
			Arrays.asList("resell", "sell"),
			new NotCondition(new PlayerOwnsHouseCondition()),
			ConversationStates.ATTENDING,
			"You don't own any house at the moment. If you want to buy one please ask about the #cost.",
			null);

		add(ConversationStates.QUESTION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
				ConversationStates.ATTENDING,
			null,
			new ResellHouseAction(getCost(), QUEST_SLOT, DEPRECIATION_PERCENTAGE, houseTax));

		// refused offer to resell a house
		add(ConversationStates.QUESTION_3,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Well, I'm glad you changed your mind.",
			null);

		// player is eligible to change locks
		add(ConversationStates.ATTENDING,
			"change",
			new PlayerOwnsHouseCondition(),
			ConversationStates.SERVICE_OFFERED,
			"If you are at all worried about the security of your house or, don't trust anyone you gave a spare key to, "
			+ "it is wise to change your locks. Do you want me to change your house lock and give you a new key now?",
			null);

		// player is not eligible to change locks
		add(ConversationStates.ATTENDING,
			"change",
			new NotCondition(new PlayerOwnsHouseCondition()),
			ConversationStates.ATTENDING,
			"You don't own any house at the moment. If you want to buy one please ask about the #cost.",
			null);

		// accepted offer to change locks
		add(ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new ChangeLockAction(QUEST_SLOT));

		// refused offer to change locks
		add(ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"OK, if you're really sure. Please let me know if I can help with anything else.",
			null);

		add(ConversationStates.ANY,
			Arrays.asList("available", "unbought", "unsold"),
			null,
			ConversationStates.ATTENDING,
			null,
			new ListUnboughtHousesAction(location));

		addReply(
				 "buy",
				 "You should really enquire the #cost before you ask to buy. And check our brochure, #https://stendhalgame.org/wiki/StendhalHouses.");
		addReply("really",
				 "That's right, really, really, really. Really.");
		addOffer("I sell houses, please look at #https://stendhalgame.org/wiki/StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.");
		addHelp("You may be eligible to buy a house if there are any #available. If you can pay the #cost, I'll give you a key. As a house owner you can buy spare keys to give your friends. See #https://stendhalgame.org/wiki/StendhalHouses for pictures inside the houses and more details.");
		addQuest("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #https://stendhalgame.org/wiki/StendhalHouses.");
		addGoodbye("Goodbye.");
	}

	protected abstract int getCost();

	protected abstract int getLowestHouseNumber();
	protected abstract int getHighestHouseNumber();
}
final class PlayerOwnsHouseCondition implements ChatCondition {
	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return HouseUtilities.playerOwnsHouse(player);
	}
}
