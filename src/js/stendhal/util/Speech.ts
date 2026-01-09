/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "./Types";


export class Speech {
	// space to be left at the beginning and end of line in pixels
	//private margin_width = 3;
	// the diameter of the arc of the rounded bubble corners
	//private arc_diameter = 2 * this.margin_width + 2;

	/**
	 * Draws a rectangle.
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	static drawBubble(ctx: RenderingContext2D, x: number, y: number,
			width: number, height: number, tail: boolean=false) {
		ctx.strokeRect(x, y - 15, width, height);
		ctx.fillRect(x, y - 15, width, height);

		ctx.beginPath();
		ctx.moveTo(x, y);

		// tail
		if (tail) {
			ctx.lineTo(x - 5, y + 8);
			ctx.lineTo(x + 1, y + 5);
		}

		ctx.stroke();
		ctx.closePath();
		ctx.fill();
	}

	/**
	 * Draws a rectangle with rounded edges.
	 *
	 * Source: https://stackoverflow.com/a/3368118/4677917
	 *
	 * @param ctx
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	static drawBubbleRounded(ctx: RenderingContext2D, x: number,
			y: number, width: number, height: number) {
		//const arc = this.arc_diameter;
		const arc = 3;

		ctx.beginPath();
		ctx.moveTo(x + arc, y);
		ctx.lineTo(x + width - arc, y);
		ctx.quadraticCurveTo(x + width, y, x + width, y + arc);
		ctx.lineTo(x + width, y + height - arc);
		ctx.quadraticCurveTo(x + width, y + height, x + width - arc, y + height);
		ctx.lineTo(x + arc, y + height);
		ctx.quadraticCurveTo(x, y + height, x, y + height - arc);
		ctx.lineTo(x, y + 8);

		// tail
		ctx.lineTo(x - 8, y + 11);
		ctx.lineTo(x, y + 3);

		ctx.lineTo(x, y + arc);
		ctx.quadraticCurveTo(x, y, x + arc, y);
		ctx.stroke();
		ctx.closePath();
		ctx.fill();
	}
}
