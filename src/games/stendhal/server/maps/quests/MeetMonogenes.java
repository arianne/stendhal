/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

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
 */
public class MeetMonogenes extends AbstractQuest {
	@Override
	public String getSlotName() {
		return "Monogenes";
	}
	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Monogenes",
				"A wise old man in Semos has a map to guide newcomers through the town.",
				false);
		final SpeakerNPC npc = npcs.get("Monogenes");

		npc.addGreeting(null, new SayTextAction("Hi again, [name]. How can I #help you this time?"));

		// A little trick to make NPC remember if it has met
        // player before and react accordingly
        // NPC_name quest doesn't exist anywhere else neither is
        // used for any other purpose
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotCompletedCondition("Monogenes")),
				ConversationStates.INFORMATION_1,
				"Hello there, stranger! Don't be too intimidated if people are quiet and reserved... " +
				"the fear of Blordrough and his forces has spread all over the country, and we're all " +
				"a bit concerned. I can offer a few tips on socializing though, would you like to hear them?",
				new SetQuestAction("Monogenes", "done"));

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
			"You should introduce yourself by saying \"hi\". After this, try to keep the conversation " +
			"to the topics they bring up; suitable subjects will be highlighted #'like this'. A few " +
			"generally safe topics of conversation are the person's #job, asking for #help, asking if " +
			"they have an #offer to make, and asking for a #quest to go on. Now, if you want a quick " +
			"run-down of the #buildings in Semos, just say.",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"And how are you supposed to know what's happening? By reading the Semos Tribune? Hah! Bye, then.",
			null);

		final List<String> yesnotriggers = new ArrayList<String>();
		yesnotriggers.addAll(ConversationPhrases.YES_MESSAGES);
		yesnotriggers.addAll(ConversationPhrases.NO_MESSAGES);

		npc.add(
				ConversationStates.INFORMATION_1,
				"",
				new NotCondition(new TriggerInListCondition(yesnotriggers)),
				ConversationStates.INFORMATION_1,
				"I asked you a 'yes or no' question: I can offer a few tips on socializing, would you like to hear them?",
				null);

		// he puts 'like this' into blue and so many people try that first
		npc.addReply(
				"like this",
				"That's right, like that! Now, I can show you a #map or direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith, the #bakery, or the old #village.");

		npc.addReply(
			"buildings",
			"I can show you a #map or direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith, the #bakery, the #public #chest or the old #village.");

		npc.add(
			ConversationStates.ATTENDING,
			"map", null, ConversationStates.ATTENDING,
			"I have marked the following locations on my map:\n"
			+ "1 Townhall, Mayor lives here,   2 Library,   3 Bank,   4 Bakery,\n"
			+ "5 Storage,   6 Blacksmith, Carmen,   7 Inn, Margaret \n"
        	+ "8 Temple, Ilisa,   9 Dangerous Dungeon,\n"
        	+ "10 Public Chest, \n"
        	+ "A Semos Village,   B Northern Plains and Mine, \n"
        	+ "C Very long path to Ados, \n"
        	+ "D Southern Plains and Nalwor Forest, \n"
        	+ "E Semos Village Open Field",
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

		npc.addReply(Arrays.asList("public", "public chest", "community chest", "chest"),
			"Follow the orange path on the map to arrive at the public chest. Inhabitants of Faiumoni and brave warriors throw some useful stuff in there which you can take for free. Remember: It is always good to share and give what you don't need anymore.");

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

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest("Monogenes")) {
				return res;
			}
			if (isCompleted(player)) {
				res.add("I spoke with Monogenes and he offered to give me a map. I can always get that map from him at any time, just by asking.");
			}
			return res;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}
	@Override
	public String getNPCName() {
		return "Monogenes";
	}
}
