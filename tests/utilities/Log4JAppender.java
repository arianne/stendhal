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
package utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class Log4JAppender extends AppenderSkeleton {

	public static final Log4JAppender INSTANCE = new Log4JAppender();

	private static List<String> messages = new ArrayList<String>();


	@Override
	protected void append(final LoggingEvent event) {
		messages.add(event.getRenderedMessage());
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	public static String[] getMessages() {
		return  messages.toArray(new String[messages.size()]);
	}

	public static void clear() {
		messages.clear();
	}

}
