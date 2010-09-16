package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

import java.util.Arrays;
import java.util.List;

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

	

	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		final BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(
				concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Toys Collector",
				"Try to find some toys for Anna and her friends.",
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

	public SpeakerNPC getNPC() {
		return npcs.get("Anna");
	}

	public List<String> getNeededItems() {
		return neededToys;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return ConversationPhrases.EMPTY;
	}

	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("toys");
	}

	public double getKarmaDiffForQuestResponse() {
		return 8.0;
	}

	public String welcomeBeforeStartingQuest() {
		return "Mummy said, we are not allowed to talk to strangers. But I'm bored. I want some #toys!";
	}

	public String welcomeDuringActiveQuest() {
		return "Hello! I'm still bored. Did you bring me toys?";
	}

	public String welcomeAfterQuestIsCompleted() {
		return "Hi! I'm busy playing with my toys, no grown ups allowed.";
	}

	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return true;
	}

	public String respondToQuest() {
		return "I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?";
	}

	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "The toys are great! Thanks!";
	}

	public String respondToQuestAcception() {
		return "Hooray! How exciting. See you soon.";
	}

	public String respondToQuestRefusal() {
		return "Oh ... you're mean.";
	}
	
	// not used
	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?";
	}
	
	public String askForMissingItems(final List<String> missingItems) {
		return "What toys did you bring?";
	}

	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "What did you bring?!";
	}

	public String respondToItemBrought() {
		return "Thank you very much! What else did you bring?";
	}

	public String respondToLastItemBrought() {
		return "These toys will keep me happy for ages! Please take these pies. Arlindo baked them for us but I think you should have them.";
	}

	public void rewardPlayer(final Player player) {
		final StackableItem pie = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"pie");
		pie.setQuantity(3);
		player.equipOrPutOnGround(pie);
		player.addXP(100);
		player.addKarma(10.0);
	}

	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Hey! It's bad to lie! You don't have "
				+ Grammar.a_noun(itemName) + " with you.";
	}

	public String respondToOfferOfNotMissingItem() {
		return "I already have that toy!";
	}

	public String respondToOfferOfNotNeededItem() {
		return "That's not a good toy!";
	}

	@Override
	public String getName() {
		return "ToysCollector";
	}
}
