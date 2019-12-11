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
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Find Rat children
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Agnus</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Agnus asks you to find her children and see if they are ok</li>
 * <li> You go find them and remember their names</li>
 * <li> You return and say the names</li>
 * <li> Agnus checks you have met them, then gives reward</li>
 * <li> Note: you can not meet the children before you started the quest with her</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 5000 XP</li>
 * <li> Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Once every 24 hours.</li>
 * </ul>
 */
public class FindRatChildren extends AbstractQuest {

	private static Logger logger = Logger.getLogger(FindRatChildren.class);

	private static final String QUEST_SLOT = "find_rat_kids";

	// twenty four hours
	private static final int REQUIRED_MINUTES = 24 * 60;

	// children names must be lower text as this is what we compare against
	private static final List<String> NEEDED_KIDS =
		Arrays.asList("avalon", "cody", "mariel", "opal");


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private List<String> missingNames(final Player player) {
		if (!player.hasQuest(QUEST_SLOT)) {
			return NEEDED_KIDS;
		}
		/*
		 * the format of the list quest slot is
		 * "looking;name;name;...:said;name;name;..."
		 */
		// put the children name to lower case so we can match it, however the player wrote the name
		final String npcDoneText = player.getQuest(QUEST_SLOT).toLowerCase();
		final String[] doneAndFound = npcDoneText.split(":");
		final List<String> result = new LinkedList<String>();
		if (doneAndFound.length > 1) {
			final String[] done = doneAndFound[1].split(";");
			final List<String> doneList = Arrays.asList(done);
			for (final String name : NEEDED_KIDS) {
				if (!doneList.contains(name)) {
					result.add(name);
				}
			}
		}
		return result;
	}

	private void askingStep() {
		final SpeakerNPC npc = npcs.get("Agnus");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.QUEST_OFFERED,
				"I feel so worried. If I only knew my #children were safe I would feel better.",
				null);

		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"Do you think you could find my children again?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Why must my children stay out so long? Please find them and tell me who is ok.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				"Thank you! I feel better now knowing my kids are safe.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"That's so nice of you. Good luck searching for them.",
				new SetQuestAction(QUEST_SLOT, "looking:said"));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh. Never mind. I'm sure someone else would be glad to help me.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -15.0));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				"children",
				null,
				ConversationStates.QUEST_OFFERED,
				"My children have gone to play in the sewers. They have been gone for a long time. Will you find them and see if they are ok?",
				null);
	}

	private void findingStep() {
		// Player goes to look for the children

	}

	private void retrievingStep() {

		final SpeakerNPC npc = npcs.get("Agnus");

		// the player returns to Agnus after having started the quest, or found
		// some kids.
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT)),
				ConversationStates.QUESTION_1,
				"If you found any of my #children, please tell me their name.", null);

		for(final String name : NEEDED_KIDS) {
			npc.add(ConversationStates.QUESTION_1, name, null,
					ConversationStates.QUESTION_1, null,
					new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String npcQuestText = player.getQuest(QUEST_SLOT).toLowerCase();
					final String[] npcDoneText = npcQuestText.split(":");
					final String lookingStr;
					final String saidStr;
					if (npcDoneText.length > 1) {
						lookingStr = npcDoneText[0];
						saidStr = npcDoneText[1];
					} else {
						// compatibility with broken quests - should never happen
						logger.warn("Player " + player.getTitle() + " found with find_rat_kids quest slot in state " + player.getQuest(QUEST_SLOT) + " - now setting this to done.");
						player.setQuest(QUEST_SLOT, "done");
						npc.say("Sorry, it looks like you have already found them after all. I got confused.");
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.ATTENDING);
						return;
					}

					final List<String> looking = Arrays.asList(lookingStr.split(";"));
					final List<String> said = Arrays.asList(saidStr.split(";"));
					String reply = "";
					List<String> missing = missingNames(player);
					final boolean isMissing = missing.contains(name);

					if (isMissing && looking.contains(name) && !said.contains(name)) {
						// we haven't said the name yet so we add it to the list
						player.setQuest(QUEST_SLOT, lookingStr
								+ ":" + saidStr + ";" + name);
						reply = "Thank you.";
					} else if (!looking.contains(name)) {
						// we have said it was a valid name but haven't seen them
						reply = "I don't think you actually checked if they were ok.";
					} else if (!isMissing && said.contains(name)) {
						// we have said the name so we are stupid!
						reply = "Yes you told me that they were ok already, thanks.";
					} else {
						assert false;
					}

					// we may have changed the missing list
					missing = missingNames(player);

					if (!missing.isEmpty()) {
						reply += " If you have seen any of my other children, please tell me who.";
						npc.say(reply);
					} else {
						player.addXP(5000);
						player.addKarma(15);
						reply += " Now that I know my kids are safe, I can set my mind at rest.";
						npc.say(reply);
						player.setQuest(QUEST_SLOT, "done;" + System.currentTimeMillis());
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});
		}

		final List<String> triggers = new ArrayList<String>();
		triggers.add(ConversationPhrases.NO_EXPRESSION);
		triggers.addAll(ConversationPhrases.GOODBYE_MESSAGES);
		npc.add(ConversationStates.QUESTION_1, triggers, null,
				ConversationStates.IDLE, "No problem, come back later.", null);

		// player says something which isn't in the needed kids list.
		npc.add(
				ConversationStates.QUESTION_1,
				"",
				new NotCondition(new TriggerInListCondition(NEEDED_KIDS)),
				ConversationStates.QUESTION_1,
				"Sorry, I don't understand you. What name are you trying to say?",
				null);

		npc.add(
				ConversationStates.QUESTION_1,
				"children",
				null,
				ConversationStates.QUESTION_1,
				"I wish to know that my children are ok. Please tell me who is ok.",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Find Rat Children",
				"Agnus, who lives in Rat City, asks young heroes to find her children and look after them. They went down into the dark tunnels and haven't returned ...",
				true);
		askingStep();
		findingStep();
		retrievingStep();
	}

	@Override
	public String getName() {
		return "FindRatChildren";
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			res.add("Agnus is really worried about her children who are missing in the tunnels. I need to find them and speak with them to check they are okay.");
			if ("rejected".equals(player.getQuest(QUEST_SLOT))) {
				res.add("I don't want to help.");
				return res;
			}
			if (!isCompleted(player)) {
				res.add("I have " + missingNames(player).size() + " " + Grammar.plnoun(missingNames(player).size(), "child") + " left to check on and tell Agnus about.");
			} else {
				res.add("Agnus was so relieved that I found her kids. Finding them earned me experience.");
			}
			return res;
	}

	@Override
	public int getMinLevel() {
		return 10;
	}

	@Override
	public String getNPCName() {
		return "Agnus";
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player, null, null);
	}

	@Override
	public String getRegion() {
		return Region.ORRIL_DUNGEONS;
	}
}
