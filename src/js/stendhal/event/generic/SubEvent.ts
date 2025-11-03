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
 * Sub-event for generic events.
 */
export abstract class SubEvent {

	/**
	 * Creates a new event.
	 *
	 * @param flags {string[]}
	 *   List of enabled flags.
	 */
	constructor(private readonly flags: string[]) {}

	/**
	 * Called when the event occurs.
	 *
	 * @param entity {any}
	 *   Entity associated with event.
	 */
	abstract execute(entity: any): void;

	/**
	 * Checks if a flag has been specified.
	 *
	 * @param flag {string}
	 *   Name of flag checking for.
	 * @return {boolean}
	 *   `true` if `flag` found in list of enabled flags.
	 */
	protected flagEnabled(flag: string): boolean {
		for (const f of this.flags) {
			if (f === flag) {
				return true;
			}
		}
		return false;
	}
}
