package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.rule.EntityManager;

/**
 * QUEST: Campfire
 * 
 * PARTICIPANTS:
 * - Sally, a scout sitting next to a campfire near Or'rill
 * 
 * STEPS:
 * - Sally asks you for wood for her campfire
 * - You collect 10 pieces of wood in the forest
 * - You give the wood to Sally.
 * - Katinka gives you 10 meat or ham in return.
 * 
 * REWARD:
 * - 10 meat or ham
 * - 50 XP
 * 
 * REPETITIONS:
 * - Unlimited, but 1000 turns (ca. 5 minutes) of waiting are required
 *   between repetitions
 */
public class Campfire implements IQuest {
	private StendhalRPWorld world;

	private StendhalRPRuleProcessor rules;
	
	private NPCList npcs;
	
	private static final int REQUIRED_WOOD = 10;

	private boolean canStartQuestNow(SpeakerNPC npc, Player player) {
		if (!player.hasQuest("campfire")) {
			return true;
		} else if (player.getQuest("campfire").equals("start")) {
			return false;
		} else {
			int turnWhenLastBroughtWood = Integer.parseInt(player.getQuest("campfire"));
			int turnsSinceLastBroughtWood = rules.getTurn() - turnWhenLastBroughtWood;
			if (turnsSinceLastBroughtWood < 0) {
				// The server was restarted since last doing the quest.
				// Make sure the player can repeat the quest.
				turnsSinceLastBroughtWood = 0;
				player.setQuest("campfire", "0");
			}
			return turnsSinceLastBroughtWood >= 1000;
		}
	}
	
	private void step_1() {
		SpeakerNPC npc = npcs.get("Sally");

		npc.add(ConversationStates.IDLE,
			SpeakerNPC.GREETING_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new SpeakerNPC.ChatAction() {
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (canStartQuestNow(engine, player)) {
					engine.say("Hi! I hope you can do me a #favor.");
				} else if (player.getQuest("campfire").equals("start")) { 
					if (player.isEquipped("wood", REQUIRED_WOOD)){
						engine.say("Hi again! Are these ten pieces of wood for me?");
						engine.setActualState(ConversationStates.QUEST_ITEM_BROUGHT);
					} else {
						engine.say("You're back already? Don't forget that you promised to collect ten pieces of wood in the forest for me!");
					}
				} else {
					engine.say("Hi again! Have you come to bring me wood again? I still have enough from last time, come back later!");
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
						if (canStartQuestNow(engine, player)) {
							engine.say("I need more wood to keep my campfire running. But I can't leave my fire unattended. Could you please collect wood for me?");
						} else if (player.getQuest("campfire").equals("start")){
							engine.say("You already promised me to bring me ten pieces of wood!");
						}
						else {
							engine.say("I don't need any more wood at the moment. Come back later.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Okay. You can find wood in the forest North of here. Come back when you got ten pieces of wood.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("campfire", "start");
					}
				});
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"What a pity! What will I do with all my meat? Maybe I'll just feed the animals.",
				null);
	}

	private void step_2() {
		// Just find the wood somewhere. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Sally");

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"yes",
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.drop("wood", REQUIRED_WOOD)) {
							player.setQuest("campfire", Integer.toString(rules.getTurn()));
							player.addXP(50);
							
							String rewardClass;
							if (Rand.throwCoin() == 1) {
								rewardClass = "meat";
							} else {
								rewardClass = "ham";
							}
							engine.say("Thank you! Here, take some " + rewardClass + ".");
							EntityManager manager = world.getRuleManager().getEntityManager();
							StackableItem reward = (StackableItem) manager.getItem(rewardClass);
							reward.setQuantity(REQUIRED_WOOD);
							player.equip(reward, true);
							world.modify(player);
						} else {
							engine.say("Hey! Where did you put the wood?");
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Too bad. Hopefully you will bring me some before my fire goes out.",
				null);
	}

	public Campfire(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = world;
		this.rules = rules;

		step_1();
		step_2();
		step_3();
	}
}