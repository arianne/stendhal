package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuest;
import games.stendhal.server.maps.quests.logic.BringListOfItemsQuestLogic;

import java.util.Arrays;
import java.util.List;

/**
 * QUEST: Cloak Collector
 * <p>
 * PARTICIPANTS: - Josephine, a young woman who live in Ados/Fado
 * <p>
 * STEPS:
 * <ul>
 * <li> Josephine asks you to bring her a cloak in every colour available on
 * the mainland 
 * <li> You bring cloaks to Josephine 
 * <li> Repeat until Josephine
 * received all cloaks. (Of course you can bring several cloaks at the same
 * time.) 
 * <li> Josephine gives you a reward
 * </ul>
 * <p>
 * REWARD: - black cloak - 10000 XP
 * <p>
 * REPETITIONS: - None.
 */
public class CloakCollector extends AbstractQuest implements BringListOfItemsQuest {

	private static final List<String> NEEDED_CLOAKS = Arrays.asList("cloak",
			"elf cloak", "dwarf cloak", "blue elf cloak", "stone cloak",
			"green dragon cloak", "bone dragon cloak", "lich cloak",
			"vampire cloak", "blue dragon cloak");

	private static final String QUEST_SLOT = "cloaks_collector";

	private void setupAbstractQuest() {
		final BringListOfItemsQuest concreteQuest = this;
		final BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		setupAbstractQuest();
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Josephine");

		// player asks about an individual cloak before accepting the quest
		npc.add(ConversationStates.QUEST_OFFERED, NEEDED_CLOAKS, null,
				ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final String itemName = sentence.getTriggerExpression().getNormalized();
						final Item item = SingletonRepository.getEntityManager().getItem(itemName);
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("You haven't seen one before? Well, it's a ");
						
						if (item == null) {
							stringBuilder.append(itemName);
						} else {
							stringBuilder.append(ItemTools.itemNameToDisplayName(item.getItemSubclass()));
						}
						
						stringBuilder.append(". So, will you find them all?");
						engine.say(stringBuilder.toString());
					}

					@Override
					public String toString() {
						return "describe item";
					}
		});
	}

	public List<String> getAdditionalTriggerPhraseForQuest() {
		return Arrays.asList("clothes");
	}

	public SpeakerNPC getNPC() {
		return npcs.get("Josephine");
	}

	public List<String> getNeededItems() {
		return NEEDED_CLOAKS;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	public List<String> getTriggerPhraseToEnumerateMissingItems() {
		return Arrays.asList("cloaks");
	}

	public double getKarmaDiffForQuestResponse() {
		return 5.0;
	}

	public boolean shouldWelcomeAfterQuestIsCompleted() {
		return false;
	}

	public String welcomeBeforeStartingQuest() {
		return "Hi there, gorgeous! I can see you like my pretty dress. I just love #clothes...";
	}

	public String welcomeDuringActiveQuest() {
		return "Hello! Did you bring any #cloaks with you?";
	}
	
	public String welcomeAfterQuestIsCompleted() {
		return "Hi again, lovely. The cloaks still look great. Thanks!";
	}

	public String respondToQuest() {
		return "At the moment I'm obsessed with #cloaks! They come in so many colours. I want all the pretty ones!";
	}

	public String respondToQuestAcception() {
		// player.addKarma(5.0);
		return "Brilliant! I'm so excited!";
	}

	public String respondToQuestAfterItHasAlreadyBeenCompleted() {
		return "Hi again, lovely. The cloaks still look great. Thanks!";
	}

	public String respondToQuestRefusal() {
		// player.addKarma(-5.0);
		return "Oh ... you're not very friendly. Bye then.";
	}

	public String askForItemsAfterPlayerSaidHeHasItems() {
		return "Great! What #cloaks did you bring?";
	}

	public String firstAskForMissingItems(final List<String> missingItems) {
		return "I want " + Grammar.quantityplnoun(missingItems.size(), "cloak")
				+ ". That's " + Grammar.enumerateCollection(missingItems)
				+ ". Will you find them?";
	}

	public String askForMissingItems(final List<String> missingItems) {
		return "I want " + Grammar.quantityplnoun(missingItems.size(), "cloak")
				+ ". That's " + Grammar.enumerateCollection(missingItems)
				+ ". Did you bring any?";
	}

	public String respondToItemBrought() {
		return "Wow, thank you! What else did you bring?";
	}

	public String respondToLastItemBrought() {
		return "Oh, they look so beautiful all together, thank you. Please take this black cloak in return, I don't like the colour.";
	}

	public String respondToOfferOfNotExistingItem(final String itemName) {
		return "Oh, I'm disappointed. You don't really have " + Grammar.a_noun(itemName) + " with you.";
	}

	public String respondToOfferOfNotMissingItem() {
		return "You've already brought that cloak to me.";
	}

	public String respondToOfferOfNotNeededItem() {
		return "Sorry, that's not a cloak I asked you for.";
	}

	public String respondToPlayerSayingHeHasNoItems(final List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	public void rewardPlayer(final Player player) {
		final Item blackcloak = SingletonRepository.getEntityManager().getItem("black cloak");
		blackcloak.setBoundTo(player.getName());
		player.equipOrPutOnGround(blackcloak);
		player.addKarma(5.0);
		player.addXP(10000);
	}

	@Override
	public String getName() {
		return "CloakCollector";
	}
	
	// You can start collecting just with a simple cloak which you can buy, but maybe not a good idea to send to Fado too early.
	@Override
	public int getMinLevel() {
		return 15;
	}

}
