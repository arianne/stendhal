package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: Imperial princess
 * PARTICIPANTS: The princess and King in Kalavan Castle
 *
 * 
 * STEPS: - Princess asks you to fetch a number of herbs and potions - You bring them - She recommends you to her father - you speak with him
 * 
 * REWARD: - XP - ability to buy houses
 * 
 * REPETITIONS: - None.
 */
public class ImperialPrincess extends AbstractQuest {
	private static final int ARANDULA_DIVISOR = 5;
        private static final int POTION_DIVISOR = 1;
        private static final int ANTIDOTE_DIVISOR = 2;

	private static final String QUEST_SLOT = "imperial_princess";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {

		SpeakerNPC npc = npcs.get("Princess Ylflia");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							engine
									.say("The trapped creatures looked much better last time I dared venture down to the basement, thank you!");
						       
						} else if (!player.hasQuest(QUEST_SLOT)){
							engine
									.say("I cannot free the trapped creatures in the basement but I could do one thing: make them more comfortable. I need #herbs for this.");
						}
						else if (player.getQuest(QUEST_SLOT).equals("recommended")){
							engine
									.say("Speak to my father, the King. I have asked him to grant you citizenship of Kalavan, to express my gratitude to you.");
						}
						else {
							engine
									.say("I'm sure I asked you to do something for me, already.");
						}
					}
				});

		/** If quest is not started yet, start it. */
		npc
				.add(
						ConversationStates.ATTENDING,
						"herbs",
						new SpeakerNPC.ChatCondition() {
							@Override
							public boolean fire(Player player, String text,
									SpeakerNPC npc) {
								return !player.hasQuest(QUEST_SLOT);
							}
						},
						ConversationStates.QUEST_OFFERED,
						null,
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC engine) {
							    engine.say("I need " + Integer.toString(player.getLevel()/ARANDULA_DIVISOR) +" arandula, " + Integer.toString(player.getLevel()/POTION_DIVISOR) + " potions and " + Integer.toString(player.getLevel()/ANTIDOTE_DIVISOR) + " antidotes. Will you get these items?");
							}
						}
						);

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Thank you! We must be subtle about this, I do not want the scientists suspecting I interfere. When you return with the items, please say codeword #herbs.",
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC engine) {
						  // store the current level incase it increases before she see them next.
								player.setQuest(QUEST_SLOT,Integer.toString(player.getLevel()));
							}
						});

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"So you'll just let them suffer! How despicable.",
						null);

	}

	private void step_2() {
		SpeakerNPC npc = npcs.get("Princess Ylflia");

		/** If player has quest and has brought the herbs, get them */
		npc
				.add(
						ConversationStates.ATTENDING,
						Arrays.asList("herb", "herbs"),
					      	null,
						ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.hasQuest(QUEST_SLOT)
							  && !player.getQuest(QUEST_SLOT).equals(
												"recommended")
						    && player.isEquipped("arandula",												Integer.valueOf(player.getQuest(QUEST_SLOT))/ARANDULA_DIVISOR)
						    && player.isEquipped("potion",												Integer.valueOf(player.getQuest(QUEST_SLOT))/POTION_DIVISOR)
						    && player.isEquipped("antidote",												Integer.valueOf(player.getQuest(QUEST_SLOT))/ANTIDOTE_DIVISOR))
						    {
							player.drop("antidote",	Integer.valueOf(player.getQuest(QUEST_SLOT))/ANTIDOTE_DIVISOR);
							player.drop("potion",	Integer.valueOf(player.getQuest(QUEST_SLOT))/POTION_DIVISOR);
							player.drop("arandula",	Integer.valueOf(player.getQuest(QUEST_SLOT))/ARANDULA_DIVISOR);
							engine
							       .say("Perfect! I will recommend you to my father, as a fine, helpful person. He will certainly agree you are eligible for citizenship of Kalavan.");
							player.addXP(Integer.valueOf(player.getQuest(QUEST_SLOT))*300);
							player.setQuest(QUEST_SLOT, "recommended");
							player.notifyWorldAboutChanges();
						}
						else if (player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT).equals("recommended")){
						    engine.say("The herbs you brought did a wonderful job. I told my father you can be trusted, you should go speak with him now.");
						}
						else { /*reminder*/
							engine
									.say("Shh! Don't say it till you have the " + Integer.toString(player.getLevel()/ARANDULA_DIVISOR) +" arandula, " + Integer.toString(player.getLevel()/POTION_DIVISOR) + " potions and " + Integer.toString(player.getLevel()/ANTIDOTE_DIVISOR) + " antidotes. I don't want anyone suspecting our code.");
						}
					}
				});
						
		}

	private void step_3() {
		SpeakerNPC npc = npcs.get("King Cozart");
		/** Complete the quest by speaking to King*/
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
			
				null, ConversationStates.IDLE, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
					    if (player.hasQuest(QUEST_SLOT)
						&& player.getQuest(QUEST_SLOT).equals("recommended"))
						{

						player.addXP(500);
						engine
								.say("Greetings! My wonderful daughter requests that I grant you citizenship of Kalavan City. Consider it done. Now, forgive me while I go back to my meal. Goodbye.");
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					    }
					    else {
						engine
						    .say("Leave me! Can't you see I am trying to eat?");
					    }
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
