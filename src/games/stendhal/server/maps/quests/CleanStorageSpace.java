package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: CleanStorageSpace PARTICIPANTS:
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
public class CleanStorageSpace extends AQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Eonna");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("clean_storage")) {
							engine.say("My #storage_space it is crawling with rats. Will you help me?");
						} else {
							engine.say("Thanks again, I don't think it needs to be cleaned again yet. If I can help you somehow just say it.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thank you! I'll be waiting for your return. Now if I can help you in anything just ask.",
				new SpeakerNPC.ChatAction() {
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
				"Maybe you are not the hero I thought you would be. *sighs* Now if I can help you in anything *sighs* just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("clean_storage", "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"storage_space",
				null,
				ConversationStates.QUEST_OFFERED,
				"yes it down the stairs, there some rats and I think I saw a snake too so be careful. So, will you do it?",
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
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("clean_storage")
								&& player.getQuest("clean_storage").equals("start");
					}
				},
				ConversationStates.QUEST_STARTED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.hasKilled("rat") && player.hasKilled("caverat")
								&& player.hasKilled("cobra")) {
							engine.say("Oh wow! A fine hero at last! Thank you! Now can I help you with anything?");
							player.addXP(25);
							player.setQuest("clean_storage", "done");
							engine.setActualState(ConversationStates.ATTENDING);
						} else {
							engine.say("Don't you remember... you promised to clean my #storage_space.");
						}
					}
				});
		
		npc.add(ConversationStates.QUEST_STARTED,
				"storage_space",
				null,
				ConversationStates.ATTENDING,
				"Did you forget? It's down the stairs, there some rats and I think I saw a snake too so be careful. Please hurry.",
				null);
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);

		step_1();
		step_2();
		step_3();
	}
}