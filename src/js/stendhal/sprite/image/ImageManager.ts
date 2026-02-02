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

import { ImageRef } from "./ImageRef";
import { ImageRefImpl } from "./ImageRefImpl";

export class ImageManager {

	images = new Map<string, ImageRefImpl>();

	load(filename: string): ImageRef {
		let imageRef = this.images.get(filename);
		if (!imageRef) {
			imageRef = new ImageRefImpl(filename);
			imageRef.load();
		}
		imageRef.use();
		return imageRef;
	}

	free(filename: string) {
		let imageRef = this.images.get(filename);
		if (!imageRef) {
			console.error("freeing unknown image: " + filename);
			return;
		}
		imageRef.free();
	}

	// TODO: consider providing a new method: free(imageRef: ImageRef)

}

export const images = /* @__PURE__ */ new ImageManager();
