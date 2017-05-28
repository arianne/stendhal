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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;

/**
 * Abstract class for quests. This is a default implementation of IQuest.
 *
 * @author hendrik
 */
public abstract class AbstractQuest implements IQuest {

	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	protected QuestInfo questInfo = new QuestInfo();

	@Override
	public QuestInfo getQuestInfo(Player player) {
		return questInfo;
	}

	@Override
	public void updatePlayer(Player player) {
		// do nothing, but may be overridden by children
	}

	/**
	 * fill fields of questInfo object with info about this quest
	 * @param name - name of the quest
	 * @param description - short description of this quest in a neutral tense (not first person)
	 * @param repeatable - is quest repeatable or not
	 */
    public void fillQuestInfo(final String name, final String description, boolean repeatable) {
		questInfo.setName(name);
		questInfo.setDescription(description);
		questInfo.setRepeatable(repeatable);
		questInfo.setSuggestedMinLevel(this.getMinLevel());
	}

	/** NPCList. */
	protected NPCList npcs = SingletonRepository.getNPCList();

	/**
	 * The slot-name in !quests.
	 * @return the slot's name
	 *
	 */
	@Override
	public abstract String getSlotName();

	@Override
    public abstract void addToWorld();

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		// sub classes can implement this method but should not call super if they do
		return false;
	}

	@Override
	public List<String> getHint(final Player player) {
		return EMPTY_LIST;
	}

	// Determines if the player should be given a hint to start the quest.
	// Not a hard condition about the quest itself. (use level check ChatConditions for that)
	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public List<String> getFormattedHistory(final Player player) {
		return getHistory(player);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(getSlotName())
				&& player.isQuestCompleted(getSlotName());
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return false;
	}

	@Override
	public boolean isStarted(final Player player) {
		return player.hasQuest(getSlotName());
	}

	@Override
	public abstract String getName();

	@Override
	public boolean isVisibleOnQuestStatus() {
		return true;
	}

	/**
	 * Returns the region where the quest adventure takes place (or begins), or null for global quests
	 *
	 * @return region, or null for global quests
	 */
	@Override
	public String getRegion() {
		return null;
	}

	/**
	 * Returns the starter NPC for the quest, or null if there is none
	 *
	 * @return NPC name, or null for quests with no starter NPC
	 */
	@Override
	public String getNPCName() {
		return null;
	}
}
