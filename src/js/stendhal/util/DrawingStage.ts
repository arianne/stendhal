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
 * Hidden canvas for manipulating images before they are displayed.
 */
export class DrawingStage {

	/** Hidden canvas element. */
	private canvas: HTMLCanvasElement;
	/** Canvas's drawing context. */
	private ctx: CanvasRenderingContext2D;

	/** Singleton instance. */
	private static instance: DrawingStage;


	/**
	 * Retrieves the singleton instance.
	 *
	 * @returns {DrawingStage}
	 *   Drawing stage instance.
	 */
	static get(): DrawingStage {
		if (!DrawingStage.instance) {
			DrawingStage.instance = new DrawingStage();
		}
		return DrawingStage.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		this.canvas = document.getElementById("drawing-stage")! as HTMLCanvasElement;
		this.ctx = this.canvas.getContext("2d")!;
		this.reset();
	}

	/**
	 * Clears canvas & resets dimensions to 0x0.
	 */
	reset() {
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.setSize(0, 0);
	}

	/**
	 * Sets dimensions of drawing canvas.
	 *
	 * @param {number} width
	 *   New canvas width.
	 * @param {number} height
	 *   New canvas height.
	 */
	setSize(width: number, height: number) {
		this.canvas.width = width;
		this.canvas.height = height;
	}

	/**
	 * Compares canvas dimensions against image and adjusts if necessary.
	 *
	 * @param {HTMLImageElement} image
	 *   Image to compare against.
	 */
	private checkSize(image: HTMLImageElement) {
		if (this.canvas.width < image.width || this.canvas.height < image.height) {
			this.setSize(image.width, image.height);
		}
	}

	/**
	 * Converts canvas data to PNG data URL.
	 *
	 * @returns {string}
	 *   PNG data encoded string.
	 */
	toDataURL(): string {
		return this.canvas.toDataURL("image/png");
	}

	/**
	 * Converts canvas data to PNG image.
	 *
	 * @returns {HTMLImageElement}
	 *   New image element.
	 */
	toImage(): HTMLImageElement {
		const image = new Image();
		image.src = this.toDataURL();
		return image;
	}

	/**
	 * Draws an image on the canvas.
	 *
	 * @param {HTMLImageElement} image
	 *   Image to be drawn.
	 * @param {boolean} [reset=false]
	 *   If `true` erases current image data before drawing.
	 */
	drawImage(image: HTMLImageElement, reset=false) {
		if (reset) {
			this.reset();
		}
		this.checkSize(image);
		this.ctx.drawImage(image, 0, 0);
	}
}
