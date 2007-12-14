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
 * 
 * @author Daniel Herding
 */
public class ConversationStates {

	/**
	 * A wildcard that always matches, regardless of the current state
	 */
	public static final int ANY = -1;

	/**
	 * The SpeakerNPC is waiting for a player to start a conversation,
	 */
	public static final int IDLE = 0;

	/**
	 * The SpeakerNPC is attending one player; all prior talk is irrelevant.
	 */
	public static final int ATTENDING = 1;

	/**
	 * The player wants to buy an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	public static final int BUY_PRICE_OFFERED = 20;

	/**
	 * The player wants to sell an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	public static final int SELL_PRICE_OFFERED = 30;

	/**
	 * The player wants to be healed; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	public static final int HEAL_OFFERED = 40;

	/**
	 * The player wants the NPC to produce something; the SpeakerNPC has told
	 * about the required resources and waits for the player to accept or reject
	 * the offer.
	 */
	public static final int PRODUCTION_OFFERED = 45;

	/**
	 * The player wants the NPC to do something; the SpeakerNPC has told about
	 * the required cash/resources and waits for the player to accept or reject
	 * the offer.
	 */
	public static final int SERVICE_OFFERED = 46;

	/**
	 * The SpeakerNPC is simply telling something to the player.
	 */
	public static final int INFORMATION_1 = 50;

	public static final int INFORMATION_2 = 51;

	public static final int INFORMATION_3 = 52;

	public static final int INFORMATION_4 = 53;

	public static final int INFORMATION_5 = 54;

	public static final int INFORMATION_6 = 55;

	public static final int INFORMATION_7 = 56;

	public static final int INFORMATION_8 = 57;

	public static final int INFORMATION_9 = 58;

	public static final int INFORMATION_10 = 59;

	/**
	 * The SpeakerNPC has offered a quest; the player has to accept or reject
	 * it.
	 */
	public static final int QUEST_OFFERED = 60;

	/**
	 * The SpeakerNPC has offered a second quest; the player has to accept or
	 * reject it.
	 */
	public static final int QUEST_2_OFFERED = 65;

	/**
	 * The player has just started the quest.
	 */
	public static final int QUEST_STARTED = 61;

	/**
	 * The player has brought a requested item, and the SpeakerNPC has asked if
	 * it should be used for the quest.
	 */
	public static final int QUEST_ITEM_BROUGHT = 62;

	/**
	 * The SpeakerNPC asks the player if the player has brought a requested item
	 */
	public static final int QUEST_ITEM_QUESTION = 63;

	/** Multi-purpose states for multiple questions */
	public static final int QUESTION_1 = 70;

	public static final int QUESTION_2 = 71;

	public static final int QUESTION_3 = 72;
}
