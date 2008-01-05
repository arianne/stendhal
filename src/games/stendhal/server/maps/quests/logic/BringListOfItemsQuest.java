package games.stendhal.server.maps.quests.logic;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * a quest which is based on bringing a list of item to an NPC.
 *
 * @author hendrik
 */
public interface BringListOfItemsQuest {

	/**
	 * the NPC which wants the items.
	 *
	 * @return SpeakerNPC
	 */
	SpeakerNPC getNPC();

	List<String> getNeededItems();

	String getSlotName();

	List<String> getTriggerPhraseToEnumerateMissingItems();

	List<String> getAdditionalTriggerPhraseForQuest();
	
	double getKarmaDiffForQuestResponse();

	String welcomeBeforeStartingQuest();

	String welcomeDuringActiveQuest();

	String welcomeAfterQuestIsCompleted();

	boolean shouldWelcomeAfterQuestIsCompleted();

	String respondToQuest();

	String respondToQuestAfterItHasAlreadyBeenCompleted();

	/**
	 * Note: This needs to include the trigger phrase which displays the list
	 * of missing items.
	 *
	 * @return response
	 */
	String respondToQuestAcception();

	String respondToQuestRefusal();

	String askForMissingItems(List<String> missingItems);

	String respondToPlayerSayingHeHasNoItems(List<String> missingItems);

	String askForItemsAfterPlayerSaidHeHasItems();

	String respondToItemBrought();

	String respondToLastItemBrought();

	void rewardPlayer(Player player);

	String respondToOfferOfNotExistingItem(String itemName);
	
	String respondToOfferOfNotMissingItem();

	String respondToOfferOfNotNeededItem();

}