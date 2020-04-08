/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;


/**
 * A class to expose quest making to Lua.
 */
public class QuestHelper {

	// static instance
	private static QuestHelper instance;


	public static QuestHelper get() {
		if (instance == null) {
			instance = new QuestHelper();
		}

		return instance;
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @return
	 * 		New LuaQuest instance.
	 */
	public LuaQuest createQuest() {
		return new LuaQuest();
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @param name
	 * 		The quest name.
	 * @param slotName
	 * 		The slot identifier.
	 * @param minLevel
	 * 		Recommended minimum level.
	 * @return
	 * 		New LuaQuest instance.
	 */
	public LuaQuest createQuest(final String slotName, final String name) {
		return new LuaQuest(slotName, name);
	}


	@SuppressWarnings("unused")
	private class LuaQuest extends AbstractQuest {

		private String slotName = null;
		private int minLevel = 0;
		private String region = null;
		private String npcName = null;
		private boolean visible = true;

		private LuaFunction add = null;
		private LuaFunction remove = null;
		private LuaFunction history = null;
		private LuaFunction formattedHistory = null;
		private LuaFunction startedCheck = null;
		private LuaFunction repeatableCheck = null;
		private LuaFunction completedCheck = null;


		private LuaQuest() {
			questInfo.setSuggestedMinLevel(minLevel);
			questInfo.setRepeatable(false);
		}

		/**
		 *
		 * @param name
		 * 		The quest name.
		 * @param slotName
		 * 		The slot identifier.
		 * @param minLevel
		 * 		Recommended minimum level.
		 */
		private LuaQuest(final String slotName, final String name) {
			setSlotName(slotName);
			setName(name);
			questInfo.setSuggestedMinLevel(minLevel);
			questInfo.setRepeatable(false);
		}

		/**
		 * This must be called in order for the quest to be added to game.
		 *
		 * (alternatively call questSystem:cacheQuest(LuaQuest))
		 */
		public void register() {
			StendhalQuestSystem.get().cacheQuest(this);
		}

		/**
		 * Gets the boolean return value of a Lua function.
		 *
		 * @param lf
		 * 		Lua function to be called.
		 * @return
		 * 		Returned value of the called Lua function.
		 */
		private boolean checkBoolFunction(final LuaFunction lf) {
			final LuaValue result = lf.call();
			if (result.isboolean()) {
				return result.toboolean();
			}

			return false;
		}

		@Override
		public String getName() {
			//return questInfo.getName();

			final StringBuilder sb = new StringBuilder();
			final char[] name = getOriginalName().toCharArray();
			boolean titleCase = true;

			for (char c: name) {
				if (Character.isSpaceChar(c)) {
					titleCase = true;
				} else if (titleCase) {
					c = Character.toTitleCase(c);
					titleCase = false;
				}

				sb.append(c);
			}

			return sb.toString().replace(" ", "");
		}

		/**
		 * Retrieves unformatted quest name.
		 *
		 * @return
		 * 		Unmodified quest name string.
		 */
		public String getOriginalName() {
			return questInfo.getName();
		}

		@Override
		public String getSlotName() {
			return slotName;
		}

		@Override
		public List<String> getHistory(final Player player) {
			final List<String> ret = new LinkedList<>();

			if (history == null) {
				return ret;
			}

			final LuaValue result = history.call();
			if (result.istable()) {
				for (final LuaValue lv: result.checktable().keys()) {
					if (lv.isstring()) {
						ret.add(lv.tojstring());
					}
				}
			}

			return ret;
		}

		@Override
		public List<String> getFormattedHistory(final Player player) {
			if (formattedHistory == null) {
				return super.getFormattedHistory(player);
			}

			final List<String> ret = new LinkedList<>();
			final LuaValue result = history.call();
			if (result.istable()) {
				for (final LuaValue lv: result.checktable().keys()) {
					if (lv.isstring()) {
						ret.add(lv.tojstring());
					}
				}
			}

			return ret;
		}

		@Override
		public int getMinLevel() {
			return minLevel;
		}

		@Override
		public String getRegion() {
			return region;
		}

		@Override
		public String getNPCName() {
			return npcName;
		}

		@Override
		public boolean isVisibleOnQuestStatus() {
			return visible;
		}

		@Override
		public boolean isStarted(final Player player) {
			if (startedCheck == null) {
				return super.isStarted(player);
			}

			return checkBoolFunction(startedCheck);
		}

		@Override
		public void addToWorld() {
			if (add != null) {
				add.invoke(); // or should this be add.call()?
			}
		}

		@Override
		public boolean removeFromWorld() {
			if (remove != null) {
				return checkBoolFunction(remove);
			}

			return false;
		}

		@Override
		public boolean isRepeatable(final Player player) {
			if (repeatableCheck != null) {
				return checkBoolFunction(repeatableCheck);
			}

			return questInfo.getRepeatable();
		}

		@Override
		public boolean isCompleted(final Player player) {
			if (completedCheck == null) {
				return super.isCompleted(player);
			}

			return checkBoolFunction(completedCheck);
		}

		/**
		 * Sets the quest name string.
		 *
		 * @param name
		 * 		Quest name string to be returned when getName() is called.
		 */
		public void setName(final String name) {
			questInfo.setName(name);
		}

		/**
		 * Sets the quest description string.
		 *
		 * @param desc
		 * 		Quest description string.
		 */
		public void setDescription(final String desc) {
			questInfo.setDescription(desc);
		}

		/**
		 * Sets the quest identifier string.
		 *
		 * @param slotName
		 * 		Slot identifier string to be returned when getSlotName() is called.
		 */
		public void setSlotName(final String slotName) {
			this.slotName = slotName;
		}

		/**
		 * Sets the recommended minimum level.
		 *
		 * @param minLevel
		 * 		Level to return when getMinLevel() is called.
		 */
		public void setMinLevel(final Integer minLevel) {
			if (minLevel != null) {
				this.minLevel = minLevel;
			}

			questInfo.setSuggestedMinLevel(this.minLevel);
		}

		/**
		 * Sets the quest region.
		 *
		 * @param region
		 * 		Region string to be returned when getRegion() is called.
		 */
		public void setRegion(final String region) {
			this.region = region;
		}

		/**
		 * Sets the NPC name.
		 *
		 * @param npcName
		 * 		NPC name to return when getNPCName() is called.
		 */
		public void setNPCName(final String npcName) {
			this.npcName = npcName;
		}

		/**
		 * Sets whether or not the quest should be shown in the travel log.
		 *
		 * @param visible
		 * 		If <code>true</code>, quest will be displayed in travel log.
		 */
		public void setVisibleOnQuestStatus(final boolean visible) {
			this.visible = visible;
		}

		/**
		 * Sets the repeatable status of the quest.
		 *
		 * (overrides setCompletedCheckFunction)
		 *
		 * @param repeatable
		 * 		If <code>true</code>, the quest is repeatable.
		 */
		public void setRepeatable(final boolean repeatable) {
			questInfo.setRepeatable(repeatable);
		}

		/**
		 * Sets the function for adding the quest to the game.
		 *
		 * @param addFunction
		 * 		Function to invoke when addToWorld() is called.
		 */
		public void setAddFunction(final LuaFunction add) {
			this.add = add;
		}

		/**
		 * Sets the function for removing the quest from the game.
		 *
		 * @param remove
		 * 		Function to invoke when removeFromWorld() is called.
		 */
		public void setRemoveFunction(final LuaFunction remove) {
			this.remove = remove;
		}

		/**
		 * Sets the function for retrieving history of quest state.
		 *
		 * @param history
		 * 		Function to invoke when getHistory() is called.
		 */
		public void setHistoryFunction(final LuaFunction history) {
			this.history = history;
		}

		/**
		 * Sets the function for retrieving formatted history of quest state.
		 *
		 * @param formattedHistory
		 * 		Function to invoke when getFormattedHistory() is called.
		 */
		public void setFormattedHistoryFunction(final LuaFunction formattedHistory) {
			this.formattedHistory = formattedHistory;
		}

		/**
		 * Sets the function for checking if the quest is started.
		 *
		 * @param startedCheck
		 * 		Function to invoke when isStarted() is called.
		 */
		public void setStartedCheckFunction(final LuaFunction startedCheck) {
			this.startedCheck = startedCheck;
		}

		/**
		 * Sets the function for checking if the quest is repeatable.
		 *
		 * @param repeatableCheck
		 * 		Function to invoke when isRepeatable() is called.
		 */
		public void setRepeatableCheckFunction(final LuaFunction repeatableCheck) {
			this.repeatableCheck = repeatableCheck;
		}

		/**
		 * Sets the function for checking if the quest is completed.
		 *
		 * @param completedCheck
		 * 		Function to invoke when isCompleted() is called.
		 */
		public void setCompletedCheckFunction(final LuaFunction completedCheck) {
			this.completedCheck = completedCheck;
		}
	}
}
