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

import { Paths } from "../data/Paths";

declare var stendhal: any;


/**
 * Indicates that an object has an associated activity. Useful for devices that don't have an attached mouse/pointer.
 */
export class ActivityIndicatorSprite {

	private static readonly img = stendhal.data.sprites.get(Paths.sprites + "/ideas/activity.png");
	private frameIdx = 0;
	private lastFrameUpdate = 0;

	private readonly animate: boolean;


	constructor() {
		this.animate = stendhal.config.getBoolean("activity-indicator.animate");
	}

	/**
	 * Draws an indicator.
	 *
	 * TODO: add option enable/disable animation
	 *
	 * @param dx
	 *   Pixel position where parent object is drawn on X axis.
	 * @param dy
	 *   Pixel position where parent object is drawn on Y axis.
	 * @param width
	 *   Pixel width of parent object.
	 */
	public draw(ctx: CanvasRenderingContext2D, dx: number, dy: number, width: number) {
		if (!ActivityIndicatorSprite.img.complete) {
			return;
		}

		// NOTE: indicator image should be square
		const dim = ActivityIndicatorSprite.img.height;

		if (this.animate) {
			const cycleStart = Date.now();
			if (this.lastFrameUpdate == 0) {
				this.lastFrameUpdate = cycleStart;
			}

			if (cycleStart - this.lastFrameUpdate > 150) {
				this.frameIdx++;
				if ((this.frameIdx + 1) * dim >= ActivityIndicatorSprite.img.width) {
					this.frameIdx = 0;
				}
				this.lastFrameUpdate = cycleStart;
			}
		}

		// draw in upper-right of target area
		ctx.drawImage(ActivityIndicatorSprite.img, dim * this.frameIdx, 0, dim, dim, dx+width-dim, dy, dim, dim);
	}
}
