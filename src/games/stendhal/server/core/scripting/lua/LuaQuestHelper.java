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
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.SimpleQuestCreator;


/**
 * Exposes quest creation & handling to Lua.
 */
public class LuaQuestHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaQuestHelper instance;

	private static StendhalQuestSystem questSystem = SingletonRepository.getStendhalQuestSystem();

	// expose SimpleQuestCreator to Lua
	public static final SimpleQuestCreator simple = SimpleQuestCreator.getInstance();


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static QuestHelper instance.
	 */
	public static LuaQuestHelper get() {
		if (instance == null) {
			instance = new LuaQuestHelper();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaQuestHelper() {
		// singleton
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @return
	 *     New LuaQuest instance.
	 */
	public LuaQuest create() {
		return new LuaQuest();
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @param slotName
	 *     The slot identifier.
	 * @return
	 *     New LuaQuest instance.
	 */
	public LuaQuest create(final String slotName) {
		return new LuaQuest(slotName);
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @param name
	 *     The quest name.
	 * @param slotName
	 *     The slot identifier.
	 * @param minLevel
	 *     Recommended minimum level.
	 * @return
	 *     New LuaQuest instance.
	 */
	public LuaQuest create(final String slotName, final String name) {
		return new LuaQuest(slotName, name);
	}

	/**
	 * Creates a new quest instance.
	 *
	 * @param slotName
	 *     The slot identifier.
	 * @param name
	 *     Reader friendly name.
	 * @param desc
	 *     Quest description.
	 * @return
	 *     New LuaQuest instance.
	 */
	public LuaQuest create(final String slotName, final String name, final String desc) {
		return new LuaQuest(slotName, name, desc);
	}

	/**
	 * Adds a quest to the world.
	 *
	 * @param quest
	 * 		Quest to be loaded.
	 */
	public void load(final IQuest quest) {
		questSystem.loadQuest(quest);
	}

	/**
	 * Removes a qeust from the world.
	 *
	 * @param questName
	 * 		String name of the quest.
	 */
	public void unload(final String questName) {
		questSystem.unloadQuest(questName);
	}

	/**
	 * Caches a quest for loading at startup.
	 *
	 * @param quest
	 * 		Quest to be cached.
	 * @deprecated
	 *     Use {@link LuaQuest.register}.
	 */
	@Deprecated
	public void cache(final IQuest quest) {
		logger.debug("LuaQuestHelper.cache deprecated. Use LuaQuest.register.");

		questSystem.cacheQuest(quest);
	}

	/**
	 * Caches a quest for loading at startup.
	 *
	 * @param quest
	 * 		Quest to be cached.
	 * @deprecated
	 *     Use {@link LuaQuest.register}.
	 */
	@Deprecated
	public void register(final IQuest quest) {
		cache(quest);
	}

	/**
	 * Checks if a quest has been loaded.
	 *
	 * @param quest
	 * 		Quest instance to be checked.
	 * @return
	 * 		<code>true</code> if the instances matches stored quests.
	 */
	public boolean isLoaded(final IQuest quest) {
		return questSystem.isLoaded(quest);
	}

	/**
	 * List all quests the player knows about.
	 *
	 * @param player
	 * 		Player to create the report for.
	 * @return
	 * 		Report.
	 */
	public String listAll(final Player player) {
		return questSystem.listQuests(player);
	}

	/**
	 * Creates a report on a specified quest for a specified player.
	 *
	 * @param player
	 * 		Player to create the report for.
	 * @param questName
	 * 		Name of quest to be reported.
	 * @return
	 * 		Report.
	 */
	public String list(final Player player, final String questName) {
		return questSystem.listQuest(player, questName);
	}

	/**
	 * Dumps the internal quest states for the specified player. This is used for the InspectAction.
	 *
	 * @param player
	 * 		Player to create report for.
	 * @return
	 * 		Report.
	 */
	public String listStates(final Player player) {
		return questSystem.listQuestsStates(player);
	}

	/**
	 * Retrieves the IQuest object for a named quest.
	 *
	 * @param questName
	 * 		Name of quest.
	 * @return
	 * 		IQuest or <code>null</code> if it does not exist.
	 */
	public IQuest getQuest(final String questName) {
		return questSystem.getQuest(questName);
	}

	/**
	 *
	 * @param questSlot
	 * @return
	 */
	public IQuest getQuestFromSlot(final String questSlot) {
		return questSystem.getQuestFromSlot(questSlot);
	}

	/**
	 *
	 * @param player
	 * @return
	 */
	public List<String> getOpen(final Player player) {
		return questSystem.getOpenQuests(player);
	}

	/**
	 *
	 * @param player
	 * @return
	 */
	public List<String> getCompleted(final Player player) {
		return questSystem.getCompletedQuests(player);
	}

	/**
	 *
	 * @param player
	 * @param region
	 * @return
	 */
	public List<String> getIncomplete(final Player player, final String region) {
		return questSystem.getIncompleteQuests(player, region);
	}

	/**
	 *
	 * @param player
	 * @return
	 */
	public List<String> getRepeatable(final Player player) {
		return questSystem.getRepeatableQuests(player);
	}

	/**
	 *
	 * @param player
	 * @param questName
	 * @return
	 */
	public String getDescription(final Player player, final String questName) {
		return questSystem.getQuestDescription(player, questName);
	}

	/**
	 *
	 * @param player
	 * @param questName
	 * @return
	 */
	public String getLevelWarning(final Player player, final String questName) {
		return questSystem.getQuestLevelWarning(player, questName);
	}

	/**
	 *
	 * @param player
	 * @param questName
	 * @return
	 */
	public List<String> getProgressDetails(final Player player, final String questName) {
		return questSystem.getQuestProgressDetails(player, questName);
	}

	/**
	 *
	 * @param player
	 * @param region
	 * @return
	 */
	public List<String> getNPCNamesForUnstartedInRegionForLevel(final Player player, final String region) {
		return questSystem.getNPCNamesForUnstartedQuestsInRegionForLevel(player, region);
	}

	/**
	 *
	 * @param player
	 * @param region
	 * @param name
	 * @return
	 */
	public List<String> getDescriptionForUnstartedInRegionFromNPCName(final Player player, final String region, final String name) {
		return questSystem.getQuestDescriptionForUnstartedQuestInRegionFromNPCName(player, region, name);
	}


	/**
	 * Class to aid with quest manipulation in Lua.
	 */
	@SuppressWarnings("unused")
	private static class LuaQuest extends AbstractQuest {

		private static LuaLogger logger = LuaLogger.get();

		private String slotName = null;
		private int minLevel = 0;
		private String region = null;
		private String npcName = null;
		private boolean visible = true;

		/**
		 * Function executed when
		 * {@link StendhalQuestSystem.loadQuest}
		 * is called.
		 */
		public LuaFunction init = null;
		public LuaFunction remove = null;
		public LuaFunction history = null;
		public LuaFunction formattedHistory = null;
		public LuaFunction startedCheck = null;
		public LuaFunction repeatableCheck = null;
		public LuaFunction completedCheck = null;


		/**
		 * Creates a new quest.
		 */
		private LuaQuest() {
			init();
		}

		/**
		 * Creates a new quest.
		 *
		 * @param slotName
		 *     The slot identifier.
		 */
		private LuaQuest(final String slotName) {
			setSlotName(slotName);

			init();
		}

		/**
		 * Creates a new quest.
		 *
		 * @param slotName
		 *     The slot identifier.
		 * @param name
		 *     Reader friendly name.
		 */
		private LuaQuest(final String slotName, final String name) {
			setSlotName(slotName);
			setName(name);

			init();
		}

		/**
		 * Creates a new quest.
		 *
		 * @param slotName
		 *     The slot identifier.
		 * @param name
		 *     Reader friendly name.
		 * @param desc
		 *     Quest description.
		 */
		private LuaQuest(final String slotName, final String name, final String desc) {
			setSlotName(slotName);
			setName(name);
			setDescription(desc);

			init();
		}

		/**
		 * Initializes default QuestInfo attributes.
		 */
		private void init() {
			questInfo.setSuggestedMinLevel(minLevel);
			questInfo.setRepeatable(false);
		}

		/**
		 * Registers quest to be added to world.
		 *
		 * {@link LuaQuest.init} must be set before this is called.
		 *
		 * (alternatively call questSystem:cacheQuest(LuaQuest))
		 */
		public void register() {
			StendhalQuestSystem.get().cacheQuest(this);
		}

		/**
		 * Registers quest to be added to world.
		 *
		 * @param func
		 *     Function to execute when {@link StendhalQuestSystem.loadQuest} is called.
		 */
		public void register(final LuaFunction func) {
			this.init = func;
			register();
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

			final LuaValue result = history.call(CoerceJavaToLua.coerce(player));
			if (result.istable()) {
				for (final LuaValue key: result.checktable().keys()) {
					if (key.isstring()) {
						ret.add(result.get(key.checkint()).tojstring());
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
			final LuaValue result = history.call(CoerceJavaToLua.coerce(player));
			if (result.istable()) {
				for (final LuaValue key: result.checktable().keys()) {
					if (key.isstring()) {
						ret.add(result.get(key.checkint()).tojstring());
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
			if (init != null) {
				init.invoke(); // or should this be init.call()?
			} else {
				logger.warn("LuaQuest.init not set. Quest will not work.");
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
		 * @param func
		 * 		Function to invoke when addToWorld() is called.
		 *
		 * @deprecated
		 *     Set LuaQuest.init directly.
		 */
		@Deprecated
		public void setAddFunction(final LuaFunction func) {
			logger.debug("LuaQuest.setAddFunction deprecated. Set LuaQuest.init directly.");

			this.init = func;
		}

		/**
		 * Sets the function for removing the quest from the game.
		 *
		 * @param func
		 * 		Function to invoke when removeFromWorld() is called.
		 * @deprecated
		 *     Set LuaQuest.remove directly.
		 */
		@Deprecated
		public void setRemoveFunction(final LuaFunction func) {
			logger.debug("LuaQuest.setRemoveFunction deprected. Set LuaQuest.remove directly.");

			this.remove = func;
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
