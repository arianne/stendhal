package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
 * REWARD: - black cloak - 2500 XP
 * <p>
 * REPETITIONS: - None.
 */
public class CloakCollector extends AbstractQuest implements BringListOfItemsQuest {

	private static final List<String> NEEDED_CLOAKS = Arrays.asList("cloak",
			"elf_cloak", "dwarf_cloak", "blue_elf_cloak", "stone_cloak",
			"green_dragon_cloak", "bone_dragon_cloak", "lich_cloak",
			"vampire_cloak", "blue_dragon_cloak");

	private static final String QUEST_SLOT = "cloaks_collector";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void setupAbstractQuest() {
		BringListOfItemsQuest concreteQuest = this;
		BringListOfItemsQuestLogic bringItems = new BringListOfItemsQuestLogic(concreteQuest);
		bringItems.addToWorld();
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		setupAbstractQuest();
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Josephine");

		// player asks about an individual cloak before accepting the quest
		npc.add(ConversationStates.QUEST_OFFERED, NEEDED_CLOAKS, null,
				ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						engine.say("You haven't seen one before? Well, it's a "
									+ StendhalRPWorld.get().getRuleManager().getEntityManager()
											.getItem(sentence.toString()).getItemSubclass()
									+ ". So, will you find them all?");
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

	public String askForMissingItems(List<String> missingItems) {
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

	public String respondToOfferOfNotExistingItem(String itemName) {
		return "Oh, I'm disappointed. You don't really have " + Grammar.a_noun(itemName) + " with you.";
	}

	public String respondToOfferOfNotMissingItem() {
		return "You've already brought that cloak to me.";
	}

	public String respondToOfferOfNotNeededItem() {
		return "That's not a real cloak...";
	}

	public String respondToPlayerSayingHeHasNoItems(List<String> missingItems) {
		return "Okay then. Come back later.";
	}

	public void rewardPlayer(Player player) {
		Item blackcloak = StendhalRPWorld.get()
				.getRuleManager()
				.getEntityManager().getItem("black_cloak");
		blackcloak.setBoundTo(player.getName());
		player.equip(blackcloak, true);
		player.addKarma(5.0);
		player.addXP(2500);
	}


}
