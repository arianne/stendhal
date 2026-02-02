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

import { images } from "./ImageManager";
import { ImageFilter } from "./ImageFilter";
import { ImageRefImpl } from "./ImageRefImpl";

export class FilteredImageRefImpl extends ImageRefImpl {

	constructor(filename: string, private filter: string, private param?: number) {
		super(filename);
	}

	override async load() {
		let originalImage = images.load(this.filename);
		await originalImage.waitFor();
		if (originalImage.image) {
			this.image = new ImageFilter().filterImage(originalImage.image, this.filter, this.param);
		}
		images.free(this.filename);
		this.promiseResolve();
	}
}
