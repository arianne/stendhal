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
	protected List<String> missingItems(Player player, boolean hash) {
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

	private void step_1() {
		SpeakerNPC npc = concreteQuest.getNPC();

		// player says hi before starting the quest
		npc.add(
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

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
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

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say(concreteQuest.respondToQuestAcception());
						player.setQuest(concreteQuest.getSlotName(), "");
					}
				});

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				concreteQuest.respondToQuestRefusal(), null);

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING, concreteQuest.getTriggerPhraseToEnumerateMissingItems(),
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
						List<String> missingItems = missingItems(player, true);
						engine.say(concreteQuest.askForMissingItems(missingItems));
					}
				});

		// player says he doesn't have required weapons with him
		npc.add(ConversationStates.QUESTION_1, "no", null,
				ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						List<String> missingItems = missingItems(player, false);
						engine.say(concreteQuest.respondToPlayerSayingHeHasNoItems(missingItems));
					}
				});

		// player says he has a required weapon with him
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, concreteQuest.askForItemsAfterPlayerSaidHeHasItems(),
				null);

		for (String weapon : concreteQuest.getNeededItems()) {
			npc.add(ConversationStates.QUESTION_1, weapon, null,
					ConversationStates.QUESTION_1, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text, SpeakerNPC engine) {
							if (!concreteQuest.getNeededItems().contains(text)) {
								engine.say(concreteQuest.respondToOfferOfNotNeededItem());
								return;
							}

							List<String> missing = missingItems(player, false);
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
							missing = missingItems(player, true);
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
	}

	private void step_2() {
		// Just find some of the items somewhere and bring them to the NPC.
	}

	private void step_3() {
		SpeakerNPC npc = concreteQuest.getNPC();

		// player returns while quest is still active
		npc.add(
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

		// player returns after finishing the quest
		if (concreteQuest.shouldWelcomeAfterQuestIsCompleted()) {
			npc.add(ConversationStates.IDLE,
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
		step_1();
		step_2();
		step_3();
	}
}
