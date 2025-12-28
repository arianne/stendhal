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

import { RenderingContext2D } from "util/Types";


export interface OverlaySpriteImpl {

	/**
	 * Instructions when sprite should be drawn on viewport.
	 *
	 * @param {RenderingContext2D} ctx
	 *   The viewport canvas drawing context.
	 * @param {number} x
	 *   Horizonal pixel position of where to draw on canvas.
	 * @param {number} y
	 *   Vertical pixel position of where to draw on canvas.
	 * @param {number} drawWidth
	 *   Width of each frame to draw.
	 * @param {number} drawHeight
	 *   Height of each frame to draw.
	 * @returns {boolean}
	 *   `true` to denote sprite is expired & should be removed.
	 */
	draw(ctx: RenderingContext2D, x: number, y: number, drawWidth: number, drawHeight: number)
			: boolean;

	/**
	 * Checks if sprite is expired.
	 *
	 * @returns {boolean}
	 *   `true` if sprite should be removed.
	 */
	expired(): boolean;
}
