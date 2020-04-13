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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.player.Player;


/**
 * Exposes ChatAction classes to Lua.
 */
public class LuaActionHelper {

	private static LuaLogger logger = LuaLogger.get();

	private static LuaActionHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static ActionHelper instance.
	 */
	public static LuaActionHelper get() {
		if (instance == null) {
			instance = new LuaActionHelper();
		}

		return instance;
	}

	/**
	 * Creates a custom ChatAction.
	 *
	 * @param lf
	 * 		Function to be invoked when ChatAction.fire() is called.
	 * @return
	 * 		New ChatAction instance.
	 */
	public ChatAction create(final LuaFunction lf) {
		return new ChatAction() {

			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final LuaValue luaPlayer = CoerceJavaToLua.coerce(player);
				final LuaValue luaSentence = CoerceJavaToLua.coerce(sentence);
				final LuaValue luaNPC = CoerceJavaToLua.coerce(npc);

				final LuaValue[] all = {luaPlayer, luaSentence, luaNPC};

				lf.invoke(all);
			}
		};
	}

	/**
	 * Creates an instance of a ChatAction from the class name string.
	 *
	 * @param className
	 * 		Class basename.
	 * @param args
	 * 		Lua table of objects that should be passed to the constructor.
	 * @return
	 * 		New <code>ChatAction</code> instance or <code>null</code>.
	 */
	public ChatAction create(String className, final LuaTable args) {
		className = "games.stendhal.server.entity.npc.action." + className;
		Object[] objects = null;
		if (args != null && !args.isnil()) {
			objects = LuaArrayHelper.get().toArray(args);
		}

		final boolean noArgs = objects == null || objects.length == 0;

		try {
			if (noArgs) {
				try {
					return (ChatAction) Class.forName(className).newInstance();
				} catch (final InstantiationException e2) {
					// do nothing
				}
			} else {
				final Constructor<?>[] constructors = Class.forName(className).getConstructors();
				for (final Constructor<?> con: constructors) {
					try {
						return (ChatAction) con.newInstance(objects);
					} catch (final InvocationTargetException e2) {
						// do nothing
					} catch (final InstantiationException e2) {
						// do nothing
					} catch (final IllegalArgumentException e2) {
						// do nothing
					}
				}
			}
		} catch (final ClassNotFoundException e1) {
			logger.error(e1, e1);
		} catch (final IllegalAccessException e1) {
			logger.error(e1, e1);
		}


		// FIXME: should we thrown an exception here?

		if (noArgs) {
			logger.error("No default constructor for " + className);
		} else if (objects != null) {
			logger.error("No constructor for " + className + " found for args: " + Arrays.toString(objects));
		} else {
			logger.error("Unknown instantiation error for " + className); // should not happen
		}

		return null;
	}

	/**
	 * Helper method for creating a MultipleActions instance.
	 *
	 * @param actionList
	 * 		LuaTable containing list of ChatAction instances.
	 * @return
	 * 		New MultipleActions instance.
	 */
	public MultipleActions multiple(final LuaTable actionList) {
		final List<ChatAction> actions = new LinkedList<>();
		for (final LuaValue idx: actionList.keys()) {
			final LuaValue value = actionList.get(idx);
			if (value.istable()) {
				actions.add(multiple(value.checktable()));
			} else if (value.isuserdata(ChatAction.class)) {
				actions.add((ChatAction) value.touserdata(ChatAction.class));
			} else {
				logger.warn("Invalid data type. Must be ChatAction or LuaTable.");
			}
		}

		return new MultipleActions(actions.toArray(new ChatAction[] {}));
	}

	/**
	 * Removes quest slot from player.
	 *
	 * This is needed because it's impossible to pass <code>nil</code> values in
	 * a LuaTable.
	 *
	 * @param questSlot
	 * 		Quest string identifier.
	 * @return
	 * 		New SetQuestAction that sets quest state to <code>null</code>.
	 */
	public SetQuestAction clearQuest(final String questSlot) {
		return new SetQuestAction(questSlot, null);
	}
}
