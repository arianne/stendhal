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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Exposes ChatCondition classes to Lua.
 */
public class LuaConditionHelper {

	private static LuaLogger logger = LuaLogger.get();

	/** The singleton instance. */
	private static LuaConditionHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static ConditionHelper instance.
	 */
	public static LuaConditionHelper get() {
		if (instance == null) {
			instance = new LuaConditionHelper();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaConditionHelper() {
		// singleton
	}

	/**
	 * Creates a custom ChatCondition.
	 *
	 * @param lf
	 * 		LuaFunction to be invoked when ChatCondition.fire() is called.
	 * @return
	 * 		New ChatCondition.
	 */
	public ChatCondition create(final LuaFunction lf) {
		return new ChatCondition() {

			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				final LuaValue luaPlayer = CoerceJavaToLua.coerce(player);
				final LuaValue luaSentence = CoerceJavaToLua.coerce(sentence);
				final LuaValue luaNPC = CoerceJavaToLua.coerce(npc);

				final LuaValue result = lf.call(luaPlayer, luaSentence, luaNPC);

				if (!result.isboolean()) {
					logger.warn("Lua function did not return boolean value");
					return false;
				}

				return result.toboolean();
			}
		};
	}

	/**
	 * Creates an instance of a ChatCondition from the class name string.
	 *
	 * @param className
	 * 		Class basename.
	 * @param args
	 * 		Lua table of objects that should be passed to the constructor.
	 * @return
	 * 		New <code>ChatCondition</code> instance or <code>null</code>.
	 */
	public ChatCondition create(String className, final LuaTable args) {
		className = "games.stendhal.server.entity.npc.condition." + className;
		Object[] objects = null;
		if (args != null && !args.isnil()) {
			objects = LuaArrayHelper.get().toArray(args);
		}

		final boolean noArgs = objects == null || objects.length == 0;
		Exception exc = new Exception("Unknown exception");

		try {
			if (noArgs) {
				try {
					return (ChatCondition) Class.forName(className).getDeclaredConstructor().newInstance();
				} catch (final InvocationTargetException e2) {
					exc = e2;
				} catch (final InstantiationException e2) {
					exc = e2;
				} catch (final NoSuchMethodException e2) {
					exc = e2;
				}
			} else {
				final Constructor<?>[] constructors = Class.forName(className).getConstructors();
				for (final Constructor<?> con: constructors) {
					try {
						if (con.isVarArgs()) {
							// FIXME: not working
							return (ChatCondition) con.newInstance((Object) objects);
						} else {
							return (ChatCondition) con.newInstance(objects);
						}
					} catch (final InvocationTargetException e2) {
						exc = e2;
					} catch (final InstantiationException e2) {
						exc = e2;
					} catch (final IllegalArgumentException e2) {
						exc = e2;
					}
				}
			}
		} catch (final ClassNotFoundException e1) {
			exc = e1;
		} catch (final IllegalAccessException e1) {
			exc = e1;
		}


		if (noArgs) {
			logger.error("No default constructor for " + className, exc);
		} else if (objects != null) {
			logger.error("No constructor for " + className + " found for args: "
				+ Arrays.toString(objects), exc);
		} else {
			logger.error("Unknown instantiation error for " + className, exc); // should not happen
		}

		return null;
	}

	/**
	 * Creates a NotCondition instance.
	 *
	 * @param condition
	 * 		Condition to be checked.
	 * @return
	 * 		New NotCondition instance.
	 */
	public NotCondition notC(final ChatCondition condition) {
		return new NotCondition(condition);
	}

	/**
	 * Creates a NotCondition instance.
	 *
	 * @param condition
	 * 		Condition to be checked.
	 * @return
	 * 		New NotCondition instance.
	 * @deprecated
	 *     Use {@link LuaConditionHelper.notC}.
	 */
	@Deprecated
	public NotCondition notCondition(final ChatCondition condition) {
		logger.debug("LuaConditionHelper.notCondition deprecated. Use LuaConditionHelper.notC.");

		return this.notC(condition);
	}

	/**
	 * Helper method for creating a NotCondition instance.
	 *
	 * @param lv
	 * 		Condition to be checked inside a LuaValue instance, a list of
	 * 		conditions inside a LuaTable, or a LuaFunction that returns a
	 * 		boolean value.
	 * @return
	 * 		New NotCondition instance.
	 */
	public NotCondition notC(final LuaValue lv) {
		if (lv.istable()) {
			return notC(andCondition(lv.checktable()));
		} else if (lv.isfunction()) {
			return new NotCondition(create((LuaFunction) lv));
		}

		return this.notC((ChatCondition) lv.touserdata(ChatCondition.class));
	}

	/**
	 * Helper method for creating a NotCondition instance.
	 *
	 * @param lv
	 * 		Condition to be checked inside a LuaValue instance, a list of
	 * 		conditions inside a LuaTable, or a LuaFunction that returns a
	 * 		boolean value.
	 * @return
	 * 		New NotCondition instance.
	 * @deprecated
	 *     Use {@link LuaConditionHelper.notC}.
	 */
	@Deprecated
	public NotCondition notCondition(final LuaValue lv) {
		logger.debug("LuaConditionHelper.notCondition deprecated. Use LuaConditionHelper.notC.");

		return this.notC(lv);
	}

	/**
	 * Helper method to create an AndCondition instance.
	 *
	 * @param conditionList
	 * 		LuaTable containing a list of ChatCondition instances.
	 * @return
	 * 		New AndCondition instance.
	 */
	public AndCondition andC(final LuaTable conditionList) {
		final List<ChatCondition> conditions = new LinkedList<>();
		for (final LuaValue idx: conditionList.keys()) {
			final LuaValue value = conditionList.get(idx);
			if (value.istable()) {
				conditions.add(andCondition(value.checktable()));
			} else if (value.isuserdata(ChatCondition.class)) {
				conditions.add((ChatCondition) value.touserdata(ChatCondition.class));
			} else {
				logger.warn("Invalid data type. Must be ChatCondition.");
			}
		}

		return new AndCondition(conditions.toArray(new ChatCondition[] {}));
	}

	/**
	 * Helper method to create an AndCondition instance.
	 *
	 * @param conditionList
	 * 		LuaTable containing a list of ChatCondition instances.
	 * @return
	 * 		New AndCondition instance.
	 * @deprecated
	 *     Use {@link LuaConditionHelper.andC}.
	 */
	@Deprecated
	public AndCondition andCondition(final LuaTable conditionList) {
		logger.debug("LuaConditionHelper.andCondition deprecated. Use LuaConditionHelper.andC.");

		return this.andC(conditionList);
	}

	/**
	 * Helper method to create an OrCondition instance.
	 *
	 * @param conditionList
	 *     LuaTable containing a list of conditions.
	 * @return
	 *     New OrCondition instance or <code>null</code> if failed.
	 */
	public OrCondition orC(final LuaTable conditionList) {
		final List<ChatCondition> conditions = new LinkedList<>();

		for (int idx=1; idx <= conditionList.length(); idx++) {
			final LuaValue c = conditionList.get(idx);
			if (c.isuserdata()) {
				// parameter is ChatCondition instance
				conditions.add((ChatCondition) c.checkuserdata());
			} else if (c.isfunction()) {
				// parameter is LuaFunction instance
				conditions.add(create(c.checkfunction()));
			} else {
				// parameter is LuaTable instance
				if (c.get(1).isstring()) {
					// table with single condition
					conditions.add(create(c.get(1).checkjstring(), c.get(2).checktable()));
				} else {
					// table with multiple conditions
					conditions.add(andC(c.checktable()));
				}
			}
		}

		final int csize = conditions.size();
		if (csize > 0) {
			return new OrCondition(Arrays.copyOf(conditions.toArray(), csize, ChatCondition[].class));
		}

		logger.warn("failed to created OrCondition");
		return null;
	}
}
