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
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.scripting.ScriptInLua.LuaLogger;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.player.Player;


/**
 * Exposes ChatAction classes to Lua.
 */
public class ActionHelper {

	private static LuaLogger logger = LuaLogger.get();

	private static ActionHelper instance;


	/**
	 * Retrieves the static instance.
	 *
	 * @return
	 * 		Static ActionHelper instance.
	 */
	public static ActionHelper get() {
		if (instance == null) {
			instance = new ActionHelper();
		}

		return instance;
	}

	/**
	 * Creates a custom ChatAction.
	 *
	 * @param f
	 * 		Function to be invoked when ChatAction.fire() is called.
	 * @return
	 * 		New ChatAction instance.
	 */
	public ChatAction create(final LuaFunction f) {
		return new ChatAction() {

			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final LuaValue luaPlayer = CoerceJavaToLua.coerce(player);
				final LuaValue luaSentence = CoerceJavaToLua.coerce(sentence);
				final LuaValue luaNPC = CoerceJavaToLua.coerce(npc);

				final LuaValue[] all = {luaPlayer, luaSentence, luaNPC};

				f.invoke(all);
			}
		};
	}

	/**
	 * Creates an instance of a ChatAction from the class name string.
	 *
	 * FIXME:
	 *
	 * @param className
	 * 		Class basename.
	 * @param params
	 * 		Parameters that should be passed to the constructor.
	 * @return
	 * 		New <code>ChatAction</code> instance or <code>null</code>.
	 */
	/*
	public ChatAction newAction(String className, final Object... params) {
		className = "games.stendhal.server.entity.npc.action." + className;

		try {
			if (params.length == 0) {
				try {
					final Constructor<?> constructor = Class.forName(className).getConstructor();

					return (ChatAction) constructor.newInstance();
				} catch (InvocationTargetException e2) {
				}
			} else {
				final Constructor<?>[] constructors = Class.forName(className).getConstructors();

				for (final Constructor<?> con: constructors) {
					try {
						return (ChatAction) con.newInstance(new Object[] { params });
					} catch (InvocationTargetException e2) {
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			logger.error(e1, e1);
		} catch (InstantiationException e1) {
			logger.error(e1, e1);
		} catch (IllegalAccessException e1) {
			logger.error(e1, e1);
		} catch (IllegalArgumentException e1) {
			logger.error(e1, e1);
		} catch (NoSuchMethodException e1) {
			logger.error(e1, e1);
		} catch (SecurityException e1) {
			logger.error(e1, e1);
		}

		return null;
	}
	*/

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
}
