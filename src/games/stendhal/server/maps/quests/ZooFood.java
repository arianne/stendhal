package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Zoo Food
 * 
 * PARTICIPANTS:
 * - Katinka, the keeper at the Ados Wildlife Refuge
 * - Dr. Feelgood, the veterinary
 * 
 * STEPS:
 * - Katinka asks you for ham for the animals
 * - You get the ham, e.g. by killing other animals ;)
 * - You give the ham to Katinka.
 * - Katinka thanks you.
 * - You can then buy cheap medicine from Dr. Feelgood.
 * 
 * REWARD:
 * - 200 XP
 * - everlasting supply for cheap medicine
 * 
 * REPETITIONS:
 * - None.
 */
public class ZooFood extends AbstractQuest {
	
	private static final int REQUIRED_HAM = 10;

	private void step_1() {
		SpeakerNPC npc = npcs.get("Katinka");

		npc.add(ConversationStates.IDLE,
			SpeakerNPC.GREETING_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new SpeakerNPC.ChatAction() {
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (!player.isQuestCompleted("zoo_food")) {
					engine.say("Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help. Maybe you can do a #task for us.");
				} else {
					engine.say("Welcome back to the Ados Wildlife Refuge! Thanks again for rescuing our animals!");
				}
			}
		});
		
		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("zoo_food")) {
							engine.say("Our tigers, lions and bears are hungry. We need " + REQUIRED_HAM + " hams to feed them. Can you help us?");
						} else {
							engine.say("Thank you, but I think we are out of trouble now.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Okay, but please don't let poor animals suffer! Bring me the hams when you have got them.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("zoo_food", "start");
					}
				});
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Too bad. Then we will have to feed them with our poor little deer.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("zoo_food", "rejected");
					}
				});
	}

	private void step_2() {
		// Just find the ham somewhere. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Katinka");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("zoo_food")	
								&& player.getQuest("zoo_food").equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Welcome back! Have you brought the " + REQUIRED_HAM + " hams?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.drop("ham", REQUIRED_HAM)) {
							world.modify(player);
							player.setQuest("zoo_food", "done");
							player.addXP(200);
							engine.say("Thank you! You have rescued our rare animals.");
						} else {
							engine.say("Don't try to trick me! I said that we need " + REQUIRED_HAM + " hams!");
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"no",
				null,
				ConversationStates.ATTENDING,
				"What a pity. I hope you will help us anyway.",
				null);
	}

	private void step_4() {
		SpeakerNPC npc = npcs.get("Dr. Feelgood");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.isQuestCompleted("zoo_food")) {
							engine.say("Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?");
						} else {
							engine.say("Excuse me! The animals are all sick because they don't have enough food. I don't have time for you now. Bye.");
							engine.setActualState(ConversationStates.IDLE);
						}
					}
				});
	}

	@Override
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		super.addToWorld(world, rules);
		step_1();
		step_2();
		step_3();
		step_4();
	}
}
