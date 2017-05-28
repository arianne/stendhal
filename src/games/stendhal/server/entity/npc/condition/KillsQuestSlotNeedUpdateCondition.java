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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checking if player's quest slot need update with newer creatures and update it if required
 * Quest string should have in proper quest index string like "creature1,w,x,y,z,creature2,a,b,c,d,creature3,..."
 * Where creature1, creature2 - names of creatures to kill;
 *       w,x and a,b - number of creatures to kill, solo and shared;
 *       y,z and c,d - number of creatures killed by player before starting this quest, both solo and shared.
 *
 * @see games.stendhal.server.entity.npc.action.StartRecordingKillsAction
 * @see games.stendhal.server.entity.npc.condition.KilledForQuestCondition
 * @see games.stendhal.server.entity.npc.condition.KilledInSumForQuestCondition
 *
 * @author yoriy
 */
@Dev(category=Category.IGNORE, label="Kills?")
public class KillsQuestSlotNeedUpdateCondition implements ChatCondition {
	private static Logger logger = Logger.getLogger(KillsQuestSlotNeedUpdateCondition.class);
	private final String questSlot;
	private final int questIndex;
	private final int questGroupIndex;
	private final List<String> creatures;
	private final boolean do_update;
    private final Map<String, List<String>> allcreatures;

	/**
	 * Creates a new KillsQuestSlotNeedUpdateCondition.
	 *
	 * @param quest - the quest slot
	 * @param index - quest slot index where information stored (creatures records)
	 * @param creatures - list of creatures required to kill by npc
	 * @param do_update - if true then player's quest slot would update
	 */
	public KillsQuestSlotNeedUpdateCondition(String quest, int index, List<String> creatures, boolean do_update) {
		this.questSlot = checkNotNull(quest);
		this.questIndex = index;
		this.creatures = creatures;
		this.do_update = do_update;
		// not applicable in this case
		this.questGroupIndex = -1;
		this.allcreatures = null;
	}


	/**
	 * Creates a new KillsQuestSlotNeedUpdateCondition.
	 *
	 * @param quest - the quest slot
	 * @param index - quest slot index where information stored (creatures group string followed by creatures records)
	 * @param allcreatures - map of creatures groups, required to kill by npc
	 * @param do_update - if true then player's quest slot would update
	 */
	public KillsQuestSlotNeedUpdateCondition(String quest, int index, Map<String, List<String>> allcreatures, boolean do_update) {
		this.questSlot = checkNotNull(quest);
		this.questGroupIndex = index;
		this.questIndex = index+1;
		this.allcreatures = allcreatures;
		this.do_update = do_update;
		// not applicable in this case
		this.creatures = null;
	}


	/**
	 * return true if player need update, or if required update was unsuccessful.
	 */
	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		String toadd="";
		List<String> neededcreatures;
		// checking what we have
		if(allcreatures==null) {
			// simple call
			neededcreatures=creatures;
		} else {
			// taking from player's quest slot
	        final String givenEnemies = player.getQuest(questSlot, questGroupIndex);
	        neededcreatures=allcreatures.get(givenEnemies);
		};

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
			return true;
		}
		// gathering creatures names
		LinkedList<String> mycreatures=new LinkedList<String>();
		for(int i=0; i<tokens.size()/5; i++) {
			mycreatures.add(tokens.get(i*5));
		}

		// check for creatures from list
		for(String monster:neededcreatures) {
			if(!mycreatures.contains(monster)) {
			   if(do_update) {
				   // adding creature name to user quest slot (tail)
				   toadd=toadd+","+monster+",0,0,0,0";
				   logger.debug("Will add <"+monster+
						        "> to player <"+player.getName()+
						        "> quest <"+questSlot+
						        "> slot <"+Integer.toString(questIndex)+
						        ">");
			   }
			   else {
				   return true;
			   }
			}
		}

		if(do_update && !toadd.isEmpty()) {
		    // now adding whole string to player's quest slot
			String finalcreatures=temp+toadd;
			logger.debug("new player <"+player.getName()+
					     "> quest <"+questSlot+
					     "> slot <"+Integer.toString(questIndex)+
					     "> value: ("+finalcreatures+")");
			player.setQuest(questSlot, questIndex, finalcreatures);
		}

		return false;
	}

	@Override
	public String toString() {
		return "KilledForQuestCondition";
	}

	@Override
	public int hashCode() {
		return 43691 * questSlot.hashCode() + 43711 * questIndex;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof KillsQuestSlotNeedUpdateCondition)) {
			return false;
		}
		KillsQuestSlotNeedUpdateCondition other = (KillsQuestSlotNeedUpdateCondition) obj;
		return (questIndex == other.questIndex)
			&& (creatures.equals(other.creatures)
			&& questSlot.equals(other.questSlot));
	}


}
