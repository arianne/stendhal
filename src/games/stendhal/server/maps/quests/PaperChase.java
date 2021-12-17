/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.events.TeleportListener;
import games.stendhal.server.core.events.TeleportNotifier;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.LoadSignFromHallOfFameAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetHallOfFameToAgeDiffAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToPlayerAgeAction;
import games.stendhal.server.entity.npc.action.SetQuestToYearAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.SystemPropertyCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.QuestUtils;

/**
 * A kind of paper chase.
 *
 * @author hendrik
 */
public class PaperChase extends AbstractQuest implements TeleportListener {
	private static final Logger logger = Logger.getLogger(PaperChase.class);
	private static final String QUEST_SLOT = "paper_chase_20[year]";

	private static final int TELEPORT_PENALTY_IN_MINUTES = 10;

	private static final List<String> NPC_IDLE = Arrays.asList();

	private List<String> points = Arrays.asList("Fiete", "Marcellus", "Marianne", "Ermenegilda", "Pierre", "Julia", "Christina", "Fidorea");

	private Map<String, String> texts = new HashMap<String, String>();

	private Map<String, String> greetings = new HashMap<String, String>();

	private LoadSignFromHallOfFameAction loadSignFromHallOfFame;


	private void setupGreetings() {
		greetings.put("Fiete", "Nice! You finnally arrived! Birds told me ages ago.");
		greetings.put("Marcellus", "Thanks for visiting me at my post.");
		greetings.put("Marianne", "Oh, hello. Nice to meet you. I would like to ask you for some eggs from those scary chickens.");
		greetings.put("Ermenegilda", "Oh, don't be scared. I can #heal you, if you like.");
		greetings.put("Pierre", "Please consider to continue your journey in a lovely made bear constume.");
		greetings.put("Julia", "If you like, take a rest from your journey and enjoy a book or two.");
		greetings.put("Christina", "I really wished, I was able to provide some food for a traveler like yourself.");
	}


	private void setupTexts() {
		texts.put("Fiete", "The next person you should find, is responsible for port operations south of Deniran.");
		texts.put("Marcellus", "The next person guards the bridge west of here.");
		texts.put("Marianne", "The next person you should find, is a little girl afraid of chicken.");
		texts.put("Ermenegilda", "But I see, you are in a hurry to meet the next person on the list: An old scary lady at the market. But she is nice and heals people in need.");
		texts.put("Pierre", "The next person is a well known costume maker.");
		texts.put("Julia", "I am sure the next person would love it. She is a book work, but quite cute.");
		texts.put("Christina", "If a book cannot grant you some rest, perhaps some bread will. The bakery will be the next stop on your jouney.");
		texts.put("Fidorea", "The final person to talk to, is the one who started all this. And I am sure, that you will get plenty of food there.");
	}

	private String getFameType() {
		String questSlot = QuestUtils.evaluateQuestSlotName(QUEST_SLOT);
		return questSlot.substring(questSlot.length() - 1);

	}

	/**
	 * Handles all normal points in this paper chase (without the first and last.
	 * one)
	 */
	private class PaperChasePoint implements ChatAction {
		private final int idx;

		PaperChasePoint(final int idx) {
			this.idx = idx;
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
			final String state = points.get(idx);
			final String next = points.get(idx + 1);
			final String questState = player.getQuest(QUEST_SLOT, 0);

			// player does not have this quest or finished it
			if (questState == null) {
				raiser.say("Please talk to Fidorea in the Mine Town north of Semos to start the paper chase.");
				return;
			}

			final String nextNPC = questState;

			// is the player supposed to speak to another NPC?
			if (!nextNPC.equals(state)) {
				raiser.say("What do you say? \"" + texts.get(nextNPC) + "\" That's obviously not me.");
				return;
			}

			// send player to the next NPC and record it in quest state
			raiser.say(greetings.get(state) + " " + texts.get(next) + " Good luck!");
			player.setQuest(QUEST_SLOT, 0, next);
			player.addXP((idx + 1) * 10);
		}

	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * Adds the task to the specified NPC. Note that the start and end of this
	 * quest have to be coded specially.
	 *
	 * @param idx
	 *            index of way point
	 */
	private void addTaskToNPC(final int idx) {
		final String state = points.get(idx);
		final SpeakerNPC npc = npcs.get(state);
		if (npc == null) {
			logger.error("NPC " + state + " missing for paper chase");
		}
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase", "paperchase"), new SystemPropertyCondition("stendhal.minetown"),
				ConversationStates.ATTENDING, null, new PaperChasePoint(idx));
		if (NPC_IDLE.contains(state)) {
			npc.add(ConversationStates.ANY, Arrays.asList("paper", "chase", "paperchase"), new SystemPropertyCondition("stendhal.minetown"),
					ConversationStates.ANY, null, new PaperChasePoint(idx));
		}
	}


