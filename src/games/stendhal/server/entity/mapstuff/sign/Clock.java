/***************************************************************************
 *                    (C) Copyright 2003-2012 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.sign;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import games.stendhal.common.constants.Actions;

/**
 * A map object that when looked at shows the server time.
 */
public class Clock extends Sign {
	private final SimpleDateFormat format = new SimpleDateFormat("h:mm");
	
	/**
	 * Create a new clock.
	 */
	public Clock() {
		put(Actions.ACTION, Actions.LOOK);
		put("class", "transparent");
	}
	
	@Override
	public String describe() {
		Calendar cal = Calendar.getInstance();
		int min = cal.get(Calendar.MINUTE);
		// Some fuzziness for the right atmosphere
		min = (min / 5) * 5;
		cal.set(Calendar.MINUTE, min);
		StringBuilder msg = new StringBuilder("The time is ");
		msg.append(format.format(cal.getTime()));
		msg.append(".");
		
		return msg.toString();
	}
}
