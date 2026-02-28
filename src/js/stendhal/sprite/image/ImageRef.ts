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

export abstract class ImageRef {
	image?: ImageBitmap;

	async /*abstract */ waitFor(): Promise<void> {
		return new Promise<void>(() => {});
	}

	drawOnto(ctx: RenderingContext2D, x: number, y: number) {
		if (this.image) {
			ctx.drawImage(this.image, x, y);
		}
	}

	abstract free(): void;
}
