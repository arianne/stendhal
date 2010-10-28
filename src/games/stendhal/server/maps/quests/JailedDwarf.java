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

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;

/**
 * QUEST: Jailed Dwarf
 * 
 * PARTICIPANTS: - Hunel, the guard of the Dwarf Kingdom's Prison
 * 
 * STEPS: - You see Hunel locked in the cell - You get the key by killing the
 * Duergar King - You speak to Hunel when you have the key. - Hunel wants to
 * stay in, he is afraid. - You can then sell chaos equipment to Hunel.
 * 
 * REWARD: - 2000 XP - everlasting place to sell chaos equipment
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
				new QuestCompletedCondition(QUEST_SLOT),
				 ConversationStates.ATTENDING,
				 "Hi. As you see, I am still too nervous to leave ...",
				 null);
		
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new NotCondition(new PlayerHasItemWithHimCondition("kanmararn prison key"))),
				 ConversationStates.IDLE,
				 "Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles.",
				 new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestNotCompletedCondition(QUEST_SLOT), new PlayerHasItemWithHimCondition("kanmararn prison key")),
				 ConversationStates.ATTENDING,
				 "You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ",
				 new MultipleActions(new SetQuestAction(QUEST_SLOT, "done"),
						 			 new IncreaseXPAction(2000)));

	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Jailed Dwarf",
				"Down in Kanmararn, you will find an afraid dwarf locked in a cell waiting for visitors. He is supposed to be the guard, but duergars have raided the prision. He might need some armor to survive once out of it.",
				true);
		step_1();
	}
	@Override
	public String getName() {
		return "JailedDwarf";
	}
	
	@Override
	public int getMinLevel() {
		return 60;
	}
}
