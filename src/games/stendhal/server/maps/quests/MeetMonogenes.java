package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Speak with Monogenes
 * PARTICIPANTS:
 * - Monogenes
 *
 * STEPS:
 * - Talk to Monogenes to activate the quest and keep speaking with Monogenes.
 * - Be polite and say "bye" at the end of the conversation to get a small
 *   reward.
 *
 * REWARD:
 * - 10 XP (check that user's level is lesser than 5)
 * - No money
 * 
 * REPETITIONS:
 * - None
 * 
 * TODO: make it possible to repeat the quest if the player said "no"
 */
public class MeetMonogenes extends AbstractQuest {

	@Override
	public void addToWorld() {
		super.addToWorld();
		SpeakerNPC npc = npcs.get("Monogenes");
		
		npc.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
						SpeakerNPC engine) {
						// A little trick to make NPC remember if it has met the
						// player before and react accordingly.
						// NPC_name quest doesn't exist anywhere else neither is
						// used for any other purpose.
						if (!player.isQuestCompleted("Monogenes")) {
							engine.say("Hi foreigner, don't be surprised if people here are reserved: the fear of the advances of Blordrough's dark legion has affected everybody, including me. Do you want to know how to socialize with Semos' people?");
							player.setQuest("Monogenes", "done");
							engine.setCurrentState(ConversationStates.QUESTION_1);
						} else {
							engine.say("Hi again, " + player.getName()
									+ ". How can I #help you this time?");
						}
					}
				});

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.HELP_MESSAGES,
				null,
				ConversationStates.INFORMATION_1,
				"I'm Diogenes' older brother and I don't remember what I did before I retired. Anyway, I can help you by telling you how to treat Semos' people...  Do you want to know how to socialize with them?",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_2,
				"Only ask them about what they bring the conversation around to: the WORDS that are bolded in blue color. Otherwise, you can get a harsh answer although you usually can ask about their job , help, offer, or quest. Do you want to know where the city's main buildings are?",
				null);
		
		npc.add(ConversationStates.INFORMATION_1,
				"no",
				null,
				ConversationStates.IDLE,
				"And how are you supposed to know what's happening? By reading the Semos Tribune? Bye!",
				null);
		
		npc.add(ConversationStates.INFORMATION_2,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Sometimes it is helpful to read the city's wooden signs by right-clicking on them and choosing LOOK. I can direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith, the #bakery, or the #village.",
				null);
		
		npc.add(ConversationStates.INFORMATION_2,
				"no",
				null,
				ConversationStates.IDLE,
				"Oh I see... You are of that kind of person that doesn't like asking for directions, huh? Well, good luck finding the secretly hidden mmhmmhmm!",
				null);
		
		npc.addReply("bank", "The bank is precisely this building next to me. I thought the big chest on the front would have given you a clue.");
		npc.addReply("library", "The library is west from here, following the path. There's an OPEN BOOK AND A FEATHER sign over one of the two doors.");
		npc.addReply("tavern", "The tavern is southeast from here, following the path. You'll see a big INN sign over the door. You can't miss it.");
		npc.addReply("temple", "The temple is the second building southeast from here, following the path. There's a small CROSS over the roof.");
		npc.addReply("bakery", "The local bakery is east of here. It has a small BREAD sign over its door.");
		npc.addReply("blacksmith", "The blacksmith's shop is southwest from here, following the path. There's a small SWORD sign over the door.");
		npc.addReply("village", "The village is southwest from here, following the path. There you can buy sheep to breed.");
		
		/** Give the reward to the polite newcomer user */
//		npc.add(ConversationStates.ATTENDING,
//				SpeakerNPC.GOODBYE_MESSAGES,
//				null,
//				ConversationStates.IDLE,
//				null,
//				new SpeakerNPC.ChatAction() {
//					@Override
//					public void fire(Player player, String text, SpeakerNPC engine) {
//						if (player.getLevel() < 15) {
//							engine.say("Bye, my friend. I hope my indications have been helpful...");
//							player.addXP(10);
//							player.notifyWorldAboutChanges();
//						} else {
//							engine.say("It's curious... Now that I think about it, I would have betted I had seen you in Semos before...");
//						}
//					}
//				});
		npc.addGoodbye();
	}
}
