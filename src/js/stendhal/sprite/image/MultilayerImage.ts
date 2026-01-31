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

import { Canvas } from "util/Types";
import { ImageMetadata } from "./ImageMetadata";

export class MultilayerImage {
	private images = new Map<ImageMetadata, ImageBitmap>();
	private pending: number;
	private canvas?: Canvas;

	constructor(private imageMetadatas: ImageMetadata[]) {
		this.pending = imageMetadatas.length;
	}

	public async load() {
		let promises: Promise<void>[] = [];
		for (let imageMetadata of this.imageMetadatas) {
			promises.push(this.loadImage(imageMetadata));
		}
		// TODO: handle empty filenames array (with placeholder image?)
		await Promise.all(promises);
		return this.canvas;
	}

	private async loadImage(imageMetadata: ImageMetadata) {
		let response = await fetch(imageMetadata.filename);
		if (response.ok) {
			let blob = await response.blob();
			let bitmap = await createImageBitmap(blob);
			// TODO: filter image
			this.images.set(imageMetadata, bitmap);
		}
		this.pending--;
		if (this.pending <= 0) {
			this.finish();
		}
	}

	private finish() {
		// TODO: handle empty image map because all images failed to load;
		let first = this.images.values().next().value!;
		this.canvas = new OffscreenCanvas(first.width, first.height);
		let ctx = this.canvas.getContext("2d")!;

		for (let imageMetadata of this.imageMetadatas) {
			let image = this.images.get(imageMetadata)!;
			ctx.drawImage(image, 0, 0);
			image.close();
		}
	}

}
