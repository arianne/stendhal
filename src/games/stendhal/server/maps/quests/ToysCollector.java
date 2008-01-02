package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.rule.RuleManager;
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
 * <p>
 * PARTICIPANTS:
 * <li> Anna, a girl who live in Ados
 * <p>
 * STEPS:
 * <li> Anna asks for some toys
 * <li> You guess she might like a teddy, dice or dress
 * <li> You bring the toy to Anna
 * <li> Repeat until Anna received all toys. (Of course you can bring several
 * toys at the same time.)
 * <li> Anna gives you a reward
 * <p>
 * REWARD:
 * <li> ? some pies?
 * <li> 100 XP
 * <p>
 * REPETITIONS: - None.
 */
public class ToysCollector extends AbstractQuest implements
		BringListOfItemsQuest {

	private static final String QUEST_SLOT = "toys_collector";
	
	private static final List<String> neededToys = 
		Arrays.asList("teddy", "dice", "dress");

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void setupAbstractQuest() {
		BringListOfItemsQuest concreteQuest = this;
		BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(
				concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		setupAbstractQuest();
		specialStuff();
	}

	private void specialStuff() {
		getNPC().add(
				ConversationStates.ATTENDING,
				"no",
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
		return "Mummy said, we are not allowed to talk to strangers. She is worried about that lost girl. But I'm bored. I want some #toys!";
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

	public String askForMissingItems(List<String> missingItems) {
		return "What toys did you bring?";
	}

	public String respondToPlayerSayingHeHasNoItems(List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "What did you bring?!";
	}

	public String respondToItemBrought() {
		return "Thank you very much! What else did you bring";
	}

	public String respondToLastItemBrought() {
		return "These toys will keep me happy for ages! Please take these pies. Arlindo baked them for us but I think you should have them.";
	}

	public void rewardPlayer(Player player) {
		RuleManager ruleManager = StendhalRPWorld.get().getRuleManager();
		StackableItem pie = (StackableItem) ruleManager.getEntityManager().getItem(
				"pie");
		pie.setQuantity(3);
		player.equip(pie, true);
		player.addXP(100);
		player.addKarma(10.0);
	}

	public String respondToOfferOfNotExistingItem(String itemName) {
		return "Hey! It's bad to lie! You don't have "
				+ Grammar.a_noun(itemName) + " with you.";
	}

	public String respondToOfferOfNotMissingItem() {
		return "I already have that toy!";
	}

	public String respondToOfferOfNotNeededItem() {
		return "That's not a good toy!";
	}
}
