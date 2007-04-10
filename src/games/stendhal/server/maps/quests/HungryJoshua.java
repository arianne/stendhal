package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/** 
 * QUEST: Hungry Joshua
 * PARTICIPANTS: 
 * - Xoderos
 * - Joshua
 * 
 * STEPS: 
 * - Talk with Xoderos to activate the quest.
 * - Make 5 sandwiches.
 * - Talk with Joshua to give him the sandwiches.
 * - Return to Xoderos with a message from Joshua.
 *
 * REWARD: 
 * - 200 XP
 * - ability to buy a keyring
 *
 * REPETITIONS:
 * - None.
 */
public class HungryJoshua extends AbstractQuest {
	private static final int FOOD_AMOUNT = 5;
	private static final String QUEST_SLOT = "hungry_joshua";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "joshua", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("sandwich", FOOD_AMOUNT)) || questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (questState.equals("start") && !player.isEquipped("sandwich", FOOD_AMOUNT)) {
			res.add("LOST_ITEM");
		}
		if (questState.equals("joshua")) {
			res.add("BROUGHT_ITEM");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {

		SpeakerNPC npc = npcs.get("Xoderos");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("My brother has enough food now, many thanks.");
						} else {
							engine.say("I'm worried about my brother who lives in Ados. I need someone to take some #food to him.");
						}
					}
				});

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING,
				"food",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				"My brother has enough sandwiches now, thank you.",
				null);

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.ATTENDING,
				"food",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return !player.hasQuest(QUEST_SLOT);
					}
				},
				ConversationStates.QUEST_OFFERED,
				"I think five sandwiches would be enough. My brother is called #Joshua.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you. I'd go myself, but I must work.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, "start");
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.ATTENDING,
				"So you'd just let him starve! I'll have to hope someone else is more charitable.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"Joshua",
				null,
				ConversationStates.QUEST_OFFERED,
				"He's the goldsmith in Ados. They're so short of supplies. Will you help?",
				null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING,
				"food",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start");
					}
				},
				ConversationStates.ATTENDING,
				"#Joshua will be getting hungry! Please hurry!",
				null);

		npc.add(ConversationStates.ATTENDING,
				"Joshua",
				null,
				ConversationStates.ATTENDING,
				"My brother, the goldsmith in Ados.",
				null);
	}

	private void step_2() {
		SpeakerNPC npc = npcs.get("Joshua");

		/** If player has quest and has brought the food, ask for it */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("start") 
										&& player.isEquipped("sandwich", FOOD_AMOUNT);
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Hi, did my brother Xoderos send you with those sandwiches?",
				null
				);
				
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
					if (player.drop("sandwich", FOOD_AMOUNT)) {
							player.setQuest(QUEST_SLOT, "joshua");
							player.addXP(150);
							engine.say("Thank you! Please let Xoderos know that I am fine. He will probably give you something in return.");
							player.notifyWorldAboutChanges();
							} else {
								engine.say("Hey! Where did you put the sandwiches?");
							}
						}
					});
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Oh dear, I'm so hungry, please say #yes they are for me.",
				null
				);
				
	
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Xoderos");

		/** Complete the quest */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("joshua");
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
							player.addXP(50);
							engine.say("I'm glad Joshua is well. Now, what can I do for you? I know, I'll fix that broken key ring that you're carrying ... there, it should work now!");
							// need to make it so that this slot being done means you get a keyring
							player.setFeature("keyring", true);
							player.notifyWorldAboutChanges();
							player.setQuest(QUEST_SLOT, "done");
					}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
