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
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Get tomi his Ice.
 *
 * PARTICIPANTS:
 * <ul>
 * <li>tomi, a captive in hell</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>tomi asks cryptic messages about ice</li>
 * <li>if you have an ice sword you get rewarded</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>XP</li>
 * <li>some karma</li>
 * <li>amusement</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>yes tomi takes as many ice as you please</li>
 * <li>bigger reward each time - with a square law on the XP</li>
 * </ul>
 */
public class HelpTomi extends AbstractQuest {
	/**
	 * Number of repetitions after which the XP growth becomes linear, instead
	 * of quadratic.
	 */
	private static final int N_0 = 10;

	private static final String QUEST_SLOT = "help_tomi";
	private static final String extraTrigger = "ice";
	private List<String> questTrigger;


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
		res.add("I met Tomi, a boy being tortured in hell.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.startsWith("done")) {
			res.add("Tomi asked for \"ice\" and took the ice sword I was carrying!");
			// provided quest isn't in 'old version' we should be able to check how many times it was done
			if (!"done".equals(questState)) {
				final int repetitions = player.getNumberOfRepetitions(getSlotName(), 1);
				if (repetitions>1) {
					res.add("I've given " + repetitions + " ice swords to Tomi so far.");
				}
			}
		}
		return res;
	}

	private void step1() {
		final SpeakerNPC npc = npcs.get("tomi");

		// says quest or ice and doesn't have an ice sword and hasn't brought one before
		npc.add(ConversationStates.ATTENDING,
			questTrigger,
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("ice sword"))),
			ConversationStates.ATTENDING,
			"my ice? ice plz", null);

		// says quest or ice and doesn't have an ice sword and has brought one in the past
		npc.add(ConversationStates.ATTENDING,
			questTrigger,
			new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("ice sword"))),
			ConversationStates.ATTENDING,
			"where is my ice?", null);

		// says quest or ice and has ice sword with him (first time)
		// player gets a karma bonus and some xp
		npc.add(ConversationStates.ATTENDING,
			questTrigger,
			new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("ice sword")),
			ConversationStates.ATTENDING,
			"my ice :)",
			new MultipleActions(new DropItemAction("ice sword"), new IncreaseXPAction(1000), new IncreaseKarmaAction(30.0), new SetQuestAction(QUEST_SLOT, "done;1")));

		// says quest or ice and has ice sword with him (second+ time)
		// player gets a karma bonus and some xp
		npc.add(ConversationStates.ATTENDING,
			questTrigger,
			new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("ice sword")),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				// we are storing the number of times the player has done the quest in the quest slot like
				// done;N. We reward based on this number. If the quest slot isn't split like this and only 'done'
				// we assume it was just done once (sorry, guys)
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					int N;
					// compatibility with old version
					final String questState = player.getQuest(QUEST_SLOT);
					if ("done".equals(questState)) {
						N = 2;
					} else {
						final String[] questparts = questState.split(";");
						N = Integer.parseInt(questparts[1]) + 1;
					}
					player.drop("ice sword");
						player.addKarma(N * 15.0);
						player.setQuest(QUEST_SLOT, "done;" + Integer.toString(N));
						// Used to be n * n, but it became a problem when with
						// overly high rewards when players noticed how it worked
						player.addXP(N * Math.min(N, N_0) * 1000);
						// make the number of smilies correspond to how many
						// times you helped him
						StringBuilder saybuf = new StringBuilder();
						saybuf.append("my ice ");
						for (int i = 0; i < N; i++) {
							saybuf.append(":) ");
						}
					npc.say(saybuf.toString());
				}
			});

	}

	@Override
	public void addToWorld() {
		// want "ice" and quest_messages to have same meaning in this quest
		fillQuestInfo(
				"Help Tomi",
				"Tomi, a boy being tortured in the hot depths of hell, sweats all over his body. The only thing which can help him is... ICE!",
				true);
	    questTrigger = ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, extraTrigger);
		step1();
	}

	@Override
	public String getName() {
		return "HelpTomi";
	}

	// there is a minimum level requirement to get into hell - this quest is in hell
	@Override
	public int getMinLevel() {
		return 200;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return true;
	}
	@Override
	public String getNPCName() {
		return "Tomi";
	}

	@Override
	public String getRegion() {
		return Region.HELL;
	}
}
