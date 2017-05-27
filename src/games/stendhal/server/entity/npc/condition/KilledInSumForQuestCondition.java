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
package games.stendhal.server.entity.npc.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checking sum of quest creatures kills in player's quest and kills slots..
 * Quest string should have in proper quest index string like "creature1,w,x,y,z,creature2,a,b,c,d,creature3,..."
 * Where creature1, creature2 - names of creatures to kill;
 *       w,x and a,b - number of creatures to kill, solo and shared;
 *       y,z and c,d - number of creatures killed by player before starting this quest, both solo and shared.
 *
 * @see games.stendhal.server.entity.npc.action.StartRecordingKillsAction
 * @see games.stendhal.server.entity.npc.condition.KilledForQuestCondition
 *
 * @author yoriy
 */
@Dev(category=Category.IGNORE, label="Kills?")
public class KilledInSumForQuestCondition implements ChatCondition {
	private static Logger logger = Logger.getLogger(KilledInSumForQuestCondition.class);
	private final String questSlot;
	private final int questIndex;
	private final int killsSum;


	/**
	 * Creates a new KilledInSumForQuestCondition.
	 *
	 * @param quest - the quest slot
	 * @param index - quest slot index where information stored
	 * @param killsSum - required sum of creatures kills
	 */
	public KilledInSumForQuestCondition(String quest, int index, int killsSum) {
		this.questSlot = checkNotNull(quest);
		this.questIndex = index;
		this.killsSum = killsSum;
	}

	/**
	 * return true if player have killed proper sum of creatures.
	 */
	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		final String temp = player.getQuest(questSlot, questIndex);
		if (temp == null) {
			return false;
		}
		final List<String> tokens = Arrays.asList(temp.split(","));
		// check for size - it should be able to divide by 5 without reminder.
		if ((tokens.size() % 5) != 0) {
			logger.error("Wrong record in player's "+player.getName()+
					" quest slot ("+questSlot+
					"), position "+questIndex+
					" : ["+	player.getQuest(questSlot)+
					"]");
			//npc.say("something wrong with you, i dont see how much monsters you killed.");
			// TODO: clear player's quest slot
			return false;
		}
		int sum=0;
		for (int i = 0; i < tokens.size() / 5; i++) {
			final String creatureName=tokens.get(i*5);
			int killedSolo;
			int killedShared;
			try {
				killedSolo=Integer.parseInt(tokens.get(i*5 + 3));
				killedShared=Integer.parseInt(tokens.get(i*5 + 4));
			} catch (NumberFormatException npe) {
				logger.error("NumberFormatException while parsing numbers in quest slot "+questSlot+
						" of player "+player.getName()
						+" , creature " + i*5);
				// TODO: clear player's quest slot
				return false;
			}
			final int diffSolo = player.getSoloKill(creatureName) - killedSolo;
			final int diffShared = player.getSharedKill(creatureName) - killedShared;
			sum = sum + diffSolo + diffShared;
		}
		if(sum < killsSum) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "KilledForQuestCondition";
	}

	@Override
	public int hashCode() {
		return 43691 * questSlot.hashCode() + 43711 * questIndex + killsSum;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KilledInSumForQuestCondition)) {
			return false;
		}
		KilledInSumForQuestCondition other = (KilledInSumForQuestCondition) obj;
		return (questIndex == other.questIndex)
			&& (killsSum == other.killsSum)
			&& questSlot.equals(other.questSlot);
	}


}
