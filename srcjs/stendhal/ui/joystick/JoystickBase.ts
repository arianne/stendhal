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

	// measured from bottom left corner
	protected readonly centerX = 120;
	protected readonly centerY = 120;


	public draw(ctx: CanvasRenderingContext2D) {
		// do nothing
	}

	protected getResource(res: string) {
		return "data/gui/joystick/" + res + ".png";
	}
}
