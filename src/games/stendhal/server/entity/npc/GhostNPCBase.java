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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.FindGhosts;

/**
 * Base class for ghost NPCs.
 *
 * @author Martin Fuchs
 */
//TODO: replace this base class with the normal way quests actions are written, join RatKidsNPCBase and GhostNPCBase, split XXXGreetingAction to use ChatConditions
public abstract class GhostNPCBase extends SpeakerNPC {

	private static Logger logger = Logger.getLogger(GhostNPCBase.class);
	private static final String QUEST_SLOT = FindGhosts.QUEST_SLOT;

	public GhostNPCBase(final String name) {
		super(name);
	}

	@Override
	protected abstract void createPath();

	@Override
	protected void createDialog() {
		add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new GreetingMatchesNameCondition(getName()), true,
				ConversationStates.IDLE, null, new GhostGreetingAction());
	}

	/**
	 * ChatAction common to all ghost NPCs.
	 *
	 * @author Martin Fuchs
	 */
	private static class GhostGreetingAction implements ChatAction {
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			if (!player.hasQuest(QUEST_SLOT) || player.isQuestInState(QUEST_SLOT, "rejected")) {
				player.setQuest(QUEST_SLOT, "looking:said");
			}
			final String npcQuestText = player.getQuest(QUEST_SLOT);
			final String[] npcDoneText = npcQuestText.split(":");
			// although all names our stored as lower case from now on,
			// older versions did not,
			// so we have to be compatible with them
			final String lookStr;
			final String saidStr;
			if (npcDoneText.length > 1) {
				lookStr = npcDoneText[0].toLowerCase();
				saidStr = npcDoneText[1].toLowerCase();

				final List<String> list = Arrays.asList(lookStr.split(";"));
				String npcName = npc.getName().toLowerCase();
				if (list.contains(npcName) || player.isQuestCompleted(QUEST_SLOT)) {
					npc.say("Please, let the dead rest in peace.");
				} else {
					player.setQuest(QUEST_SLOT, lookStr + ";" + npcName
									+ ":" + saidStr);
					npc.say("Remember my name ... " + npc.getName() + " ... "
							+ npc.getName() + " ...");
					player.addXP(100);
				}
			} else {
				// compatibility with older broken quest slots. fix them.
				logger.warn("Player " + player.getTitle() + " found with find_ghosts quest slot in state " + player.getQuest(QUEST_SLOT) + " - now setting this to done.");
				player.setQuest(QUEST_SLOT, "done");
				npc.say("Please, let the dead rest in peace.");
			}
		}
	}
}
