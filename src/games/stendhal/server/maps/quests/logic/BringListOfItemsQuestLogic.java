package games.stendhal.server.maps.quests.logic;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An abstract quest which is based on bringing a list of items to an NPC.
 * The NPC keeps track of the items already brought to him.
 */
public class BringListOfItemsQuestLogic {
	/** the concrete quest information (which items?, which npc?, what does it say?) */
	protected BringListOfItemsQuest concreteQuest;

	/**
	 * Creates a new BringItems quest.
	 *
	 * @param concreteQuest the real quest
	 */
	public BringListOfItemsQuestLogic(BringListOfItemsQuest concreteQuest) {
		this.concreteQuest = concreteQuest;
	}

	/**
	 * Returns a list of the names of all items that the given player still
	 * has to bring to fulfil the quest.
	 *
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of weapon names
	 */
	protected List<String> getListOfStillMissingItems(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();

		String doneText = player.getQuest(concreteQuest.getSlotName());
		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		for (String weapon : concreteQuest.getNeededItems()) {
			if (!done.contains(weapon)) {
				if (hash) {
					weapon = "#" + weapon;
				}
				result.add(weapon);
			}
		}
		return result;
	}

	/**
	 * player says hi before starting the quest
	 */
	protected void welcomeNewPlayer() {
		concreteQuest.getNPC().add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.hasQuest(concreteQuest.getSlotName());
				}
			},
			ConversationStates.ATTENDING,
			concreteQuest.welcomeBeforeStartingQuest(),
			null);
	}

	/**
	 * player asks about quest
	 */
	protected void tellAboutQuest() {
 		List<String> questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		List<String> additionalTrigger = concreteQuest.getAdditionalTriggerPhraseForQuest();
		if (additionalTrigger!=null){
			questTrigger.addAll(additionalTrigger);
		}
		concreteQuest.getNPC().add(ConversationStates.ATTENDING,
			questTrigger,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text,	SpeakerNPC engine) {
					return !player.hasQuest(concreteQuest.getSlotName());
				}
			}, ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text,
						SpeakerNPC engine) {
					if (!player.isQuestCompleted(concreteQuest.getSlotName())) {
						engine.say(concreteQuest.respondToQuest());
					} else {
						engine.say(concreteQuest.respondToQuestAfterItHasAlreadyBeenCompleted());
						engine.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});
	}

	/**
	 * player is willing to help
	 */
	protected void acceptQuest() {
		concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					engine.say(concreteQuest.respondToQuestAcception());
					player.setQuest(concreteQuest.getSlotName(), "");
				}
			});
	}

	/**
	 * player is not willing to help
	 */
	protected void rejectQuest() {
		concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE,
			concreteQuest.respondToQuestRefusal(), null);
	}

	/**
	 * player asks what exactly is missing
	 */
	protected void listMissingItemsDuringQuestOffer() {
		if (concreteQuest.getTriggerPhraseToEnumerateMissingItems() != ConversationPhrases.YES_MESSAGES) {
			concreteQuest.getNPC().add(ConversationStates.QUEST_OFFERED,
				concreteQuest.getTriggerPhraseToEnumerateMissingItems(),
				null, ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						List<String> missingItems = getListOfStillMissingItems(player, false);
						engine.say(concreteQuest.askForMissingItems(missingItems));
					}
				});
		}
	}

	/**
	 * player asks what exactly is missing
	 */
	protected void listMissingItems() {
		// List missing items at the beginning of the conversation and during
		// the "giving items" states. Unless the trigger phrase is simply
		// yes. In this case it is ignored during the "giving items" state because
		// there is already a yes-trigger defined elsewhere.
		int[] states;
		if (concreteQuest.getTriggerPhraseToEnumerateMissingItems() != ConversationPhrases.YES_MESSAGES) {
			states = new int[] {ConversationStates.ATTENDING, ConversationStates.QUESTION_1};
		} else {
			states = new int[] {ConversationStates.ATTENDING};
		}

		concreteQuest.getNPC().add(states,
			concreteQuest.getTriggerPhraseToEnumerateMissingItems(),
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return player.hasQuest(concreteQuest.getSlotName())
							&& !player.isQuestCompleted(concreteQuest.getSlotName());
				}
			}, ConversationStates.QUESTION_1, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					List<String> missingItems = getListOfStillMissingItems(player, true);
					engine.say(concreteQuest.askForMissingItems(missingItems));
				}
			});
	}

	/**
	 * player says he doesn't have required items with him
	 */
	protected void playerDoesNotWantToGiveItems() {
		concreteQuest.getNPC().add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					List<String> missingItems = getListOfStillMissingItems(player, false);
					engine.say(concreteQuest.respondToPlayerSayingHeHasNoItems(missingItems));
				}
			});
	}

	/**
	 * player says he has a required weapon with him
	 */
	protected void playerWantsToGiveItems() {
		concreteQuest.getNPC().add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_1, concreteQuest.askForItemsAfterPlayerSaidHeHasItems(),
			null);
	}

	/**
	 * player offers an item
	 */
	protected void offerItem() {
		concreteQuest.getNPC().add(ConversationStates.QUESTION_1, concreteQuest.getNeededItems(), null,
			ConversationStates.QUESTION_1, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					if (!concreteQuest.getNeededItems().contains(text)) {
						engine.say(concreteQuest.respondToOfferOfNotNeededItem());
						return;
					}

					List<String> missing = getListOfStillMissingItems(player, false);
					if (!missing.contains(text)) {
						engine.say(concreteQuest.respondToOfferOfNotMissingItem());
						return;
					}

					if (!player.drop(text)) {
						engine.say(concreteQuest.respondToOfferOfNotExistingItem(text));
						return;
					}

					// register weapon as done
					String doneText = player.getQuest(concreteQuest.getSlotName());
					player.setQuest(concreteQuest.getSlotName(), doneText + ";" + text);
					// check if the player has brought all weapons
					missing = getListOfStillMissingItems(player, true);
					if (missing.size() > 0) {
						engine.say(concreteQuest.respondToItemBrought());
					} else {
						concreteQuest.rewardPlayer(player);
						player.notifyWorldAboutChanges();
						engine.say(concreteQuest.respondToLastItemBrought());
						player.setQuest(concreteQuest.getSlotName(), "done");
						engine.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});
	}

	/**
	 * player returns while quest is still active
	 */
	protected void welcomeKnownPlayer() {
		concreteQuest.getNPC().add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text,
						SpeakerNPC engine) {
					return player.hasQuest(concreteQuest.getSlotName())
							&& !player.isQuestCompleted(concreteQuest.getSlotName());
				}
			},
			ConversationStates.ATTENDING,
			concreteQuest.welcomeDuringActiveQuest(),
			null);
	}

	/**
	 * player returns after finishing the quest
	 */
	protected void welcomePlayerAfterQuest() {
		if (concreteQuest.shouldWelcomeAfterQuestIsCompleted()) {
			concreteQuest.getNPC().add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.isQuestCompleted(concreteQuest.getSlotName());
					}
				},
				ConversationStates.ATTENDING,
				concreteQuest.welcomeAfterQuestIsCompleted(),
				null);
		}
	}

	/**
	 * Adds the quest to the world
	 */
	public void addToWorld() {

		// talk about quest
		welcomeNewPlayer();
		tellAboutQuest();
		listMissingItemsDuringQuestOffer();
		acceptQuest();
		rejectQuest();

		// accept items
		welcomeKnownPlayer();
		listMissingItems();
		playerDoesNotWantToGiveItems();
		playerWantsToGiveItems();
		offerItem();

		welcomePlayerAfterQuest();
	}
}
