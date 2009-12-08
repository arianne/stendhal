package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Speak with Monogenes PARTICIPANTS: - Monogenes
 * 
 * STEPS: - Talk to Monogenes to activate the quest and keep speaking with
 * Monogenes. - Be polite and say "bye" at the end of the conversation to get a
 * small reward.
 * 
 * REWARD: broken (- 10 XP (check that user's level is lesser than 2) - No money)
 * 
 * REPETITIONS: - None
 * 
 * TODO: make it possible to repeat the quest if the player said "no"
 */
public class MeetMonogenes extends AbstractQuest {
	@Override
	public String getSlotName() {
		return "meetMonogenes";
	}
	@Override
	public void addToWorld() {
		super.addToWorld();
		final SpeakerNPC npc = npcs.get("Monogenes");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				null, ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						// A little trick to make NPC remember if it has met the
						// player before and react accordingly.
						// NPC_name quest doesn't exist anywhere else neither is
						// used for any other purpose.
						if (player.isQuestCompleted("Monogenes")) {
							engine.say("Hi again, " + player.getTitle()
									+ ". How can I #help you this time?");
						} else {
							engine
									.say("Hello there, stranger! Don't be too intimidated if people are quiet and reserved... the fear of Blordrough and his forces has spread all over the country, and we're all a bit concerned. I can offer a few tips on socializing though, would you like to hear them?");
							player.setQuest("Monogenes", "done");
							engine
									.setCurrentState(ConversationStates.INFORMATION_1);
						}
					}
				});

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.HELP_MESSAGES,
			null,
			ConversationStates.INFORMATION_1,
			"I can offer you a few tips on socializing with the residents of Semos, if you like?",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"You should introduce yourself by saying \"hi\". After this, try to keep the conversation to the topics they bring up; suitable subjects will be highlighted #'like this'. A few generally safe topics of conversation are the person's #job, asking for #help, asking if they have an #offer to make, and asking for a #quest to go on. Now, if you want a quick run-down of the #buildings in Semos, just say.",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"And how are you supposed to know what's happening? By reading the Semos Tribune? Hah! Bye, then.",
			null);

		npc.addReply(
			"buildings",
			"I can show you a #map or direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith, the #bakery, or the old #village.");

		npc.add(
			ConversationStates.ATTENDING,
			"map", null, ConversationStates.ATTENDING, "Caption\n"
			+ "1 Townhall, Tad lives here,   2 Library,   3 Bank,   4 Bakery,\n"
			+ "5 Storage,   6 Blacksmith, Carmen,   7 Inn, Margaret \n"
        	+ "8 Temple, ilisa,   9 Dangerous Dungeon \n"
        	+ "A Semos Village,   B Northern Plains and Mine, \n"
        	+ "C Very long path to Ados, \n"
        	+ "D Southern Plains and Nalwor Forest",
        	new ExamineChatAction("map-semos-city.png", "Semos City", "Map of Semos City"));

		npc.addReply(
			"bank",
			"See this big building in front of me, with the giant fake treasure chest? That's it right there. Kinda obvious once you think about it.");

		npc.addReply(
			"library",
			"Follow the path from here to the west; you'll see a building with two doors, and the emblem of a book and quill pen on display.");

		npc.addReply(
			"tavern",
			"Just head southeast along the path, and you can't miss it. It has a large sign that reads INN.");

		npc.addReply(
			"temple",
			"The temple is southeast from here, beside the #tavern. It has a cross on the roof, very distinctive.");

		npc.addReply(
			"bakery",
			"Our local bakery is just east of this square; they have a sign up with a picture of a loaf of bread on it.");

		npc.addReply(
			"blacksmith",
			"Head southwest to reach the smithy. There's a picture of an anvil hanging above the door, you should be able to spot it.");

		npc.addReply(
			"village",
			"Just keep heading southwest, past the #blacksmith, and you will shortly come to the old Semos village. Nishiya still sells sheep there.");

		/** Give the reward to the polite newcomer user */
		// npc.add(ConversationStates.ATTENDING,
		// SpeakerNPC.GOODBYE_MESSAGES,
		// null,
		// ConversationStates.IDLE,
		// null,
		// new SpeakerNPC.ChatAction() {
		// @Override
		// public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		// if (player.getLevel() < 2) {
		// engine.say("Goodbye! I hope I was of some use to you.");
		// player.addXP(10);
		// player.notifyWorldAboutChanges();
		// } else {
		// engine.say("I hope to see you again sometime.");
		// }
		// }
		// });
		npc.addGoodbye();
	}

	@Override
	public String getName() {
		return "MeetMonogenes";
	}
}
