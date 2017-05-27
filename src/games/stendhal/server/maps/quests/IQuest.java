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

import java.util.List;

import games.stendhal.server.entity.player.Player;

/**
 * All quests MUST implement this interface or extend the abstract class
 * AbstractQuest in order for the loader to recognize them.
 */
public interface IQuest {

	/**
	 * function will return information about this quest
	 * @param player - player for whom required quest info
	 * @return - QuestInfo object with info about this quest
	 */
	QuestInfo getQuestInfo(Player player);

	/**
	 * @param player Player
	 */
	void updatePlayer(Player player);


	/**
	 * adds the quest to the game world (e.g. by placing SpeakerNPCs there)
	 */
	void addToWorld();

	/**
	 * removes a quest from the world.
	 *
	 * @return true if the quest could be unloaded, false otherwise
	 */
	boolean removeFromWorld();

	/**
	 * Was the quest started?
	 *
	 * @param player
	 *            Player
	 * @return true, if it was started, false otherwise
	 */
	boolean isStarted(Player player);

	/**
	 * Was the quest completed?<!--sic--> Note: A quest can be completed
	 * without its status being "Done" (e. g. rejected, failed).
	 *
	 * @param player
	 *            Player
	 * @return true, if it was completed, false otherwise
	 */
	boolean isCompleted(Player player);

	/**
	 * May the quest be repeated?
	 *
	 * @param player
	 *            Player
	 * @return true, if it can be repeated, false otherwise
	 */
	boolean isRepeatable(Player player);

	/**
	 * Gets a the quest history for the given player, written in the first person.
	 *
	 * @param player
	 *            Player
	 * @return list of history item-names
	 */
	List<String> getHistory(Player player);

	List<String> getFormattedHistory(Player player);

	/**
	 * Gets a list of possible hint-names.
	 * <p>
	 * The hint system will ensure that the same hint is not displayed twice.
	 * This class creates a list of useful hints (without hints about already
	 * completed parts).
	 *
	 * @param player
	 *            Player
	 * @return list of history item-names
	 */
	List<String> getHint(Player player);

	/**
	 * Returns the name of the quest.
	 *
	 * @return name
	 */
	String getName();

	/**
	 * Returns the minimum level of player expected to start the quest. Used for choosing which hints to give.
	 * To set a hard minimum level requirement for doing the quest, use level related ChatConditions in the quest methods
	 *
	 * @return level
	 */
	int getMinLevel();

	/**
	 * Returns the slot name of the quest.
	 *
	 * @return slot name
	 */
	String getSlotName();

	/**
	 * Determines whether the quest should be shown in the Quest Status/Progress log
	 *
	 * @return true, if the quest is visible, false otherwise
	 */
	boolean isVisibleOnQuestStatus();

	/**
	 * Returns the region where the quest adventure takes place (or begins), or null for global quests
	 *
	 * @return region, or null for global quests
	 */
	String getRegion();

	/**
	 * Returns the starter NPC for the quest, or null if there is none
	 *
	 * @return NPC name, or null for quests with no starter NPC
	 */
	String getNPCName();

}
