/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Log4J appender which logs to the game console.
 *
 * @author Matthias Totz
 */
public class GameConsoleAppender extends AppenderSkeleton {

	@Override
	protected void append(final LoggingEvent loggingEvent) {
		final StringBuilder buf = new StringBuilder();
		buf.append(getLayout().format(loggingEvent));
		final ThrowableInformation ti = loggingEvent.getThrowableInformation();

		if (ti != null) {
			final String[] cause = ti.getThrowableStrRep();

			for (final String line : cause) {
				buf.append(line).append('\n');
			}
		}

		j2DClient.get().addEventLine(new HeaderLessEventLine(buf.toString(), NotificationType.CLIENT));
	}

	@Override
	public void close() {
		// implementation of abstract method
		// yet nothing do to
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

}
