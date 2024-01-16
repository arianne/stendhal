/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
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
 * Indicates that a corpse is not empty. Useful for devices that don't have an attached mouse/pointer.
 */
export class CorpseIndicatorSprite {

	private static readonly img = stendhal.data.sprites.get(Paths.sprites + "/ideas/containing.png");
	private frameIdx = 0;
	private lastFrameUpdate = 0;


	public draw(ctx: CanvasRenderingContext2D, dx: number, dy: number) {
		if (!CorpseIndicatorSprite.img.complete) {
			return;
		}

		const cycleStart = Date.now();
		if (this.lastFrameUpdate == 0) {
			this.lastFrameUpdate = cycleStart;
		}

		const dim = CorpseIndicatorSprite.img.height;
		if (cycleStart - this.lastFrameUpdate > 150) {
			this.frameIdx++;
			if ((this.frameIdx + 1) * dim >= CorpseIndicatorSprite.img.width) {
				this.frameIdx = 0;
			}
			this.lastFrameUpdate = cycleStart;
		}

		// adjust for center of image
		//~ const halfDim = Math.floor(dim / 2);
		//~ dx -= halfDim;
		//~ dy -= halfDim;

		ctx.drawImage(CorpseIndicatorSprite.img, dim * this.frameIdx, 0, dim, dim, dx, dy, dim, dim);
	}
}
