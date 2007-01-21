package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: CleanStorageSpace
 *
 * PARTICIPANTS:
 * - Eonna
 * 
 * STEPS:
 * - Eonna asks you to clean her storage_space.
 * - You go kill at least a rat, a cave rat and a cobra.
 * - Eoanna checks your kills and then thanks you.
 * 
 * REWARD:
 * - 25 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class CleanStorageSpace extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Eonna");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("clean_storage")) {
							engine.say("My #basement is absolutely crawling with rats. Will you help me?");
						} else {
							engine.say("Thanks again! I think it's still clear down there.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh, thank you! I'll wait up here, and if any try to escape I'll hit them with the broom!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("clean_storage", "start");
						player.removeKill("rat");
						player.removeKill("cobra");
						player.removeKill("caverat");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"*sigh* Oh well, maybe someone else will be my hero...",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("clean_storage", "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				Arrays.asList("basement", "storage_space"),
				null,
				ConversationStates.QUEST_OFFERED,
				"Yes, it's just down the stairs, over there. A whole bunch of nasty-looking rats; I think I saw a snake as well! You should be careful... still want to help me?",
				null);
	}

	private void step_2() {
		// Go kill at least a rat, a cave rat and a cobra.
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Eonna");

		// the player returns to Eonna after having started the quest.
		// Eonna checks if the player has killed one of each animal race.
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC engine) {
						return player.hasQuest("clean_storage")
								&& player.getQuest("clean_storage").equals("start");
					}
				},
				ConversationStates.QUEST_STARTED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.hasKilled("rat") && player.hasKilled("caverat")
								&& player.hasKilled("cobra")) {
							engine.say("A hero at last! Thank you!");
							player.addXP(25);
							player.setQuest("clean_storage", "done");
							engine.setCurrentState(ConversationStates.ATTENDING);
						} else {
							engine.say("Don't you remember promising to clean out the rats from my #basement?");
						}
					}
				});
		
		npc.add(ConversationStates.QUEST_STARTED,
				"basement",
				null,
				ConversationStates.ATTENDING,
				"Down the stairs, like I said. Please get rid of all those rats, and see if you can find the snake as well!",
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
