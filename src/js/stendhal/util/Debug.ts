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


export class Debug {

	/** Properties marked for debugging. */
	private static readonly properties: Record<string, boolean> = {};


	/**
	 * Static class.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Checks if a property is marked for debugging.
	 *
	 * @param {string} prop
	 *   Property name.
	 * @returns {boolean}
	 *   `true` if `prop` is contained in active list.
	 */
	static isActive(prop: string): boolean {
		return Debug.properties[prop] === true;
	}

	/**
	 * Marks or unmarks a property for debugging.
	 *
	 * @param {string} prop
	 *   Property name.
	 * @param {boolean} active
	 *   `true` to mark as active, `false` for inactive.
	 */
	static setActive(prop: string, active: boolean) {
		Debug.properties[prop] = active;
	}

	/**
	 * Toggles marking property for debugging.
	 *
	 * @param {string} prop
	 *   Property name.
	 * @returns {boolean}
	 *   Property debugging state (`true` if active).
	 */
	static toggle(prop: string): boolean {
		Debug.setActive(prop, !Debug.isActive(prop));
		return Debug.isActive(prop);
	}
}
