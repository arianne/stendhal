package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Cloaks for Bario
 * 
 * PARTICIPANTS:
 * - Bario, a guy living in an underground house deep under the Ados
 *   Wildlife Refuge
 * 
 * STEPS:
 * - Bario asks you for a number of blue elf cloaks.
 * - You get some of the cloaks somehow, e.g. by killing elves.
 * - You bring the cloaks to Bario and give them to him.
 * - Repeat until Bario received enough cloaks. (Of course you can
 *   bring up all required cloaks at the same time.)
 * - Bario gives you a golden shield in exchange.
 * 
 * REWARD:
 * - golden shield
 * - 1500 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class CloaksForBario implements IQuest {
	
	private StendhalRPWorld world;

	private NPCList npcs;

	private static final int REQUIRED_CLOAKS = 10;
	
	private void step_1() {
		SpeakerNPC npc = npcs.get("Bario");
		
		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest("cloaks_for_bario");
					}
				},
				ConversationStates.ATTENDING,
				"Hey! How did you get down here? Well. I'm Bario. Can you do a #task for me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("cloaks_for_bario")) {
							engine.say("I don't dare to go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here. Can you help me?");
						} else {
							engine.say("I don't have anything to do for you.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("I need some blue elf cloaks to survive the winter. If you bring me ten of them, I will give you a reward.");
						player.setQuest("cloaks_for_bario", Integer.toString(REQUIRED_CLOAKS));
					}
				});
		
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Too bad. Looks like I have to burn all my wood next winter.",
				null
				);
	}

	private void step_2() {
		// Just find some of the cloaks somewhere and bring them to Bario.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Bario");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("cloaks_for_bario")
								&& ! player.isQuestCompleted("cloaks_for_bario");
					}
				},
				ConversationStates.QUESTION_1,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("Hi again. I still need "
								+ player.getQuest("cloaks_for_bario")
								+ " blue elf cloaks. Do you have one for me?");
					}
				});
		
		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isQuestCompleted("cloaks_for_bario");
					}
				},
				ConversationStates.ATTENDING,
				"Welcome! Thanks again for the cloaks.",
				null);

	// player says he doesn't have any blue elf cloaks with him
	npc.add(ConversationStates.QUESTION_1,
			"no",
			null,
			ConversationStates.ATTENDING,
			"Too bad.",
			null);

	// player says he has a blue elf cloak with him
	npc.add(ConversationStates.QUESTION_1,
			"yes",
			null,
			ConversationStates.ATTENDING,
			null,
			new SpeakerNPC.ChatAction() {
				public void fire(Player player, String text, SpeakerNPC engine) {
					if (player.drop("elf_cloak_+2")) {
						// find out how many cloaks the player still has to bring
						int toBring = Integer.parseInt(player.getQuest("cloaks_for_bario")) - 1;
						if (toBring > 0) {
							player.setQuest("cloaks_for_bario", Integer.toString(toBring));
							engine.say("Thank you very much! Do you have another one? I still need " + toBring + " cloaks.");
							engine.setActualState(ConversationStates.QUESTION_1);
						} else {
							Item goldenShield = world.getRuleManager().getEntityManager().getItem("golden_shield");            
							player.equip(goldenShield);
							player.addXP(1500);
							world.modify(player);
							player.setQuest("cloaks_for_bario", "done");
							engine.say("Thank you very much! Now I have enough cloaks to survive the winter. Here, take this golden shield as a reward.");
						}
					} else {
						engine.say("Don't try to trick me! You don't have a blue elf cloak!");
					}
				}
			});
	}

	public CloaksForBario(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = w;

		step_1();
		step_2();
		step_3();
	}
}