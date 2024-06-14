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

import { Pair } from "../../util/Pair";


/**
 * Represents a single or dual state element tooltip text.
 */
export class Tooltip extends Pair<string, string|undefined> {

	/** Element for which text is displayed. */
	private readonly element: HTMLElement;


	/**
	 * Creates a new pair of tooltips.
	 *
	 * @param {HTMLElement} element
	 *   Element for which text is displayed.
	 * @param {string} first
	 *   Text when in primary state.
	 * @param {string=} second
	 *   Text when in secondary state.
	 * @param {boolean} [primary=true]
	 *   Initial state. `true` for primary, `false` for secondary. Does nothing if `secondary` is not
	 *   defined.
	 */
	constructor(element: HTMLElement, first: string, second?: string, primary=true) {
		super(first, second);
		this.element = element;
		this.setState(primary);
	}

	/**
	 * Checks if secondary state text is set.
	 *
	 * @returns {boolean}
	 *   `true` for dual state functionality, `false` for single.
	 */
	isDualState(): boolean {
		return typeof(this.second) !== "undefined";
	}

	/**
	 * Updates element text for state.
	 *
	 * @param {boolean} primary
	 *   New state. `true` for primary, `false` for secondary. Always uses primary if only has single
	 *   state functionality.
	 */
	setState(primary: boolean) {
		if (primary || !this.isDualState()) {
			this.element.title = this.first;
		} else {
			this.element.title = this.second as string;
		}
	}

	/**
	 * Updates element text to inverse state.
	 */
	toggle() {
		this.setState(this.element.title !== this.first);
	}
}
