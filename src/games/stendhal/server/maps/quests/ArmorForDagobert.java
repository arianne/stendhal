package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.maps.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Armor for Dagobert
 * 
 * PARTICIPANTS:
 * - Dagobert, the consultant at the bank of Semos
 * 
 * STEPS:
 * - Dagobert asks you to find a leather_cuirass.
 * - You get a leather_cuirass, e.g. by killing a cyclops.
 * - Dagobert sees your leather_cuirass and asks for it and then thanks you.
 * 
 * REWARD:
 * - 50 XP
 * - 80 gold
 * 
 * REPETITIONS:
 * - None.
 */
public class ArmorForDagobert implements IQuest {
	private StendhalRPWorld world;

	private NPCList npcs;

	private void step_1() {
		SpeakerNPC npc = npcs.get("Dagobert");

		npc.add(ConversationStates.ATTENDING,
				Behaviours.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("armor_dagobert")) {
							engine.say("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?");
						} else {
							engine.say("Thank you very much for the armor, but I don't have any other task for you.");
							engine.setActualState(ConversationStates.ATTENDING);
						}
					}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Once I had a nice #leather_cuirass, but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("armor_dagobert", "start");
					}
				});
		
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Well, then I guess I'll just duck and cover.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("armor_dagobert", "rejected");
					}
				});
		
		// player wants to know what a leather_cuirass is
		npc.add(ConversationStates.QUEST_OFFERED,
				"leather_cuirass",
				null,
				ConversationStates.QUEST_OFFERED,
				"A leather_cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.",
				null);
	}

	private void step_2() {
		// Just find a leather_cuirass somewhere. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Dagobert");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE,
				Behaviours.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("armor_dagobert")
								&& player.getQuest("armor_dagobert").equals("start");
					}
				}, 
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.isEquipped("leather_cuirass")) {
							engine.say("Excuse me, please! I have noticed the leather_cuirass you're carrying. Is it for me?");
						} else {
							engine.say("Luckily I haven't been robbed while you were away. I would be glad to receive a leather_cuirass. Anyway, how can I #help you?");
							// engine.setActualState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"yes",
				// make sure the player isn't cheating by putting the armor
				// away and then saying "yes"
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isEquipped("leather_cuirass");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("leather_cuirass");
						
						StackableItem money = (StackableItem) world.getRuleManager().getEntityManager().getItem("money");            
						money.setQuantity(80);
						player.equip(money);
						player.addXP(50);
						
						world.modify(player);
						player.setQuest("armor_dagobert", "done");
						engine.say("Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere.");
					}
				});

		npc.add(
				ConversationStates.QUEST_ITEM_BROUGHT,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Well then, I hope you find another one which you can give to me before I get robbed again.",
				null);
	}

	public ArmorForDagobert(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = w;

		step_1();
		step_2();
		step_3();
	}
}