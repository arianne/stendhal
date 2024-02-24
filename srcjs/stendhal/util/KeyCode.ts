/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Keyboard key codes.
 */
export class KeyCode {

	public static readonly TAB = 9;
	public static readonly ENTER = 13;
	public static readonly ARROW_LEFT = 37;
	public static readonly ARROW_UP = 38;
	public static readonly ARROW_RIGHT = 39;
	public static readonly ARROW_DOWN = 40;

	// for direction control with asdw keys
	public static readonly CHAR_A = 65; // left
	public static readonly CHAR_W = 87; // up
	public static readonly CHAR_D = 68; // right
	public static readonly CHAR_S = 83; // down

	private constructor() {}


	/**
	 * Extracts key code from keyboard event.
	 *
	 * @param e
	 *   Keyboard event.
	 * @return
	 *   Integer representation of key.
	 */
	public static extract(e: KeyboardEvent): number {
		// NOTE: MDM states that both UIEvent.which & KeyboardEvent.keyCode are deprecated
		if (e.which) {
			return e.which;
		}
		return e.keyCode;
	}
}
