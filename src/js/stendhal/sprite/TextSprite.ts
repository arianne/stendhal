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

import { RenderingContext2D } from "util/Types";

export class TextSprite {
	private textMetrics?: TextMetrics;

	constructor(
		public readonly text: string,
		public readonly color: string,
		public readonly font: string) {}

	/*
	 * Draws text in specified color with black outline. Setting the font is the
	 * caller's responsibility.
	 *
	 * @param ctx graphics context
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public draw(ctx: RenderingContext2D, x: number, y: number) {
		ctx.font = this.font;
		ctx.lineWidth = 2;
		ctx.strokeStyle = "black";
		ctx.fillStyle = this.color;
		ctx.lineJoin = "round";
		ctx.strokeText(this.text, x, y);
		ctx.fillText(this.text, x, y);
	}

	getTextMetrics(ctx: RenderingContext2D) {
		if (!this.textMetrics) {
			ctx.font = this.font;
			this.textMetrics = ctx.measureText(this.text);
		}
		return this.textMetrics;
	}
}
