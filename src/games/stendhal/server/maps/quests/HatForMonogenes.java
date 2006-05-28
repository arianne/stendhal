package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.maps.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Hat For Monogenes
 * PARTICIPANTS:
 * - Monogenes, an old man in Semos city. 
 *
 * STEPS:
 * - Monogenes asks you to buy a hat for him.
 * - Xin Blanca sells you a leather_helmet.
 * - Monogenes sees your leather_helmet and asks for it and then thanks you.
 *
 * REWARD:
 * - 10 XP
 *
 * REPETITIONS:
 * - None.
 */
public class HatForMonogenes implements IQuest {
	private StendhalRPWorld world;

	private NPCList npcs;

	private void step_1() {
		SpeakerNPC npc = npcs.get("Monogenes");

		npc.add(ConversationStates.ATTENDING,
				Behaviours.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("hat_monogenes")) {
							engine.say("Could you bring me a #hat to cover my baldness? Brrrrr! Semos day's are getting colder...");
						} else {
							engine.say("Thanks good friend, but this hat will last five winters at least and I don't really need more than one. If I can help you somehow just say it.");
							engine.setActualState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"yes",
				null,
				ConversationStates.ATTENDING,
				"Thanks, my good friend. I'll be waiting for your return. Now if I can help you in anything just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("hat_monogenes", "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Yes, forget it bud. You surely have more importants things to do and little time. I'll just stay here with my cool not-as-in-slick head. Boohooooo! Sniff... now if I can help you... sniff in anything sniff... just ask.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest("hat_monogenes", "rejected");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"hat",
				null,
				ConversationStates.QUEST_OFFERED,
				"You don't know what a hat is? Anything light like leather that can cover my head. So, will you do it?",
				null);
	}

	private void step_2() {
		//Just buy the leather_helmet from Xin Blanca. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Monogenes");

		npc.add(ConversationStates.IDLE,
				Behaviours.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest("hat_monogenes")
								&& player.getQuest("hat_monogenes").equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						if (player.isEquipped("leather_helmet")) {
							engine.say("Hey! Is that hat for me?");
						} else {
							engine.say("Hey, my good friend, remember that leather hat I asked you before? I like having fresh ideas but not in this manner... Anyway, what can I do for you?");
							engine.setActualState(1);
						}
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"yes",
				// make sure the player isn't cheating by putting the helmet
				// away and then saying "yes"
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.isEquipped("leather_helmet");
					}
				}, 
				ConversationStates.ATTENDING,
				"Bless you, my good friend! Now I can laugh at the soon coming snowflakes wuahahaha! Ahem... If there's anything I can do for you now just say it.",
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.drop("leather_helmet");

						player.addXP(10);
						world.modify(player);
						player.setQuest("hat_monogenes", "done");
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh! Ok... I suppose there's someone more fortunate than me that will get his head warm today...Boohoooo! Sniff... How can I help you then?",
				null);
	}

	public HatForMonogenes(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = w;

		step_1();
		step_2();
		step_3();
	}
}