	private void createHallOfFameSign() {
		loadSignFromHallOfFame = new LoadSignFromHallOfFameAction(null, "Those who travelled the world on behalf of Fidorea:\n", getFameType(), 2000, true);
		loadSignFromHallOfFame.fire(null, null, null);
	}

	/**
	 * sets the sign to show the hall of fame
	 *
	 * @param sign a Sign or <code>null</code>.
	 */
	public void setSign(Sign sign) {
		loadSignFromHallOfFame.setSign(sign);
		loadSignFromHallOfFame.fire(null, null, null);
	}

	public void addToStarterNPCs() {
		SpeakerNPC npc = npcs.get("Fidorea");

		ChatAction startAction = new MultipleActions(
			new SetQuestAction(QUEST_SLOT, 0, points.get(0)),
			new SetQuestToPlayerAgeAction(QUEST_SLOT, 1),
			new SetQuestToYearAction(QUEST_SLOT, 2));

		// Fidorea introduces the quests
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING,
			"I have nothing to do for you. But thanks for asking.",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestNotStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.QUEST_OFFERED,
			"Those who had to stay at home because of their duties, have prepared a #paper #chase.",
			null);
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("paper", "chase"),
			new SystemPropertyCondition("stendhal.minetown"),
			ConversationStates.ATTENDING,
			"You must ask every person on the trail about the #paper #chase. Your journey starts south of Deniran at the port. The person, you are looking for, is responsible for port operations. "
			+ "And just a warning: you may teleport on your journey, but every teleport will count as " + TELEPORT_PENALTY_IN_MINUTES + " minutes on the high score sign.",
			startAction);


		// add normal way points (without first and last)
		for (int i = 0; i < points.size() - 1; i++) {
			addTaskToNPC(i);
		}

		// Fidorea does the post processing of this quest
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"),
				new AndCondition(new QuestNotStartedCondition(QUEST_SLOT), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING, "Oh, that is a nice #quest.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"),
			new AndCondition(
					new QuestStartedCondition(QUEST_SLOT),
					new QuestNotInStateCondition(QUEST_SLOT, 0, "Fidorea"),
					new QuestNotInStateCondition(QUEST_SLOT, 0, "done"),
					new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING, "I guess you still have to talk to some people.", null);

		ChatAction reward = new MultipleActions(
			new IncreaseKarmaAction(15),
			new IncreaseXPAction(400),
			new SetQuestAction(QUEST_SLOT, 0, "done"),
			new EquipItemAction("empty scroll", 5),
			new SetHallOfFameToAgeDiffAction(QUEST_SLOT, 1, getFameType()),
			loadSignFromHallOfFame);

		npc.add(ConversationStates.ATTENDING, Arrays.asList("paper", "chase"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "Fidorea"), new SystemPropertyCondition("stendhal.minetown")),
			ConversationStates.ATTENDING,
			"Very good. You did the complete quest, talking to all those people around the world. I will add your name to the sign for everyone to see. And here are some magic scrolls as reward. They will help you on further travels.",
			reward);
	}


	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Paper Chase",
				"Some rumours are going around in Faiumoni. Maybe some of the guys who live around there know something.",
				false);
		setupGreetings();
		setupTexts();
		createHallOfFameSign();
		TeleportNotifier.get().registerListener(this);
	}


	@Override
	public String getName() {
		return "PaperChase";
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}


	@Override
	public void onTeleport(Player player, boolean playerAction) {
		if (!playerAction) {
			return;
		}

		if (player.hasQuest(QUEST_SLOT) && !player.getQuest(QUEST_SLOT, 0).equals("done")) {
			int startAgeWithPenalty = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, 1), 0) - TELEPORT_PENALTY_IN_MINUTES;
			player.setQuest(QUEST_SLOT, 1, Integer.toString(startAgeWithPenalty));
		}
	}


	@Override
	public String getNPCName() {
		return "Fidorea";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}
}
