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

import { stendhal } from "stendhal";

export class ImageRef {
	image?: ImageBitmap;
	refCount = 0;
	lastFreed?: Date;
	closed = false;

	constructor(private filename: string) {
		// empty
	}

	async load() {
		let url = this.filename + "?v=" + stendhal.data.build.version;
		let response = await fetch(url);
		if (!response.ok || this.closed) {
			return;
		}
		let blob = await response.blob();
		if (this.closed) {
			return;
		}
		let bitmap = await createImageBitmap(blob);
		if (this.closed) {
			bitmap.close();
			return;
		}
		this.image = bitmap;
	}

	/**
	 * called internally by ImageManager
	 */
	use() {
		this.refCount++;
		this.lastFreed = undefined;
	}

	/**
	 * called internally by ImageManager
	 */
	free() {
		this.refCount--;
		if (this.refCount < 0) {
			console.error("Negative reference count", this);
		}
		if (this.refCount <= 0) {
			this.lastFreed = new Date();
		}
	}

	/**
	 * called internally by ImageManager
	 */
	close() {
		this.closed = true;
		this.image?.close();
		this.image = undefined;
	}

}
