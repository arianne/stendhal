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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Speak with Io PARTICIPANTS: - Io
 *
 * STEPS: - Talk to Io to activate the quest and keep speaking with Io.
 *
 * REWARD: - 10 XP - 5 gold coins
 *
 * REPETITIONS: - As much as wanted, but you only get the reward once.
 */
public class MeetIo extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_io";



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met the telepath Io Flotto in Semos Temple.");
		if (isCompleted(player)) {
			res.add("Io taught me the six basic elements of telepathy and promised to remind me if I need to refresh my knowledge.");
		}
		return res;
	}

	private void prepareIO() {

		final SpeakerNPC npc = npcs.get("Io Flotto");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.HELP_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I'm a telepath and a telekinetic; I can help you by sharing my mental skills with you. Do you want me to teach you the six basic elements of telepathy? I already know the answer but I'm being polite...",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.HELP_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Do you want to repeat the six basic elements of telepathy? I already know the answer but I'm being polite...",
			null);

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_1,
			"Type #/who to ascertain the names of those adventurers who are currently present in the world of Stendhal. Do you want to learn the second basic element of telepathy?",
			null);

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_2,
			"Type #/where #username to discern where in Stendhal that person is currently roaming; you can use #'/where sheep' to keep track of any sheep you might own. To understand the system used for defining positions in Stendhal, try asking #Zynn; he knows more about it than I do. Ready for the third lesson?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			"Zynn",
			null,
			ConversationStates.INFORMATION_2,
			"His full name is Zynn Iwuhos. He spends most of his time in the library, making maps and writing historical record books. Ready for the next lesson?",
			null);

		npc.add(
			ConversationStates.INFORMATION_2,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_3,
			"Type #'/tell username message' or #'/msg username message' to talk to anybody you wish, no matter where in Stendhal that person is.  You can type #'// response' to continue talking to the last person you send a message to. Ready to learn my fourth tip?",
			null);

		npc.add(
			ConversationStates.INFORMATION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_4,
			"Press #Shift+Up at the same time to recall things you previously said, in case you need to repeat yourself. Okay, shall we move on to the fifth lesson?",
			null);

		npc.add(
			ConversationStates.INFORMATION_4,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_5,
			"Type #/support #<message> to report a problem. You can also try the IRC channel ##arianne on #'irc.libera.chat'. There is a web frontend at #https://stendhalgame.org/development/chat.html \nOkay, time for your last lesson in mental manipulation!",
			null);

		npc.add(
			ConversationStates.INFORMATION_5,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_6,
			"You can travel to the astral plane at any time, thereby saving and closing your game. Just type #/quit, or press the #Esc key, or even simply close the window. Okay! Hmm, I think you want to learn how to float in the air like I do.",
			null);

		/** Give the reward to the patient newcomer user */
		final String answer = "*yawns* Maybe I'll show you later... I don't want to overload you with too much information at once. You can get a summary of all those lessons at any time, incidentally, just by typing #/help.\n";
		npc.add(ConversationStates.INFORMATION_6,
			ConversationPhrases.YES_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.IDLE,
			answer + "Hey! I know what you're thinking, and I don't like it!",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 10));
		reward.add(new IncreaseXPAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_6,
			ConversationPhrases.YES_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.IDLE,
			answer + "Remember, don't let anything disturb your concentration.",
			new MultipleActions(reward));

		npc.add(
			ConversationStates.ANY,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"If you ever decide to widen the frontiers of your mind a bit more, drop by and say hello. Farewell for now!",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Io",
				"Io Flotto can teach about how to communicate.",
				false);
		prepareIO();
	}

	@Override
	public String getName() {
		return "MeetIo";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Io Flotto";
	}
}
