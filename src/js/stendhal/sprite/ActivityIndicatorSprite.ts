/***************************************************************************
 *                  Copyright © 2024-2026 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";

import { stendhal } from "../stendhal";
import { ImageRef } from "./image/ImageRef";
import { images } from "./image/ImageManager";


/**
 * Indicates that an object has an associated activity. Useful for devices that don't have an attached mouse/pointer.
 */
export class ActivityIndicatorSprite {

	private imageRef: ImageRef;
	private frameIdx = 0;
	private lastFrameUpdate = 0;

	private readonly animate: boolean;


	constructor() {
		this.animate = stendhal.config.getBoolean("activity-indicator.animate");
		this.imageRef = images.load(Paths.sprites + "/ideas/activity.png");
	}

	/**
	 * Draws an indicator.
	 *
	 * @param dx
	 *   Pixel position where parent object is drawn on X axis.
	 * @param dy
	 *   Pixel position where parent object is drawn on Y axis.
	 * @param width
	 *   Pixel width of parent object.
	 */
	public draw(ctx: RenderingContext2D, dx: number, dy: number, width: number) {
		let image = this.imageRef.image;
		if (!image) {
			return;
		}

		// NOTE: indicator image should be square
		let dim = image.height;

		if (this.animate) {
			let cycleStart = Date.now();
			if (this.lastFrameUpdate == 0) {
				this.lastFrameUpdate = cycleStart;
			}

			if (cycleStart - this.lastFrameUpdate > 150) {
				this.frameIdx++;
				if ((this.frameIdx + 1) * dim >= image.width) {
					this.frameIdx = 0;
				}
				this.lastFrameUpdate = cycleStart;
			}
		}

		// draw in upper-right of target area
		ctx.drawImage(image, dim * this.frameIdx, 0, dim, dim, dx+width-dim, dy, dim, dim);
	}

	free() {
		this.imageRef.free();
	}
}
