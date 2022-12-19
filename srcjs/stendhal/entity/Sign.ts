/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";

declare var stendhal: any;

export class Sign extends Entity {
	override zIndex = 5000;

	constructor() {
		super();
		this["class"] = "default";
	}

	override draw(ctx: CanvasRenderingContext2D) {
		if (!this.imagePath) {
			this.imagePath = stendhal.paths.sprites + "/signs/" + this["class"] + ".png";
		}
		var image = stendhal.data.sprites.get(this.imagePath);
		if (image.height) {
			var localX = this["x"] * 32;
			var localY = this["y"] * 32;
			ctx.drawImage(image, localX, localY);
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
