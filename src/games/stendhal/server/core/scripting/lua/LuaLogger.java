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

	/** Filename of script currently being executed. */
	private String filename;
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

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	private String formatMessage(String message) {
		message = message.trim();
		if (filename == null) {
			message = "(unknown source) " + message;
		} else {
			message = "(" + filename + ") " + message;
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

	public void debug(final String message) {
		logger.debug(formatMessage(message));
	}
}
