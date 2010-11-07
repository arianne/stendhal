package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.npc.action.*;
import games.stendhal.server.entity.npc.condition.*;
import java.util.*;

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


	public void addToWorld() {
		super.addToWorld();
		prepareBalloonList();
		prepareGreetWithBalloonStep();
		prepareAttendingWithBalloonStep();
		prepareQuestItemQuestionStep();
	}

	// Load the different outfits into the list
	public void prepareBalloonList() {
		for (int i = 0; i < 4; i++) {
			balloonList[i] = new Outfit(i+1,null,null,null,null);
		}

	}

	// If the player has a balloon (and it is mine town weeks),
	// ask if Bobby can have it
	private void prepareGreetWithBalloonStep() {

		// get a reference to Bobby
		SpeakerNPC npc = npcs.get("Bobby");

		// Add conversation states for all 4 different kinds of balloons
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
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

		SpeakerNPC npc = npcs.get("Bobby");

		npc.add(
				ConversationStates.ATTENDING,
				"balloon",
				new AndCondition(
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

		SpeakerNPC npc = npcs.get("Bobby");

		// The player has a balloon but wants to keep it to himself
		npc.add(
				ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"*pouts*",
				null);

		// Rewards to give to the player if he gives Bobby the balloon
		// NOTE: Also changes the players outfit to get rid of the balloon
		List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new ChangePlayerOutfitAction(balloonList[0], false));
		reward.add(new ChangePlayerOutfitAction(balloonList[1], false));
		reward.add(new ChangePlayerOutfitAction(balloonList[2], false));
		reward.add(new ChangePlayerOutfitAction(balloonList[3], false));
		reward.add(new IncreaseXPAction(200));
		reward.add(new IncreaseKarmaAction(50));

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
	public String getName() {
		return "BalloonForBobby";
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

}
