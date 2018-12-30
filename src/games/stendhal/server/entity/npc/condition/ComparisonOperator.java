/***************************************************************************
 *                   (C) Copyright 2018 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

/**
 * operators for comparing values
 *
 * @author hendrik
 */
public enum ComparisonOperator {

	EQUALS {
		@Override
		public boolean compare(int value1, int value2) {
			return value1 == value2;
		}
	},
	LESS_THAN {
		@Override
		public boolean compare(int value1, int value2) {
			return value1 < value2;
		}
	},
	GREATER_THAN {
		@Override
		public boolean compare(int value1, int value2) {
			return value1 > value2;
		}
	},
	LESS_OR_EQUALS {
		@Override
		public boolean compare(int value1, int value2) {
			return value1 <= value2;
		}
	},
	GREATER_OR_EQUALS {
		@Override
		public boolean compare(int value1, int value2) {
			return value1 >= value2;
		}
	};

	public abstract boolean compare(int value1, int value2);
}
