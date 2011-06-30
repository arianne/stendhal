/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

/**
 * Represents the conversation status of a NPC that can talk with players.
 */
public enum ConversationStates {

	/**
	 * A wildcard that always matches, regardless of the current state.
	 */
	ANY,

	/**
	 * The SpeakerNPC is waiting for a player to start a conversation.
	 */
	IDLE,

	/**
	 * The SpeakerNPC is attending one player; all prior talk is irrelevant.
	 */
	ATTENDING,

	/**
	 * The player wants to buy an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	BUY_PRICE_OFFERED,

	/**
	 * The player wants to sell an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	SELL_PRICE_OFFERED,

	/**
	 * The player wants to be healed; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	HEAL_OFFERED,

	/**
	 * The player wants the NPC to produce something; the SpeakerNPC has told
	 * about the required resources and waits for the player to accept or reject
	 * the offer.
	 */
	PRODUCTION_OFFERED,

	/**
	 * The player wants the NPC to do something; the SpeakerNPC has told about
	 * the required cash/resources and waits for the player to accept or reject
	 * the offer.
	 */
	SERVICE_OFFERED,
	
	/**
	 * The player wants the NPC to repair an item. The NPC told the price and
	 * awaits the player's response accepting or rejecting the offer.
	 */
	REPAIR_OFFERED,

	/**
	 * The SpeakerNPC is simply telling something to the player.
	 */
	INFORMATION_1,
	INFORMATION_2,
	INFORMATION_3,
	INFORMATION_4,
	INFORMATION_5,
	INFORMATION_6,
	INFORMATION_7,
	INFORMATION_8,
	INFORMATION_9,
	INFORMATION_10,
	/**
	 * The SpeakerNPC has offered a quest; the player has to accept or reject
	 * it.
	 */
	QUEST_OFFERED,

	/**
	 * The SpeakerNPC has offered a second quest; the player has to accept or
	 * reject it.
	 */
	QUEST_2_OFFERED,

	/**
	 * The player has just started the quest.
	 */
	QUEST_STARTED,

	/**
	 * The player has brought a requested item, and the SpeakerNPC has asked if
	 * it should be used for the quest.
	 */
	QUEST_ITEM_BROUGHT,

	/**
	 * The SpeakerNPC asks the player if the player has brought a requested item.
	 */
	QUEST_ITEM_QUESTION,

	/** Multi-purpose states for multiple questions. */
	QUESTION_1,

	QUESTION_2,

	QUESTION_3;
}
