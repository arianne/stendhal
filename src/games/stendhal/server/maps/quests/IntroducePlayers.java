package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** 
 * QUEST: Introduce new players to game
 * PARTICIPANTS: 
 * - Tad
 * - Margaret
 * - Ilisa
 * 
 * STEPS: 
 * - Tad asks you to buy a flask to give it to Margaret.
 * - Margaret sells you a flask
 * - Tad thanks you and asks you to take the flask to Ilisa
 * - Ilisa asks you for a few herbs.
 * - Return the created dress potion to Tad.
 *
 * REWARD: 
 * - 170 XP
 * - 10 gold coins 
 *
 * REPETITIONS:
 * - None.
 */
public class IntroducePlayers extends AbstractQuest {
	private static final String QUEST_SLOT = "introduce_players";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (player.hasQuest("TadFirstChat")) {
			res.add("FIRST_CHAT");
		}
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		String questState = player.getQuest(QUEST_SLOT);
		if (player.isQuestInState(QUEST_SLOT, "start", "ilisa", "corpse&herbs", "potion", "done")) {
			res.add("GET_FLASK");
		}
		if ((questState.equals("start") && player.isEquipped("flask")) || player.isQuestInState(QUEST_SLOT, "ilisa", "corpse&herbs", "potion", "done")) {
			res.add("GOT_FLASK");
		}
		if (player.isQuestInState(QUEST_SLOT, "ilisa", "corpse&herbs", "potion", "done")) {
			res.add("FLASK_TO_ILISA");
		}
		if (player.isQuestInState(QUEST_SLOT, "corpse&herbs", "potion", "done")) {
			res.add("GET_HERB");
		}
		if ((questState.equals("corpse&herbs") && player.isEquipped("arandula")) || player.isQuestInState(QUEST_SLOT, "potion", "done")) {
			res.add("GET_HERB");
		}
		if (player.isQuestInState(QUEST_SLOT, "potion", "done")) {
			res.add("TALK_TO_TAD");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Tad");
		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				new StandardInteraction.QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("I'm alright now, thanks.");
					}
				});

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.QUEST_MESSAGES,
				new StandardInteraction.QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?");
					}
				});

		/** In case Quest has already been completed */
		npc.add(ConversationStates.ATTENDING,
				"flask",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.isQuestCompleted("introduce_players");
					}
				},
				ConversationStates.ATTENDING,
				"You've already helped me out! I'm feeling much better now.",
				null);
		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.QUEST_OFFERED,
				"flask",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return !player.hasQuest("introduce_players");
					}
				},
				ConversationStates.QUEST_OFFERED,
				"You could probably get a flask from #Margaret.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						engine.say("Great! Please go as quickly as you can. *sneeze*");
						player.setQuest("introduce_players", "start");
					}
				});
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"Oh, please won't you change your mind? *sneeze*",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"margaret",
				null,
				ConversationStates.QUEST_OFFERED,
				"Margaret is the maid in the inn just down the street. So, will you help?",
				null);
		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING,
				"flask",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest("introduce_players")
								&& player.getQuest("introduce_players").equals("start")
								&& !player.isEquipped("flask");
					}
				},
				ConversationStates.ATTENDING,
				"*cough* Oh dear... I really need this medicine! Please hurry back with the #flask from #Margaret.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"margaret",
				null,
				ConversationStates.ATTENDING,
				"Margaret is the maid in the inn just down the street.",
				null);
	}
	private void step_2() {
		/** Just buy the stuff from Margaret. It isn't a quest */
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Tad");
    // staring the conversation the first time after getting a flask.
    npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest("introduce_players")
								&& player.getQuest("introduce_players").equals(
										"start") && player.isEquipped("flask");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						// note Ilisa is spelled with a small i here because I and l cannot be told apart in game
						engine.say("Ok, you got the flask! Now, I need you to take it to #ilisa... she'll know what to do next.");
						StackableItem money = (StackableItem) StendhalRPWorld.get()
								.getRuleManager().getEntityManager().getItem(
										"money");
						money.setQuantity(10);
						player.equip(money);
						player.addXP(10);
						player.notifyWorldAboutChanges();
						player.setQuest("introduce_players", "ilisa");
					}
				});
    // remind the player to take the flask to ilisa.    npc.add(ConversationStates.IDLE,        SpeakerNPC.GREETING_MESSAGES,        new SpeakerNPC.ChatCondition() {            @Override
			public boolean fire(Player player, SpeakerNPC npc) {                return player.hasQuest("introduce_players")                        && player.getQuest("introduce_players").equals(                                "ilisa") && player.isEquipped("flask");            }        },        ConversationStates.ATTENDING,        null,        new SpeakerNPC.ChatAction() {            @Override
			public void fire(Player player, String text,                    SpeakerNPC engine) {				// note Ilisa is spelled with a small i here because I and l cannot be told apart in game
                engine.say("Ok, you got the flask! Now, I need you to take it to #ilisa... she'll know what to do next.");            }        });
		npc.add(ConversationStates.ATTENDING,				"ilisa",				null,				ConversationStates.ATTENDING,				"Ilisa is the summon healer at Semos temple.",				null);	}
	private void step_4() {
		SpeakerNPC npc = npcs.get("Ilisa");
		npc.add(ConversationStates.IDLE,				SpeakerNPC.GREETING_MESSAGES,				new SpeakerNPC.ChatCondition() {					@Override
					public boolean fire(Player player, SpeakerNPC npc) {						return player.hasQuest("introduce_players")								&& player.getQuest("introduce_players").equals(										"ilisa");					}				},				ConversationStates.ATTENDING,				null,				new SpeakerNPC.ChatAction() {					@Override
					public void fire(Player player, String text,							SpeakerNPC engine) {						if (player.drop("flask")) {							engine.say("Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a few #herbs. Can you help?");							player.addXP(10);
							player.notifyWorldAboutChanges();
							player.setQuest("introduce_players", "corpse&herbs");						} else {							engine.say("Medicine for #Tad? Didn't he tell you to bring a flask?");						}					}				});

        npc.add(ConversationStates.ATTENDING,
				Arrays.asList("herbs", "arandula"),
				null,
				ConversationStates.ATTENDING,
				"Poor Tad is always feeling sick... He'll give a good reward to you for helping get his medicine, though.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				"tad",
				null,
				ConversationStates.ATTENDING,
				"He needs a very powerful potion to heal himself. He offers a good reward to anyone who will help him.",
				null);
	}
	private void step_5() {		SpeakerNPC npc = npcs.get("Ilisa");
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest("introduce_players")
								&& player.getQuest("introduce_players").equals(
										"corpse&herbs");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.drop("arandula")) {
							engine.say("Okay! Thank you. Now I will just mix these... a pinch of this... and a few drops... there! Can you ask #Tad to stop by and collect it? I want to see how he's doing.");
							player.addXP(50);

							player.notifyWorldAboutChanges();
							player.setQuest("introduce_players", "potion");
						} else {
							engine.say("Can you fetch those #herbs for the medicine?");
						}
					}
				});

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("potion", "medicine"),
				null,
				ConversationStates.ATTENDING,
				"The medicine that #Tad is waiting for.",
				null);
	}
	private void step_6() {
		SpeakerNPC npc = npcs.get("Tad");

		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC npc) {
						return player.hasQuest("introduce_players")
								&& player.getQuest("introduce_players").equals(
										"potion");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						// note Ilisa is spelled with a small i here because I and l cannot be told apart in game
						engine.say("Thanks! I will go talk with #ilisa as soon as possible.");
						player.addXP(100);
						player.notifyWorldAboutChanges();
						player.setQuest("introduce_players", "done");
					}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
		step_4();
		step_5();
		step_6();
	}
}