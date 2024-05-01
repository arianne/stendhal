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

import { Enumeration } from "./Enumeration";


/**
 * Represents a numeric enumeration type.
 */
export class NumericEnumeration extends Enumeration<number> {

	/** Tracks value of most recent instance created. */
	private static currentValue = -1;

	/** Numeric value representation of this instance. */
	override readonly value: number;


	/**
	 * Creates a new numeric enumeration.
	 *
	 * @param value {number}
	 *   Numeric value representation of this instance. If `undefined`, increments value from most
	 *   recent instance created (default: `undefined`).
	 */
	constructor(value?: number) {
		super(value);
		if (typeof(value) === "undefined") {
			NumericEnumeration.currentValue++;
			value = NumericEnumeration.currentValue;
		} else {
			NumericEnumeration.currentValue = value;
		}
		this.value = value;
	}
}
