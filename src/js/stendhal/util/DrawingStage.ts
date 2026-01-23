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

import { Canvas, RenderingContext2D } from "./Types";


/**
 * Hidden canvas for manipulating images before they are displayed.
 */
export class DrawingStage {

	/** Hidden canvas element. */
	private canvas: Canvas;
	/** Canvas's drawing context. */
	private ctx: RenderingContext2D;


	/**
	 * create new DrawingStage
	 *
	 * @param width the canvas width.
	 * @param height the canvas height.
	 */
	constructor(width: number, height: number) {
		this.canvas = new OffscreenCanvas(width, height);
		this.ctx = this.canvas.getContext("2d")!;
	}


	/**
	 * Converts canvas
	 *
	 * @returns new image
	 */
	toImage(): CanvasImageSource {
		return this.canvas;
	}

	/**
	 * Draws an image on the canvas.
	 *
	 * @param image Image to be drawn.
	 */
	drawImage(image: CanvasImageSource) {
		this.ctx.drawImage(image, 0, 0);
	}
}
