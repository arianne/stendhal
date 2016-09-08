/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.textformat;

/**
 * Tally marks representation of an integer number. The number is split into
 * groups of fives. For example, 12 is transformed into 552 (12 = 5+5+2).
 */
public class TallyMarks {

	private final int value;

	public TallyMarks(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (value < 0) {
			return "0";
		}
		int quotient = value / 5;
		int remainder = value % 5;
		StringBuilder digits = new StringBuilder();
		for (int i = 0; i < quotient; i++) {
			digits.append("5");
		}
		if ((quotient > 0 && remainder > 0) || quotient == 0) {
			digits.append(String.valueOf(remainder));
		}
		return digits.toString();
	}
}
