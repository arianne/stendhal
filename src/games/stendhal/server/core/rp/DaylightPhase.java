/***************************************************************************
 *                   (C) Copyright 2011-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp;

import java.util.Calendar;

/**
 * day light phase
 *
 * @author hendrik
 */
public enum DaylightPhase {

	/** during the night */
	NIGHT (0x47408c, "night"),

	/** early morning before sunrise at */
	DAWN (0x774590, "night"),

	/** the sun is rising */
	SUNRISE (0xc0a080, "sunrise"),

	/** during the day */
	DAY ("day"),

	/** the sun is setting */
	SUNSET (0xc0a080, "sunset"),

	/** early night */
	DUSK (0x774590, "night");

	private Integer color;
	private String greetingName;

	// this should only be set for testing purposes
	private static DaylightPhase testing_phase;

	private DaylightPhase(int color, String greetingName) {
		this.color = Integer.valueOf(color);
		this.greetingName = greetingName;
	}

	private DaylightPhase(String greetingName) {
		this.color = null;
		this.greetingName = greetingName;
	}

	/**
	 * gets the current daylight phase
	 *
	 * @return DaylightPhase
	 */
	public static DaylightPhase current() {
		if (testing_phase != null) {
			return testing_phase;
		}

		Calendar cal = Calendar.getInstance();

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		// anything but precise, but who cares
		int diffToMidnight = Math.min(hour, 24 - hour);
		if (diffToMidnight > 3) {
			return DAY;
		} else if (diffToMidnight == 3) {
			if (hour < 12) {
				return SUNRISE;
			} else {
				return SUNSET;
			}
		} else if (diffToMidnight == 2) {
			if (hour < 12) {
				return DAWN;
			} else {
				return DUSK;
			}
		} else {
			return NIGHT;
		}
	}

	/**
	 * gets the color of this daylight phase
	 *
	 * @return color
	 */
	public Integer getColor() {
		return color;
	}

	/**
	 * Gets the greeting name
	 *
	 * @return greeting name
	 */
	public String getGreetingName() {
		return greetingName;
	}

	/**
	 * WARNING: this should only be used for testing purposes.
	 *
	 * @param phase
	 * 		<code>DaylightPhase</code> to set for testing.
	 */
	public static void setTestingPhase(final DaylightPhase phase) {
		testing_phase = phase;
	}

	/**
	 * Disables testing phase.
	 */
	public static void unsetTestingPhase() {
		testing_phase = null;
	}
}
