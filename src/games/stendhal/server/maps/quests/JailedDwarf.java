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

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Jailed Dwarf
 *
 * PARTICIPANTS: - Hunel, the guard of the Dwarf Kingdom's Prison
 *
 * STEPS:
 * <ul>
 * <li> You see Hunel locked in the cell. </li>
 * <li> Gget the key by killing the duergar king. </li>
 * <li> Speak to Hunel when you have the key. </li>
 * <li> Hunel wants to stay in, he is afraid. </li>
 * <li> You can then sell chaos equipment to Hunel. </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 2,000 XP </li>
 * <li> some karma (20) </li>
 * <li> everlasting place to sell chaos equipment </li>
 * </ul>
 *
 * REPETITIONS: - None.
 */
public class JailedDwarf extends AbstractQuest {

	private static final String QUEST_SLOT = "jailed_dwarf";



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Hunel");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Hi. As you see, I am still too nervous to leave ...",
				null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotCompletedCondition(QUEST_SLOT),
						new NotCondition(new PlayerHasItemWithHimCondition("kanmararn prison key"))),
				ConversationStates.IDLE,
				"Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles.",
				new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotCompletedCondition(QUEST_SLOT),
						new PlayerHasItemWithHimCondition("kanmararn prison key")),
				ConversationStates.ATTENDING,
				"You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ",
				new MultipleActions(new SetQuestAction(QUEST_SLOT, "done"),
						 			 new IncreaseXPAction(2000),
						 			 new IncreaseKarmaAction(20)));
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Jailed Dwarf",
				"Down in Kanmararn is an afraid dwarf locked in a cell waiting for visitors. He is supposed to be the guard, but duergars have raided the prison. He might need some armor to survive once out of it.",
				true);
		step_1();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			res.add("I need to get a key to unlock Hunel.");
			if (isCompleted(player)) {
				res.add("I killed the Duergar King and got the key to unlock Hunel. But now he's too afraid to leave, wanting to buy more and more armor before he feels safe. Poor Hunel.");
			}
			return res;
	}

	@Override
	public String getName() {
		return "JailedDwarf";
	}

	@Override
	public int getMinLevel() {
		return 60;
	}

	@Override
	public String getNPCName() {
		return "Hunel";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_DUNGEONS;
	}
}
