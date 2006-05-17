package games.stendhal.server.entity.npc;

public enum ConversationStatus {
	/**
	 * A wildcard that always matches, regardless of the current state
	 */
	ANY, // was -1

	/**
	 * The SpeakerNPC is waiting for a player to start a conversation,
	 */

	IDLE, // was 0
	/**
	 * The SpeakerNPC is attending one player; all prior talk is irrelevant.
	 */
	
	ATTENDING, // was 1
	/**
	 * The player wants to buy an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	
	BUY_PRICE_OFFERED, // was 20
	/**
	 * The player wants to sell an item; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	
	SELL_PRICE_OFFERED, // was 30
	/**
	 * The player wants to be healed; the SpeakerNPC has told the price and
	 * waits for the player to accept or reject the offer.
	 */
	
	HEAL_OFFERED, // was 40
	
	/**
	 * The SpeakerNPC is simply telling something to the player.
	 */
	INFORMATION_1, // was 50
	INFORMATION_2, // was 51
	INFORMATION_3, // was 52
	INFORMATION_4, // was 53
	INFORMATION_5, // was 54
	INFORMATION_6, // was 55
	
	/**
	 * The SpeakerNPC has offered a quest; the player has to accept or reject
	 * it.
	 */
	QUEST_OFFERED, // was 60
	/**
	 * The player has just started the quest. 
	 */
	QUEST_STARTED,
	/**
	 * The player has brought a requested item, and the SpeakerNPC has asked if
	 * it should be used for the quest. 
	 */
	QUEST_ITEM_BROUGHT,
}
