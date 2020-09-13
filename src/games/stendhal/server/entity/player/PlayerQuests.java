/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.util.QuestUtils;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Accesses the player quest states.
 *
 * @author hendrik
 */
class PlayerQuests {
	private final Player player;

	private static Logger logger = Logger.getLogger(PlayerQuests.class);


	public PlayerQuests(final Player player) {
		this.player = player;
	}

	/**
	 * Checks whether the player has completed the given quest or not.
	 *
	 * @param name
	 *            The quest's name
	 * @return true if the quest has been completed by the player
	 */
	public boolean isQuestCompleted(final String name) {
		final String info = getQuest(name, 0);

		if (info == null) {
			return false;
		}

		return info.equals("done");
	}

	/**
	 * Checks whether the player has made any progress in the given quest or
	 * not. For many quests, this is true right after the quest has been
	 * started.
	 *
	 * @param name
	 *            The quest's name
	 * @return true iff the player has made any progress in the quest
	 */
	public boolean hasQuest(final String name) {
		return (player.getKeyedSlot("!quests", QuestUtils.evaluateQuestSlotName(name)) != null);
	}

	/**
	 * Gets the player's current status in the given quest.
	 *
	 * @param name
	 *            The quest's name
	 * @return the player's status in the quest
	 */
	public String getQuest(final String name) {
		return player.getKeyedSlot("!quests", QuestUtils.evaluateQuestSlotName(name));
	}

	/**
	 * Allows to store the player's current status in a quest in a string. This
	 * string may, for instance, be "started", "done", a semicolon- separated
	 * list of items that need to be brought/NPCs that need to be met, or the
	 * number of items that still need to be brought. Note that the string
	 * "done" has a special meaning: see isQuestComplete().
	 *
	 * @param name
	 *            The quest's name
	 * @param status
	 *            the player's status in the quest. Set it to null to completely
	 *            reset the player's status for the quest.
	 */
	public void setQuest(final String name, final String status) {
		final String oldStatus = player.getKeyedSlot("!quests", QuestUtils.evaluateQuestSlotName(name));
		player.setKeyedSlot("!quests", QuestUtils.evaluateQuestSlotName(name), status);
		if ((status == null) || !status.equals(oldStatus)) {
			new GameEvent(player.getName(), "quest", QuestUtils.evaluateQuestSlotName(name), status).raise();
		}
		// check for reached achievements
		SingletonRepository.getAchievementNotifier().onFinishQuest(player);
	}


	/**
	 * Gets the player's current status in the given quest.
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return the player's status in the quest
	 */
	public String getQuest(final String name, final int index) {
		String state = getQuest(name);
		if (state == null) {
			return null;
		}

		if(index == -1) {
			return state;
		}

		String[] elements = state.split(";");
		if (index < elements.length) {
			return elements[index];
		}
		return "";
	}

	/**
	 * Allows to store the player's current status in a quest in a string. This
	 * string may, for instance, be "started", "done", a semicolon- separated
	 * list of items that need to be brought/NPCs that need to be met, or the
	 * number of items that still need to be brought. Note that the string
	 * "done" has a special meaning: see isQuestComplete().
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to change (separated by ";")
	 * @param subStatus
	 *            the player's status in the quest. Set it to null to completely
	 *            reset the player's status for the quest.
	 */
	public void setQuest(final String name, final int index, final String subStatus) {
		String state = getQuest(name);
		if (state == null) {
			state = "";
		}
		String[] elements = state.split(";");
		if (elements.length <= index) {
			String[] temp = new String[index + 1];
			System.arraycopy(elements, 0, temp, 0, elements.length);
			elements = temp;
		}

		elements[index] = subStatus;
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				res.append(";");
			}
			if (elements[i] != null) {
				res.append(elements[i]);
			}
		}
		setQuest(name, res.toString());
	}

	public List<String> getQuests() {
		final RPSlot slot = player.getSlot("!quests");
		final RPObject quests = slot.iterator().next();

		final List<String> questsList = new LinkedList<String>();
		for (final String quest : quests) {
			if (!quest.equals("id") && !quest.equals("zoneid")) {
				questsList.add(quest);
			}
		}
		return questsList;
	}

	public void removeQuest(final String name) {
		player.setKeyedSlot("!quests", QuestUtils.evaluateQuestSlotName(name), null);
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name
	 *            quest
	 * @param states
	 *            valid states
	 * @return true, if the quest is in one of theses states, false otherwise
	 */
	public boolean isQuestInState(final String name, final String... states) {
		final String questState = getQuest(name);

		if (questState != null) {
			for (final String state : states) {
				if (questState.equals(state)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name
	 *            quest
	 * @param index
	 *            quest index
	 * @param states
	 *            valid states
	 * @return true, if the quest is in one of theses states, false otherwise
	 */
	public boolean isQuestInState(final String name, final int index, final String... states) {
		final String questState = getQuest(name, index);

		if (questState != null) {
			for (final String state : states) {
				if (questState.equals(state)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the recorded item stored in a substate of quest slot
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return the name of the required item (no formatting)
	 */
	public String getRequiredItemName(final String name, final int index) {
		if (!player.hasQuest(name)) {
			logger.error(player.getName() + " does not have quest " + name);
			return "";
		}
		String questSubString = getQuest(name, index);
		final String[] elements = questSubString.split("=");
		String questItem = elements[0];
		return questItem;
	}

	/**
	 * Gets the recorded item quantity stored in a substate of quest slot
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return required item quantity
	 */
	public int getRequiredItemQuantity(final String name, final int index) {
		int amount = 1;
		if (!player.hasQuest(name)) {
			logger.error(player.getName() + " does not have quest " + name);
			return amount;
		}
		String questSubString = getQuest(name, index);
		final String[] elements = questSubString.split("=");
		if(elements.length > 1) {
			amount=MathHelper.parseIntDefault(elements[1], 1);
		}
		return amount;

	}

	/**
	 * Gets the number of repetitions in a substate of quest slot
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return the integer value in the index of the quest slot, used to represent a number of repetitions
	 */
	public int getNumberOfRepetitions(final String name, final int index) {
		if (!player.hasQuest(name)) {
			logger.error(player.getName() + " does not have quest " + name);
			return 0;
		}
		String questState = player.getQuest(name, index);
		return MathHelper.parseIntDefault(questState, 0);
	}

}
