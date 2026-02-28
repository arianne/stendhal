/***************************************************************************
 *                  Copyright © 2024-2026 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { OverlaySpriteImpl } from "../OverlaySpriteImpl";

import { htmlImageStore } from "data/HTMLImageStore";
import { RenderingContext2D } from "util/Types";
import { singletons } from "../../SingletonRepo";
import { images } from "sprite/image/ImageManager";
import { ImageRef } from "sprite/image/ImageRef";


/**
 * Represents an entity animation overlay.
 */
export class SkillEffect implements OverlaySpriteImpl {

	/** Image to be drawn on canvas. */
	protected readonly imageRef: ImageRef;
	/** Time for which each frame should be displayed. */
	private readonly delay: number;
	/** Timestamp at which this sprite should expire. */
	private readonly expires: number;
	/** Timestamp at which frame cycling begins. */
	private cycleTime: number;
	/** Timestamp of most recent draw. */
	private drawTime: number;


	/**
	 * Creates a new skill effect sprite.
	 *
	 * @param {string} name
	 *   Image filename (excluding .png suffix).
	 * @param {number} [delay=100]
	 *   Time, in milliseconds, for which each frame should be displayed. Any value less than 1
	 *   results in using a default of 100ms.
	 * @param {number} [duration=0]
	 *   Time, in milliseconds, sprite drawing should persist. Any value less than 1 signifies that
	 *   sprite should not expire.
	 */
	constructor(name: string, delay=100, duration=0) {
		this.imageRef = images.load(singletons.getPaths().sprites + "/entity/overlay/" + name + ".png");
		this.cycleTime = Date.now();
		this.drawTime = 0;
		this.delay = delay > 0 ? delay : 100;
		this.expires = duration > 0 ? this.cycleTime + duration : 0;
	}

	/**
	 * Draws sprite to canvas.
	 *
	 * @param {RenderingContext2D} ctx
	 *   Canvas drawing context.
	 * @param {number} colIdx
	 *   Horizontal frame index.
	 * @param {number} x
	 *   Horizonal pixel position of where to draw on canvas.
	 * @param {number} y
	 *   Vertical pixel position of where to draw on canvas.
	 * @param {number} drawWidth
	 *   Width of each frame to draw.
	 * @param {number} drawHeight
	 *   Height of each frame to draw.
	 */
	protected drawInternal(ctx: RenderingContext2D, colIdx: number, x: number, y: number,
			drawWidth: number, drawHeight: number) {
		ctx.drawImage(this.imageRef.image!, colIdx*drawWidth, 0, drawWidth, drawHeight, 
				x, y, drawWidth, drawHeight);
	}

	draw(ctx: RenderingContext2D, x=0, y=0, drawWidth=48, drawHeight=64): boolean {
		this.drawTime = Date.now();
		if (!this.imageRef.image) {
			return this.expired();
		}

		const timeDiff = this.drawTime - this.cycleTime;
		const fcount = Math.floor(this.imageRef.image?.width / drawWidth);
		let frame = Math.floor(timeDiff / this.delay);
		if (frame >= fcount) {
			// start new frame cycle
			frame = 0;
			this.cycleTime = this.drawTime;
		}

		this.drawInternal(ctx, frame, x, y, drawWidth, drawHeight);
		return this.expired();
	}

	expired(): boolean {
		return this.expires > 0 && this.drawTime > this.expires;
	}

	free(): void {
		this.imageRef.free();
	}

}
