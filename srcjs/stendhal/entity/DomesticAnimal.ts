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

import { RPEntity } from "./RPEntity";

declare var stendhal: any;

export class DomesticAnimal extends RPEntity {

	override drawMain(ctx: CanvasRenderingContext2D) {
		if (!this.imagePath && this["_rpclass"]) {
			this["largeWeight"] = this["largeWeight"] | 20;
			if (this["_rpclass"] == "sheep") {
				this["largeWeight"] = 60;
			}
			this.imagePath = "/data/sprites/" + this["_rpclass"] + ".png";
		}

		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		var image = stendhal.data.sprites.get(this.imagePath);
		if (image.height) {
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this["dir"] - 1;
			if (this["weight"] >= this["largeWeight"]) {
				yRow += 4;
			}
			this["drawHeight"] = image.height / nDirections / 2;
			this["drawWidth"] = image.width / nFrames;
			var drawX = ((this["width"] * 32) - this["drawWidth"]) / 2;
			var frame = 0;
			if (this["speed"] > 0) {
				// % Works normally with *floats* (just whose bright idea was
				// that?), so use floor() as a workaround
				frame = Math.floor(Date.now() / 100) % nFrames;
			}
			var drawY = (this["height"] * 32) - this["drawHeight"];
			ctx.drawImage(image, frame * this["drawWidth"], yRow * this["drawHeight"], this["drawWidth"], this["drawHeight"], localX + drawX, localY + drawY, this["drawWidth"], this["drawHeight"]);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(/data/sprites/cursor/look.png) 1 3, auto";
	}

}
