/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { ImageRef } from "./ImageRef";


export class ImageSprite {

	constructor(
		public imageRef: ImageRef,
		public offsetX?: number,
		public offsetY?: number,
		public width?: number,
		public height?: number
	) { }

	drawOnto(ctx: RenderingContext2D, x: number, y: number, entityWidth: number, entityHeight: number) {
		let image = this.imageRef.image;
		if (!image) {
			return;
		}
		let offsetX = this.offsetX || 0;
		let offsetY = this.offsetY || 0;
		let width = this.width || image.width;
		let height = this.height || image.height;

		// use entity dimensions to center sprite
		x += Math.floor((entityWidth - width) / 2);
		y += Math.floor((entityHeight - height) / 2);

		ctx.drawImage(image, offsetX, offsetY, width, height, x, y, width, height);
	}

	public free() {
		this.imageRef.free();
	}

}
