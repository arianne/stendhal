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


export class BackgroundPainter {

	private bg: HTMLImageElement;
	// each slice represents a portion of the image with the upper-left corner at offset x,y
	private slices: {x: number; y: number;}[][] = [];
	private tileW: number;
	private tileH: number;
	private warned = false;


	constructor(img: HTMLImageElement) {
		this.bg = img;
		this.tileW = this.bg.width / 3;
		this.tileH = this.bg.height / 3;

		for (let y = 0; y < this.bg.height; y += this.tileH) {
			const row = [];
			for (let x = 0; x < this.bg.width; x += this.tileW) {
				row.push({x: x, y: y});
			}
			this.slices.push(row);
		}
	}

	getTileWidth(): number {
		return this.tileW;
	}

	getTileHeight(): number {
		return this.tileH;
	}

	/**
	 * Draws the images on the canvas.
	 *
	 * @param x
	 *     Left position where to start drawing image.
	 * @param y
	 *     Top position where to start drawing image.
	 * @param width
	 *     Number of horizontal tiles to draw.
	 * @param height
	 *     Number of vertical tiles to draw.
	 */
	paint(ctx: RenderingContext2D, x: number, y: number,
			width: number, height: number) {
		// FIXME: if image parts were not cached, tile width & height may be 0
		if (!this.tileW || !this.tileH) {
			if (!this.warned) {
				console.warn("cannot draw background while tile width or height is 0");
				this.warned = true;
			}
			// try again
			this.tileW = this.bg.width / 3;
			this.tileH = this.bg.height / 3;
			return;
		}

		// TODO: add support for constructor(String image, int leftWidth, int centerWidth, int topHeight, int centerHeight)

		let drawY = 0, ir = 0;
		while (drawY < height) {
			let drawX = 0, ic = 0;
			while (drawX < width) {
				const slice = this.slices[ir][ic];
				ctx.drawImage(this.bg, slice.x, slice.y, this.tileW, this.tileH,
						x+drawX, y+drawY, this.tileW, this.tileH);
				drawX += this.tileW;
				// offset last column to fit inside width
				drawX = drawX <= width ? drawX : drawX + (width - drawX);
				ic = drawX + this.tileW >= width ? 2 : 1;
			}
			drawY += this.tileH;
			// offset last row to fit inside height
			drawY = drawY <= height ? drawY : drawY + (height - drawY);
			ir = drawY + this.tileH >= height ? 2 : 1;
		}
	}
}
