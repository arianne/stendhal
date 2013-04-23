/* $Id$ */
/***************************************************************************
 *					(C) Copyright 2003-2010 - Marauroa					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.client;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * logs uncaught exceptions to the logging system before
 * the exception is propagated to the Java VM which will
 * kill the thread. The Java VM only logs to stderr so
 * there would be no information in the logfile without
 * this class.
 */
class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static Logger logger = Log4J.getLogger(LogUncaughtExceptionHandler.class);
	private Thread.UncaughtExceptionHandler next;

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		logger.error("Exception in thread " + thread.getName(), exception);
		System.err.println("Exception in thread " + thread.getName());
		exception.printStackTrace();
		if (next != null) {
			next.uncaughtException(thread, exception);
		}
	}

	/**
	 * installs this uncaught exception handler
	 */
	static void setup() {
		LogUncaughtExceptionHandler handler = new LogUncaughtExceptionHandler();
		handler.next = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

}
