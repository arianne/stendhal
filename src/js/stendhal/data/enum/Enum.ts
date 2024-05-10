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

import { AbstractEnum } from "./AbstractEnum";


/**
 * Represents a string enumeration type.
 */
export class Enum extends AbstractEnum<string> {

	/** String enumeration type must have a value. */
	override readonly value!: string;


	/**
	 * Creates a new string enumeration.
	 *
	 * @param value {string}
	 *   String value representation of this instance.
	 */
	constructor(value: string) {
		super(value);
	}
}
