/***************************************************************************
 *                    Copyright © 2019-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import org.apache.log4j.Logger;


/**
 * Handles logging from within Lua scripts.
 */
public class LuaLogger {

	private static final Logger logger = Logger.getLogger(LuaLogger.class);

	/** Identifier of script chunk currently being executed. */
	private String chunkname;
	/** Singleton instance. */
	private static LuaLogger instance;


	/**
	 * Retrieves the singleton instance.
	 */
	public static LuaLogger get() {
		if (instance == null) {
			instance = new LuaLogger();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LuaLogger() {
		// do nothing
	}

	/**
	 * Sets the identifier to be used in log messages.
	 *
	 * @param script
	 *     Script with ID to be used.
	 */
	public void setScript(final LuaScript script) {
		if (script == null) {
			chunkname = null;
		} else {
			chunkname = script.getChunkName();
		}
	}

	private String formatMessage(String message) {
		message = message.trim();
		if (chunkname == null) {
			message = "(unknown source) " + message;
		} else {
			message = "(" + chunkname + ") " + message;
		}
		return message;
	}

	public void info(final String message) {
		logger.info(formatMessage(message));
	}

	public void warn(final String message) {
		logger.warn(formatMessage(message));
	}

	public void error(final String message) {
		logger.error(formatMessage(message));
	}

	public void error(final String message, final Throwable throwable) {
		logger.error(formatMessage(message), throwable);
	}

	public void error(final Throwable throwable) {
		logger.error(formatMessage(throwable.getMessage()), throwable);
	}

	public void debug(final String message) {
		logger.debug(formatMessage(message));
	}
}
