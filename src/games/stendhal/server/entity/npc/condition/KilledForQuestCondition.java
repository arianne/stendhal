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
 * Checks the records of kills.
 * Quest string should have in proper quest index string like "creature1,w,x,y,z,creature2,a,b,c,d,creature3,..."
 * Where creature1, creature2 - names of creatures to kill;
 *       w,x and a,b - number of creatures to kill, solo and shared;
 *       y,z and c,d - number of creatures killed by player before starting this quest, both solo and shared.
 *
 * @see games.stendhal.server.entity.npc.action.StartRecordingKillsAction
 * @see games.stendhal.server.entity.npc.condition.KilledInSumForQuestCondition
 *
 * @author yoriy
 */
@Dev(category=Category.KILLS, label="Kills?")
public class KilledForQuestCondition implements ChatCondition {
	private static Logger logger = Logger.getLogger(KilledForQuestCondition.class);
	private final String questSlot;
	private final int questIndex;


	/**
	 * Creates a new KilledForQuestCondition.
	 *
	 * @param quest the quest slot
	 * @param index index of sub state
	 */
	@Dev
	public KilledForQuestCondition(String quest, @Dev(defaultValue="1") int index) {
		this.questSlot = checkNotNull(quest);
		this.questIndex = index;
	}

	/**
	 * return true if player have killed proper creature numbers.
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
					" quest slot ("+questSlot+") : ["+player.getQuest(questSlot)+"]");
			//npc.say("something wrong with you, i dont see how much monsters you killed.");
			return false;
		}

		for (int i = 0; i < tokens.size() / 5; i++) {
			final String creatureName = tokens.get(i*5);
			int toKillSolo;
			int toKillShared;
			int killedSolo;
			int killedShared;
			try {
				toKillSolo = Integer.parseInt(tokens.get(i*5 + 1));
				toKillShared = Integer.parseInt(tokens.get(i*5 + 2));
				killedSolo = Integer.parseInt(tokens.get(i*5 + 3));
				killedShared = Integer.parseInt(tokens.get(i*5 + 4));
			} catch (NumberFormatException npe) {
				logger.error("NumberFormatException while parsing numbers in quest slot "+questSlot+
						" of player "+player.getName()
						+" , creature " + i*5);
				return false;
			}
			final int diffSolo = player.getSoloKill(creatureName) - killedSolo - toKillSolo;
			final int diffShared = player.getSharedKill(creatureName) - killedShared - toKillShared;
			// if solo kills less then required, return false
			if(diffSolo < 0) {
				return false;
			}
			// if player killed solo less then required for shared, return false
			if((diffSolo+diffShared) < 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "KilledForQuestCondition";
	}

	@Override
	public int hashCode() {
		return 43669 * questSlot.hashCode() + questIndex;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KilledForQuestCondition)) {
			return false;
		}
		KilledForQuestCondition other = (KilledForQuestCondition) obj;
		return (questIndex == other.questIndex) && questSlot.equals(other.questSlot);
	}

}
