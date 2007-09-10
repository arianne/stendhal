package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
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
public class CloakCollector extends AbstractQuest {

	private static final List<String> NEEDEDCLOAKS = Arrays.asList("cloak",
			"elf_cloak", "dwarf_cloak", "blue_elf_cloak", "stone_cloak",
			"green_dragon_cloak", "bone_dragon_cloak", "lich_cloak",
			"vampire_cloak", "blue_dragon_cloak");

	/**
	 * Returns a list of the names of all cloaks that the given player still has
	 * to bring to fulfil the quest.
	 *
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of cloak names
	 */
	private List<String> missingcloaks(Player player, boolean hash) {

		String doneText = player.getQuest("cloaks_collector");
		List<String> neededCopy = new LinkedList<String>(NEEDEDCLOAKS);

		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		neededCopy.removeAll(done);
		if (hash) {
			List<String> result = new LinkedList<String>();
			for (String cloak : neededCopy) {
				result.add("#" + cloak);
			}
			return result;
		}

		return neededCopy;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Josephine");

		// player says hi before starting the quest
		npc
				.add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new SpeakerNPC.ChatCondition() {
							@Override
							public boolean fire(Player player, String text,
									SpeakerNPC engine) {
								return !player.hasQuest("cloaks_collector");
							}
						},
						ConversationStates.ATTENDING,
						"Hi there, gorgeous! I can see you like my pretty dress. I just love #clothes...",
						null);

		npc.add(ConversationStates.ATTENDING, "clothes",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !player.hasQuest("cloaks_collector");
					}
				}, ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("cloaks_collector")) {
							engine
									.say("At the moment I'm obsessed with #cloaks! They come in so many colours. I want all the pretty ones!");
						} else { // to be honest i don't understand when this
							// would be implemented. i put the text i
							// want down in stage 3 and it works fine.
							engine.say("The cloaks are great! Thanks!");
							engine
									.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});
		// player asks what cloaks are needed
		npc.add(ConversationStates.QUEST_OFFERED, "cloaks", null,
				ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						List<String> needed = missingcloaks(player, true);
						engine.say("I want "
								+ Grammar
										.quantityplnoun(needed.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed)
								+ ". Will you find them?");
					}
				});
		// player says yes
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						engine.say("Brilliant! I'm so excited! Bye!");
						player.setQuest("cloaks_collector", "");
						player.addKarma(5.0);
					}
				});

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, "no", null,
				ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						engine
								.say("Oh ... you're not very friendly. Bye then.");
						player.addKarma(-5.0);
					}
				});

		// player asks about an individual cloak
		for (String cloak : NEEDEDCLOAKS) {
			npc.add(ConversationStates.QUEST_OFFERED, cloak, null,
					ConversationStates.QUEST_OFFERED, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							engine
									.say("You haven't seen one before? Well, it's a "
											+ StendhalRPWorld.get()
													.getRuleManager()
													.getEntityManager()
													.getItem(text)
													.getItemSubclass()
											+ ". So, will you find them all?");
						}
					});
		}
	}

	private void step_2() {
		// Just find the cloaks and bring them to Josephine.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Josephine");

		// player returns while quest is still active
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return player.hasQuest("cloaks_collector")
								&& !player.isQuestCompleted("cloaks_collector");
					}
				}, ConversationStates.QUESTION_1,
				"Hello! Did you bring any #cloaks with you?", null);
		// player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_1, "cloaks", null,
				ConversationStates.QUESTION_1, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						List<String> needed = missingcloaks(player, true);
						engine.say("I want "
								+ Grammar
										.quantityplnoun(needed.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed)
								+ ". Did you bring any?");
					}
				});
		// player says he has a required cloak with him
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1,
				"Great! What #cloaks did you bring?", null);

		for (String cloak : NEEDEDCLOAKS) {
			npc.add(ConversationStates.QUESTION_1, cloak, null,
					ConversationStates.QUESTION_1, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							List<String> missing = missingcloaks(player, false);

							if (missing.contains(text)) {
								if (player.drop(text)) {
									// register cloak as done
									String doneText = player
											.getQuest("cloaks_collector");
									player.setQuest("cloaks_collector",
											doneText + ";" + text);
									// check if the player has brought all
									// cloaks
									missing = missingcloaks(player, true);
									if (!missing.isEmpty()) {
										engine
												.say("Wow, thank you! What else did you bring?");
									} else {
										rewardPlayer(player);
										engine
												.say("Oh, they look so beautiful all together, thank you. Please take this black cloak in return, I don't like the colour.");
										player.setQuest("cloaks_collector",
												"done");
										player.notifyWorldAboutChanges();
										engine
												.setCurrentState(ConversationStates.ATTENDING);
									}
								} else {
									engine
											.say("Oh, I'm disappointed. You don't really have "
													+ Grammar.a_noun(text)
													+ " with you.");
								}
							} else {
								engine
										.say("You've already brought that cloak to me.");
							}
						}


					});
		}

		npc.add(ConversationStates.QUESTION_1, "",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !NEEDEDCLOAKS.contains(text);
					}
				}, ConversationStates.QUESTION_1, "That's not a real cloak...",
				null);

		npc.add(ConversationStates.ATTENDING, "no",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !player.isQuestCompleted("cloaks_collector");
					}
				}, ConversationStates.ATTENDING,
				"Ok. If you want help, just say.", null);

		// player says he didn't bring any cloaks to different question
		npc.add(ConversationStates.QUESTION_1, Arrays.asList("no", "nothing"),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return !player.isQuestCompleted("cloaks_collector");
					}
				}, ConversationStates.ATTENDING, "Okay then. Come back later.",
				null);

		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return player.isQuestCompleted("cloaks_collector");
					}
				}, ConversationStates.ATTENDING,
				"Hi again, lovely. The cloaks still look great. Thanks!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
	private static void rewardPlayer(Player player) {
		Item blackcloak = StendhalRPWorld.get()
				.getRuleManager()
				.getEntityManager().getItem(
						"black_cloak");
		blackcloak.put("bound", player
				.getName());
		player.equip(blackcloak, true);
		player.addKarma(5.0);
		player.addXP(2500);
	}
}
