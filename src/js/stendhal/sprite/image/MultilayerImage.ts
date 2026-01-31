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
import { ImageFilter } from "./ImageFilter";

export class MultilayerImage {
	private images = new Map<ImageMetadata, ImageBitmap>();
	private pending: number;
	private canvas?: OffscreenCanvas;

	
	constructor(private imageMetadatas: ImageMetadata[]) {
		this.pending = imageMetadatas.length;
	}

	public async load() {
		let promises: Promise<void>[] = [];
		for (let imageMetadata of this.imageMetadatas) {
			promises.push(this.loadImage(imageMetadata));
		}

		if (promises.length) {
			await Promise.all(promises);
		} else {
			this.finish();
		}
		return this.canvas!.transferToImageBitmap();
	}

	private async loadImage(imageMetadata: ImageMetadata) {
		let response = await fetch(imageMetadata.filename);
		if (response.ok) {
			let blob = await response.blob();
			let bitmap = await createImageBitmap(blob);
			if (imageMetadata.filter) {
				let filteredBitmap = new ImageFilter().filterImage(bitmap, imageMetadata.filter, imageMetadata.color);
				bitmap.close();
				bitmap = filteredBitmap;
			}
			this.images.set(imageMetadata, bitmap);
		}
		this.pending--;
		if (this.pending <= 0) {
			this.finish();
		}
	}

	private finish() {
		let first = undefined;
		if (this.images.size) {
			first = this.images.values().next().value;
		}
		// TODO: have a global constant of an empty Canvas and an empty ImageBitmap
		this.canvas = new OffscreenCanvas(first?.width || 0, first?.height || 0);
		let ctx = this.canvas.getContext("2d")!;

		for (let imageMetadata of this.imageMetadatas) {
			let image = this.images.get(imageMetadata)!;
			ctx.drawImage(image, 0, 0);
			image.close();
		}
	}

}
