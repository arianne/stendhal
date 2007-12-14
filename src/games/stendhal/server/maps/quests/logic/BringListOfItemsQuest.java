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
	 * the NPC which wants the items
	 *
	 * @return SpeakerNPC
	 */
	public SpeakerNPC getNPC();

	public List<String> getNeededItems();

	public String getSlotName();

	public List<String> getTriggerPhraseToEnumerateMissingItems();

	public List<String> getAdditionalTriggerPhraseForQuest();
	
	public double getKarmaDiffForQuestResponse();

	public String welcomeBeforeStartingQuest();

	public String welcomeDuringActiveQuest();

	public String welcomeAfterQuestIsCompleted();

	public boolean shouldWelcomeAfterQuestIsCompleted();

	public String respondToQuest();

	public String respondToQuestAfterItHasAlreadyBeenCompleted();

	/**
	 * Note: This needs to include the trigger phrase which displays the list
	 * of missing items.
	 *
	 * @return response
	 */
	public String respondToQuestAcception();

	public String respondToQuestRefusal();

	public String askForMissingItems(List<String> missingItems);

	public String respondToPlayerSayingHeHasNoItems(List<String> missingItems);

	public String askForItemsAfterPlayerSaidHeHasItems();

	public String respondToItemBrought();

	public String respondToLastItemBrought();

	public void rewardPlayer(Player player);

	public String respondToOfferOfNotExistingItem(String itemName);
	
	public String respondToOfferOfNotMissingItem();

	public String respondToOfferOfNotNeededItem();

}