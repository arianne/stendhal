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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ChangePlayerOutfitAndPreserveTempAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerIsWearingOutfitCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.SystemPropertyCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Balloon for Bobby
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Bobby (the boy in fado city)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Mine town weeks must be on for the quest to work</li>
 * <li>If you have a balloon, Bobby asks you if he can have it</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>200 XP</li>
 * <li>50 Karma</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Infinite, but only valid during mine town weeks </li>
 * </ul>
 */

public class BalloonForBobby extends AbstractQuest {

	public static final String QUEST_SLOT = "balloon_bobby";
	// List of outfits which are balloons
	private static final Outfit[] balloonList = new Outfit[4];

	private final String NPCName = "Bobby";

	@Override
	public void addToWorld() {
		fillQuestInfo("Balloon for Bobby",
				"Young boy Bobby in Fado stares into the sky, searching for balloons. He loves them and wants to have one really bad.",
				true);
		prepareBalloonList();
		prepareRequestQuestStep();
		prepareGreetWithBalloonStep();
		prepareAttendingWithBalloonStep();
		prepareQuestItemQuestionStep();
	}

	// Load the different outfits into the list
	public void prepareBalloonList() {
		for (int i = 0; i < 4; i++) {
			balloonList[i] = new Outfit(null, null, null, null, null, null, null, null, i+1);
		}
	}

	private void prepareRequestQuestStep() {
		SpeakerNPC npc = npcs.get(NPCName);

		// Player asks Bobby for "quest".
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Would you get me a #balloon? Unless the mine town weeks are currently on,"
						+ " then I can get my own :)",
				null);

		// Player asks for quest after quest is started.
		npc.add(ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I hope you can get me a #balloon soon. Unless the mine town weeks are currently on,"
						+ " then I can get my own :)",
				null);

		// Player agrees to get a balloon.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Yay!",
				new SetQuestAction(QUEST_SLOT, 0, "start"));

		// Player refuses to get a balloon.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Aww. :'(",
				new SetQuestAction(QUEST_SLOT, 0, "rejected"));

		// Player asks about "balloon".

		npc.add(ConversationStates.ANY,
				"balloon",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"One day, i will have enough balloons to fly away!",
				null);
	}

	// If the player has a balloon (and it is mine town weeks),
	// ask if Bobby can have it
	private void prepareGreetWithBalloonStep() {

		// get a reference to Bobby
		SpeakerNPC npc = npcs.get(NPCName);

		// Add conditions for all 4 different kinds of balloons
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new NotCondition(
								new SystemPropertyCondition("stendhal.minetown")),
						new OrCondition(
								new PlayerIsWearingOutfitCondition(balloonList[0]),
								new PlayerIsWearingOutfitCondition(balloonList[1]),
								new PlayerIsWearingOutfitCondition(balloonList[2]),
								new PlayerIsWearingOutfitCondition(balloonList[3]))),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Hello, is that balloon for me?",
				null);
	}

	// If the player has a balloon but refused to give it to booby
	// after him greeting, he now has another chance.
	// (Unless it's not mine town week)
	private void prepareAttendingWithBalloonStep() {

		SpeakerNPC npc = npcs.get(NPCName);

		npc.add(
				ConversationStates.ATTENDING,
				"balloon",
				new AndCondition(
						new QuestStartedCondition(QUEST_SLOT),
						new NotCondition(
								new SystemPropertyCondition("stendhal.minetown")),
						new OrCondition(
								new PlayerIsWearingOutfitCondition(balloonList[0]),
								new PlayerIsWearingOutfitCondition(balloonList[1]),
								new PlayerIsWearingOutfitCondition(balloonList[2]),
								new PlayerIsWearingOutfitCondition(balloonList[3]))),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Is that balloon for me?",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"balloon",
				new AndCondition(
						new QuestStartedCondition(QUEST_SLOT),
						new NotCondition(
								new SystemPropertyCondition("stendhal.minetown")),
						new NotCondition(
								new OrCondition(
										new PlayerIsWearingOutfitCondition(balloonList[0]),
										new PlayerIsWearingOutfitCondition(balloonList[1]),
										new PlayerIsWearingOutfitCondition(balloonList[2]),
										new PlayerIsWearingOutfitCondition(balloonList[3])))),
				ConversationStates.ATTENDING,
				"You don't even have a balloon for me :(",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"balloon",
				new SystemPropertyCondition("stendhal.minetown"),
				ConversationStates.ATTENDING,
				"The clouds told me that the mine town weeks are still going -"
				+ " I can get my own balloons."
				+ " Come back when mine town weeks are over :)",
				null);
	}

	// Let player decide if he wants to give the balloon to bobby
	private void prepareQuestItemQuestionStep() {

		SpeakerNPC npc = npcs.get(NPCName);

		// The player has a balloon but wants to keep it to himself
		npc.add(
				ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new PlaySoundAction("pout-1"),
						new NPCEmoteAction("pouts.", false))
				);

		// Rewards to give to the player if he gives Bobby the balloon
		// NOTE: Also changes the players outfit to get rid of the balloon
		List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new ChangePlayerOutfitAndPreserveTempAction(balloonList[0], false));
		reward.add(new ChangePlayerOutfitAndPreserveTempAction(balloonList[1], false));
		reward.add(new ChangePlayerOutfitAndPreserveTempAction(balloonList[2], false));
		reward.add(new ChangePlayerOutfitAndPreserveTempAction(balloonList[3], false));
		reward.add(new IncreaseXPAction(200));
		reward.add(new IncreaseKarmaAction(50));
		reward.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		reward.add(new IncrementQuestAction(QUEST_SLOT, 1, 1));

		// The player has a balloon and gives it to Bobby
		npc.add(
				ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Yippie! Fly balloon! Fly!",
				new MultipleActions(reward));

	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest(QUEST_SLOT)) {
			final List<String> questInfo = Arrays.asList(player.getQuest(QUEST_SLOT).split(";"));
			final String questState = questInfo.get(0);
			int completedCount = 0;

			if (questInfo.size() > 1) {
				completedCount = Integer.parseInt(questInfo.get(1));
			}

			if (questState.equals("rejected")) {
				res.add("I don't like balloons.");
			} else if (questState.equals("start")) {
				res.add("I love balloons! I'm going to help " + NPCName + " get his own.");
			} else if (questState.equals("done")) {
				String balloon = "balloon";
				if (completedCount > 1) {
					balloon = balloon + "s";
				}

				res.add("I have found and given " + Integer.toString(completedCount) + " beautiful " + balloon + " for " + NPCName + ".");
			}
		}

		return res;
	}

	@Override
	public String getName() {
		return "BalloonForBobby";
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getRegion() {
		return Region.FADO_CITY;
	}

	@Override
	public String getNPCName() {
		return NPCName;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return true;
	}
}
