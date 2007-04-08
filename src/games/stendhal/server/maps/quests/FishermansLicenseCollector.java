package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**QUEST: Fisherman's license Collector
 * PARTICIPANTS:
 * - Santiago the fisherman
 *  
 *
 * STEPS:
 * - The player must bring all kinds of fishes to the fisherman 
 *
 * REWARD:
 * - 500 XP
 * - The player gets a fisherman's license.  
 *
 * REPETITIONS:
 * - No repetitions.
 **/
public class FishermansLicenseCollector extends AbstractQuest {

	static final String QUEST_SLOT = "fishermans_license2";

	private static final List<String> neededFish = Arrays.asList(
		"trout", // fairly rare from glow_monster in haunted house
		"perch",      // rare from monk on mountain
		"mackerel" // rare from devil_queen on mountain
	);
	
	/**
	 * Returns a list of the names of all fish that the given player
	 * still has to bring to fulfil the quest.
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of fish names
	 */
	private List<String> missingFish(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();
		
		String doneText = player.getQuest(QUEST_SLOT);
		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		for (String fish: neededFish) {
			if (! done.contains(fish)) {
				if (hash) {
					fish = "#" + fish;
				}
				result.add(fish);
			}
		}
		return result;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Santiago");
		
		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.isQuestCompleted(FishermansLicenseQuiz.QUEST_SLOT)
								&& !player.hasQuest(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"Hello again! The second part of your #exam is waiting for you!",
				null);

		// player is willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("You have to bring me one fish of each #species so that I can see what you have learned so far.");
						player.setQuest(QUEST_SLOT, "");
					}
				});
		
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"It's okay, then you can excercise some more.",
				null
				);

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING,
				"species",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT) &&
								!player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.QUESTION_2,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						List<String> needed = missingFish(player, true);
						engine.say("There " + Grammar.isare(needed.size()) + " " + Grammar.quantityplnoun(needed.size(), "fish") + " still missing: "
								+ Grammar.enumerateCollection(needed) + ". Do you have such fish with you?");
					}
				});

		// player says he doesn't have required fish with him
		npc.add(ConversationStates.QUESTION_2,
				"no",
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					List<String> missing = missingFish(player, false);
					engine.say("Let me know as soon as you find " + Grammar.itthem(missing.size()) + ". Goodbye.");
				}});

		// player says he has a required fish with him
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.QUESTION_2,
				"What did you find?",
				null);
		
		for (String fish: neededFish) {
			npc.add(ConversationStates.QUESTION_2,
					fish,
					null,
					ConversationStates.QUESTION_2,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text, SpeakerNPC engine) {
							List<String> missing = missingFish(player, false);
							if (missing.contains(text)) {
								if (player.drop(text)) {
									// register fish as done
									String doneText = player.getQuest(QUEST_SLOT);
									player.setQuest(QUEST_SLOT, doneText + ";" + text);
									// check if the player has brought all fish
									missing = missingFish(player, true);
									if (missing.size() > 0) {
										engine.say("Thank you very much! Do you have another fish for me?");
									} else {
										player.addXP(500);
										engine.say("You did a great job! Now you are a real fisherman and your chance to catch fish will increase!");
										player.setQuest(QUEST_SLOT, "done");
										player.notifyWorldAboutChanges();
									}
								} else {
									engine.say("Don't try to cheat! I know that you don't have " + Grammar.a_noun(text) + ". What do you really have for me?");
								}
							} else {
								engine.say("I already have that one. Do you have other fish for me?");
							}
						}
					});
		}
	}

	private void step_2() {
		// Just find some of the fish somewhere and bring them to Santiago.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Santiago");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"Welcome back. I hope you were not lazy and bring me some other fish #species.",
				null);
		
		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"Welcome fisherman! Nice to see you again. I wish you luck for fishing.",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
}
