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

import java.util.Calendar;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.Actions;
import games.stendhal.common.grammar.Grammar;

/**
 * A map object that when looked at shows the server time.
 */
public class Clock extends Sign {
	/** Maximum seconds the clock can be wrong to either direction */
	private static final int MAX_IMPRECISION = 300;

	/**
	 * The amount of seconds this clock is wrong.
	 * [ -MAX_IMPRECISION, MAX_IMPRECISION - 1 ]
	 */
	private final int imprecisionSeconds;

	/**
	 * Create a new clock.
	 */
	public Clock() {
		put(Actions.ACTION, Actions.LOOK);
		put("class", "transparent");
		imprecisionSeconds = Rand.rand(2 * MAX_IMPRECISION) - MAX_IMPRECISION;
	}

	@Override
	public String describe() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, imprecisionSeconds);

		// Add 2.5 minutes so that the rounding is what humans expect
		cal.add(Calendar.SECOND, 150);

		int min = cal.get(Calendar.MINUTE);
		// Round down to nearest multiple of 5
		min = (min / 5) * 5;
		int hour = cal.get(Calendar.HOUR);
		if (min > 30) {
			// For getting the hour right for the "x to y" versions
			hour = (hour + 1) % 12;
		}
		if (hour == 0) {
			hour = 12;
		}

		StringBuilder msg = new StringBuilder("The time is ");
		msg.append(describeMinute(min));
		msg.append(Grammar.numberString(hour));
		msg.append('.');

		return msg.toString();
	}

	/**
	 * Textual description of the minute part.
	 *
	 * @param m minute
	 * @return description of the minute. Empty string if it's even.
	 */
	private String describeMinute(int m) {
		switch (m) {
		case 5: return "five past ";
		case 10: return "ten past ";
		case 15: return "quarter past ";
		case 20: return "twenty past ";
		case 25: return "twenty five past ";
		case 30: return "half past ";
		case 35: return "twenty five to ";
		case 40: return "twenty to ";
		case 45: return "quarter to ";
		case 50: return "ten to ";
		case 55: return "five to ";
		default: return "";
		}
	}
}
