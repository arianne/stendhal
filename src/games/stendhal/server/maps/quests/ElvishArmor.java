package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

import java.util.Arrays;
import java.util.List;

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

	
	private static final List<String> NEEDEDITEMS = Arrays.asList(
			"elvish armor", "elvish legs", "elvish boots", "elvish sword",
			"elvish cloak", "elvish shield");

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	


	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		final BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

  	@Override
	public void addToWorld() {
		super.addToWorld();
		offerSteps();
		setupAbstractQuest();
	}

	public SpeakerNPC getNPC() {
		return npcs.get("Lupos");
	}

	public List<String> getNeededItems() {
		return NEEDEDITEMS;
	}

	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return Arrays.asList("equipment");
	}

	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("secrets");
	}

	public double getKarmaDiffForQuestResponse() {
		return 5.0;
	}

	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return true;
	}

	public String welcomeBeforeStartingQuest() {
		return "Greetings, traveller. I see that you have come far to be here. "
			+ "I am interested in anyone who has encountered our kin, the green elves of Nalwor. They guard their #secrets closely.";
	}

	public String respondToQuest() {
		return "They won't share knowledge of how to create the green armor, shields and the like. You would call them elvish items. "
			+ "I wonder if a traveller like you could bring me any?";
	}

	public String respondToQuestAcception() {
		return "The secrets of the green elves shall be ours at last! Bring me all elvish equipment you can find, I'll reward you well!";
	}

	public String respondToQuestRefusal() {
		return "Another unhelpful soul, I see.";
	}

	public String welcomeDuringActiveQuest() {
		return "Hello! I hope your search for elvish #equipment is going well?";
	}

	// this one not actually used here
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I have heard descriptions of "
								+ Grammar.quantityplnoun(missingItems.size(), "item", "a")
								+ " in all. They are: "
								+ Grammar.enumerateCollection(missingItems)
								+ ". Will you collect them?";
	}

	public String askForMissingItems(final List<String> missingItems) {
		return "I have heard descriptions of "
								+ Grammar.quantityplnoun(missingItems.size(), "item", "a")
								+ " in all. They are: "
								+ Grammar.enumerateCollection(missingItems)
								+ ". Have you looted any?";
	}

	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "Felicitations! What #equipment did you pillage?";
	}

	public String respondToItemBrought() {
		return "Excellent work. Is there more that you plundered?";
	}


	public String respondToLastItemBrought() {
		return "I will study these! The albino elves owe you a debt of thanks.";
	}
								
	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Liar! You don't really have "
										+ Grammar.a_noun(itemName)
										+ " with you.";
	}
	public String respondToOfferOfNotMissingItem() {
		return "You've already brought that elvish item to me.";
	}

	public String respondToOfferOfNotNeededItem() {
		return	"I don't think that's a piece of elvish armor...";
	}

	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "I understand, the green elves protect themselves well. If there's anything else I can do for you, just say.";
	}

	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "I'm now busy studying the properties of the elvish armor you brought me. It really is intriguing. Until I can reproduce it, I would buy similar items from you."; 
	}

	public void rewardPlayer(final Player player) {
		player.addKarma(20.0);
		player.addXP(20000);
	}

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
				"If you have found any more elvish items, I'd be glad if you would #sell them to me. I would buy elvish armor, shield, legs, boots, cloak or sword. I would also buy a drow sword if you have one.",
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
}
