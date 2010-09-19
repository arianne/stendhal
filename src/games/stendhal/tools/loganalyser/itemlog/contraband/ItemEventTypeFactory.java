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
package games.stendhal.tools.loganalyser.itemlog.contraband;

public class ItemEventTypeFactory {

	public static ItemEventType create() {
		return null;
	}

	public static ItemEventType create(String event) {
		if (event.equals("register")) {
			return new RegisterItemEventType();
		} else if (event.equals("merge in")) {
			return new MergedInItemEventType();
		} else if (event.equals("splitted out")) {
			return new SplittedOutItemEventType();
		}
		return new DoNothingItemEventType();
	}
}
