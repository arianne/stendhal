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

import { stendhal } from "stendhal"

export class ImageCache {
	images: Record<string, ImageBitmap|undefined> = {};
	closed = false;

	load(filenames: string[]) {
		for (let filename of filenames) {
			this.loadImage(filename);
		}
	}

	private async loadImage(filename: string) {
		let url = filename + "?v=" + stendhal.data.build.version;
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
		this.images[filename] = bitmap;
	}

	close() {
		this.closed = true;
		for (let image of Object.values(this.images)) {
			image?.close();
		}
	}
}