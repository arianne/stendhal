/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Represents an enumaration type.
 */
export class Enumeration<T> {

	/**
	 * Creates a new enumeration instance.
	 *
	 * @param value {T}
	 *   Value representation of this instance.
	 */
	constructor(readonly value?: T) {}

	/**
	 * Checks for equality between two objects.
	 *
	 * @param obj {any}
	 *   The other object to compare against this instance.
	 * @return {boolean}
	 *   `true` if `obj` is instance of same type & their values are the same.
	 */
	equals(obj: any): boolean {
		if (!(obj instanceof Enumeration)) {
			return false;
		}
		return this.value === obj.value;
	}
}
