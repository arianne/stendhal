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
	 *   Script with ID to be used.
	 */
	public void setScript(final LuaScript script) {
		if (script == null) {
			chunkname = null;
		} else {
			chunkname = script.getChunkName();
		}
	}

	/**
	 * Formats a message for logging.
	 *
	 * @param message
	 *   Text in logged message.
	 * @return
	 *   Formatted message text with source.
	 */
	private String formatMessage(String message) {
		message = message.trim();
		if (chunkname == null) {
			message = "(unknown source) " + message;
		} else {
			message = "(" + chunkname + ") " + message;
		}
		return message;
	}

	/**
	 * Logs a message at info level.
	 *
	 * @param message
	 *   Text in logged message.
	 */
	public void info(final String message) {
		logger.info(formatMessage(message));
	}

	/**
	 * Logs a message at warning level.
	 *
	 * @param message
	 *   Text in logged message.
	 */
	public void warn(final String message) {
		logger.warn(formatMessage(message));
	}

	/**
	 * Logs a message at error level.
	 *
	 * @param message
	 *   Text in logged message.
	 */
	public void error(final String message) {
		logger.error(formatMessage(message));
	}

	/**
	 * Logs a message at error level & raises an exception.
	 *
	 * @param message
	 *   Text in logged message.
	 * @param throwable
	 *   Exception to raise.
	 */
	public void error(final String message, final Throwable throwable) {
		logger.error(formatMessage(message), throwable);
	}

	/**
	 * Logs a message at error level & raises an exception.
	 *
	 * @param throwable
	 *   Exception to raise.
	 */
	public void error(final Throwable throwable) {
		logger.error(formatMessage(throwable.getMessage()), throwable);
	}

	/**
	 * Logs a message at debug level.
	 *
	 * @param message
	 *   Text in logged message.
	 */
	public void debug(final String message) {
		logger.debug(formatMessage(message));
	}

	/**
	 * Logs a deprecation warning.
	 *
	 * @param old
	 *   The deprecated item.
	 * @param alt
	 *   Alternative to use.
	 */
	public void deprecated(final String old, final String alt) {
		String msg = "'" + old + "' is deprecated";
		if (alt != null) {
			msg += ", use '" + alt + "' instead";
		}
		warn(msg);
	}

	/**
	 * Logs a deprecation warning.
	 *
	 * @param old
	 *   The deprecated item.
	 */
	public void deprecated(final String old) {
		deprecated(old, null);
	}
}
