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

import { FilteredImageRefImpl } from "./FilteredImageRefImpl";
import { ImageRef } from "./ImageRef";
import { ImageRefImpl } from "./ImageRefImpl";

export class ImageManager {

	images = new Map<string, ImageRefImpl>();

	load(filename: string, filter?: string, param?: number): ImageRef {
		let key = filename + "!" + filter + "!" + param;
		let imageRef = this.images.get(key);
		if (!imageRef) {
			if (filter) {
				imageRef = new FilteredImageRefImpl(filename, filter, param);
			} else {
				imageRef = new ImageRefImpl(filename);
			}
			imageRef.load();
			this.images.set(key, imageRef);
		}
		imageRef.use();
		return imageRef;
	}

	free(filename: string, filter?: string, param?: number) {
		let key = filename + "!" + filter + "!" + param;
		let imageRef = this.images.get(key);
		if (!imageRef) {
			console.error("freeing unknown image: " + key);
			return;
		}
		imageRef.free();
	}

	cleanup(olderThan: Date) {
		for (let [key, imageRef] of this.images) {
			if (imageRef.shouldCleanup(olderThan)) {
				imageRef.close();
				this.images.delete(key);
			}
		}
	}

}

export const images = /* @__PURE__ */ new ImageManager();
