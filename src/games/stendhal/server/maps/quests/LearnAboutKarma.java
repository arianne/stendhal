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
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Learn about Karma
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Sarzina, the friendly wizardess who also sells potions in Fado</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Sarzina asks if you are a helpful person</li>
 * <li>You get good or bad karma depending on what you say</li>
 * <li>You get the chance to learn about karma and find out what yours is.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Some Karma</li>
 * <li>Knowledge</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Can always learn about karma but not get the bonus each time</li>
 * </ul>
 */
public class LearnAboutKarma extends AbstractQuest {

	private static final String QUEST_SLOT = "learn_karma";


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
		res.add("I have met Sarzina in a hut in Fado and asked about a quest.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("done")) {
			res.add("Sarzina told me about karma and that I can come back to be reminded of how it works any time.");
		}
		return res;
	}

	private void step1() {
		final SpeakerNPC npc = npcs.get("Sarzina");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Are you someone who likes to help others?", null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"If you want to get good #karma all you have to do is be helpful to others. I know a hunter girl called Sally who needs wood, and "
			+ "I know another girl called Annie who loves icecream, well, I know many people who needs tasks doing for them regularly and I'm sure if you help them you will be rewarded, that's how karma works after all.", null);

		// player is willing to help other people
		// player gets a little karma bonus
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Wonderful! You must have good #karma.",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "done"),
					new EnableFeatureAction("karma_indicator")));

		// player is not willing to help other people
		// player gets a little karma removed
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"I knew it ... you probably have bad #karma.",
			new MultipleActions(
					new DecreaseKarmaAction(10.0),
					new SetQuestAction(QUEST_SLOT, "done"),
					new EnableFeatureAction("karma_indicator")));

		// player wants to know what karma is, and has completed the quest
		npc.add(
			ConversationStates.ATTENDING,
			"karma",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.QUESTION_1,
			"When you do a good thing like a #task for someone else, you get good karma. Good karma means you're likely to "
			+ "do well in battle and when fishing or searching for something like gold. "
			+ "Do you want to know what your karma is now?",
			null);

		// Player wants to know what karma is, but has not yet completed the
		// quest. Act like the player asked about for a task.
		npc.add(ConversationStates.ATTENDING,
				"karma",
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Are you someone who likes to help others?", null);

		// player wants to know what his own karma is
		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final long roundedkarma = Math.round(player.getKarma());
					final String Yk = "Your karma ";
					final String canseekarma = "Now you can always see your karma,";
					final String rk = Long.toString(roundedkarma);
                    if (roundedkarma > 499 ) {
                        npc.say(Yk+"is unbelievably high, "+rk+"! You must have done many good things! " + canseekarma + " it's 'in the blue'." );
                    } else if (roundedkarma > 99) {
                        npc.say(Yk+"is great, "+rk+". " + canseekarma + " it's 'in the blue' right now.");
                    } else if (roundedkarma > 5) {
                    	npc.say(Yk+"of "+rk+" is good. " + canseekarma + " and you should try to keep yours out of the 'red'.");
                    } else if (roundedkarma > -5) {
                        npc.say(Yk+"is "+rk+". " + canseekarma + " and yours is roughly in the middle of the scale.");
                    } else if (roundedkarma > -99) {
                        npc.say(Yk+"of "+rk+" is not very good. " + canseekarma + " if you had good karma it would show as blue.");
                    } else if (roundedkarma > -499) {
                        npc.say(Yk+"is terrible, "+rk+"! " + canseekarma + " and yours is well 'in the red'.");
                    } else {
                    	npc.say(Yk+"is disastrous, "+rk+"!!! " + canseekarma + " and yours hardly even registers on the scale. You must have done some bad things... ");
                    }
				}
			});

		// player doesn't want to know what his own karma is
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			null, ConversationStates.ATTENDING,
			"Fair enough! I could help you another way?", null);

		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.QUEST_MESSAGES,
				null, ConversationStates.QUESTION_1,
				"If you ask around for tasks, and you complete them, you'll increase your karma. Do you want to know what your karma is now?", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Learn About Karma",
				"Sarzina will teach about karma.",
				false);
		step1();
	}

	@Override
	public String getName() {
		return "LearnAboutKarma";
	}

	@Override
	public String getRegion() {
		return Region.FADO_CITY;
	}
	@Override
	public String getNPCName() {
		return "Sarzina";
	}
}
