/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Joystick implementation that does nothing. Used when joystick should not be drawn on-screen.
 */
export class JoystickBase {

	public onRemoved() {
		// do nothing
	}

	protected getResource(res: string) {
		return "data/gui/joystick/" + res + ".png";
	}

	protected getCenterX(): number {
		// FIXME: may be better to use position relative to viewport after loaded
		/*
		const gamewindow = document.getElementById("gamewindow")!;
		const rect = gamewindow.getBoundingClientRect();
		return rect.left + 32;
		*/
		return 224;
	}

	protected getCenterY(): number {
		// FIXME: may be better to use position relative to viewport after loaded
		/*
		const gamewindow = document.getElementById("gamewindow")!;
		const rect = gamewindow.getBoundingClientRect();
		return rect.top + 32;
		*/
		return 384;
	}
}
