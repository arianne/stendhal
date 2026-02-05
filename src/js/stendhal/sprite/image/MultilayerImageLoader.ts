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

import { ImageMetadata } from "./ImageMetadata";
import { images } from "./ImageManager";
import { ImageRef } from "./ImageRef";

export class MultilayerImageLoader {
	private imageRefs: ImageRef[] = [];

	constructor(private imageMetadatas: ImageMetadata[]) {
	}

	public async load() {
		for (let imageMetadata of this.imageMetadatas) {
			this.imageRefs.push(images.load(imageMetadata.filename, imageMetadata.filter, imageMetadata.color));
		}

		for (let imageRef of this.imageRefs) {
			await imageRef.waitFor();
		}

		let canvas: OffscreenCanvas|undefined = undefined;
		let ctx: OffscreenCanvasRenderingContext2D|undefined = undefined;
		for (let imageRef of this.imageRefs) {
			if (imageRef.image) {
				if (!canvas) {
					canvas = new OffscreenCanvas(imageRef.image.width, imageRef.image.height);
					ctx = canvas.getContext("2d")!;
				}
				ctx?.drawImage(imageRef.image, 0, 0);
				imageRef.free();
			}
		}
		if (!canvas) {
			canvas = new OffscreenCanvas(0, 0);
		}
		return canvas.transferToImageBitmap();
	}

}
