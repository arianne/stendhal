/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.List;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.mapstuff.sound.BackgroundMusicSource;
import games.stendhal.server.entity.player.Player;


public class LuaWorldHelper extends ScriptingSandbox {

	/** Singleton instance. */
	private static LuaWorldHelper instance;


	/**
	 * Retrieves singleton instance.
	 */
	public static LuaWorldHelper get() {
		if (instance == null) {
			instance = new LuaWorldHelper();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaWorldHelper() {
		super("");
		// super contstructor needs a string
	}

	@Override
	public boolean load(final Player player, final List<String> args) {
		// do nothing, this is not a functional script
		return true;
	}

	/**
	 * Sets the background music for the current zone.
	 *
	 * @param filename
	 *   File basename excluding .ogg extension.
	 * @param args
	 *   Lua table of key=value integer values. Valid keys are `volume`, `x`, `y`, & `radius`.
	 */
	public void setMusic(final String filename, final LuaTable args) {
		// default values
		int volume = 100;
		int x = 1;
		int y = 1;
		int radius = 10000;

		for (final LuaValue lkey: args.keys()) {
			final String key = lkey.tojstring();
			final LuaInteger lvalue = (LuaInteger) args.get(lkey);

			if (!lvalue.isnil()) {
				if (key.equals("volume")) {
					volume = lvalue.toint();
				} else if (key.equals("x")) {
					x = lvalue.toint();
				} else if (key.equals("y")) {
					y = lvalue.toint();
				} else if (key.equals("radius")) {
					radius = lvalue.toint();
				} else {
					LuaLogger.get().warn("Unknown table key in game:setMusic: " + key);
				}
			}
		}

		final BackgroundMusicSource musicSource = new BackgroundMusicSource(filename, radius, volume);
		musicSource.setPosition(x, y);
		add(musicSource);
	}

	/**
	 * Sets the background music for the current zone.
	 *
	 * @param filename
	 *   File basename excluding .ogg extension.
	 */
	public void setMusic(final String filename) {
		setMusic(filename, new LuaTable());
	}

	/**
	 * Executes a function after a specified number of turns.
	 *
	 * @param turns
	 *   Number of turns to wait.
	 * @param func
	 *   The function to be executed.
	 * @todo
	 *   FIXME: how to invoke with parameters?
	 */
	public void runAfter(final int turns, final LuaFunction func) {
		SingletonRepository.getTurnNotifier().notifyInTurns(turns, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				func.call();
			}
		});
	}

	/**
	 * Creates a new game event.
	 *
	 * @param source
	 *   Source of the event, usually a character.
	 * @param event
	 *   Name of event.
	 * @param params
	 *   List of event parameters.
	 * @return
	 *   New `games.stendhal.server.core.engine.GameEvent` instance.
	 */
	public GameEvent createEvent(final String source, final String event, final String... params) {
		return new GameEvent(source, event, params);
	}

	/**
	 * Creates a new game event.
	 *
	 * @param source
	 *   Source of the event, usually a character.
	 * @param event
	 *   Name of event.
	 * @param params
	 *   List of event parameters.
	 * @return
	 *   New `games.stendhal.server.core.engine.GameEvent` instance.
	 */
	public GameEvent createEvent(final String source, final String event, final LuaTable params) {
		return createEvent(source, event, (String[]) LuaArrayHelper.get().fromTable(params));
	}

	/**
	 * Executes a new game event.
	 *
	 * @param source
	 *   Source of the event, usually a character.
	 * @param event
	 *   Name of event.
	 * @param params
	 *   List of event parameters.
	 * @see
	 *   `games.stendhal.server.core.engine.GameEvent`
	 */
	public void raiseEvent(final String source, final String event, final String... params) {
		createEvent(source, event, params).raise();
	}

	/**
	 * Executes a new game event.
	 *
	 * @param source
	 *   Source of the event, usually a character.
	 * @param event
	 *   Name of event.
	 * @param params
	 *   List of event parameters.
	 * @see
	 *   `games.stendhal.server.core.engine.GameEvent`
	 */
	public void raiseEvent(final String source, final String event, final LuaTable params) {
		createEvent(source, event, params).raise();
	}
}
