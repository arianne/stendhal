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
package games.stendhal.server.entity.npc;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;

// import org.apache.log4j.Logger;

/**
 * Base class for rat kid NPCs.
 *
 * @author Norien
 */

// TODO: replace this base class with the normal way quests actions are written, join RatKidsNPCBase and GhostNPCBase, split XXXGreetingAction to use ChatConditions
public abstract class RatKidsNPCBase extends SpeakerNPC {

	//	private static Logger logger = Logger.getLogger(RatKidsBase.class);
	private static final String QUEST_SLOT = "find_rat_kids";

	public RatKidsNPCBase(final String name) {
		super(name);
	}

	@Override
	protected abstract void createPath();

	@Override
	protected void createDialog() {
		add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new GreetingMatchesNameCondition(getName()), true,
				ConversationStates.IDLE, null, new RatKidGreetingAction());
	}

	/**
	 * ChatAction common to all rat kid NPCs.
	 *
	 * @author Norien
	 */
	private static class RatKidGreetingAction implements ChatAction {
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			if (!player.hasQuest(QUEST_SLOT) || player.isQuestInState(QUEST_SLOT, "rejected")) {
				npc.say("Mother says I mustn't talk to strangers.");
			} else {
				final String npcQuestText = player.getQuest(QUEST_SLOT);
				final String[] npcDoneText = npcQuestText.split(":");

				final String lookStr;
				final String saidStr;
				if (npcDoneText.length > 1) {
					lookStr = npcDoneText[0].toLowerCase();
					saidStr = npcDoneText[1].toLowerCase();

					final List<String> list = Arrays.asList(lookStr.split(";"));
					String npcName = npc.getName().toLowerCase();
					if (list.contains(npcName) || player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("Oh hello again.");
					} else if ( npcDoneText.length > 1) {
						player.setQuest(QUEST_SLOT, lookStr + ";" + npcName
								+ ":" + saidStr);//
						npc.say("Hello my name is " + npc.getName() + ". Please tell mother that I am ok.");
						player.addXP(500);
					} else {
						npc.say("Mother says I mustn't talk to strangers.");
					}
				}
			}
		}
	}
}
