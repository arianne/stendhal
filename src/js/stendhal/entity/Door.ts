/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { MenuItem } from "../action/MenuItem";
import { Portal } from "./Portal";
import { Paths } from "../data/Paths";
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";

export class Door extends Portal {

	override zIndex = 5000;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class") {
			if (this.imageSprite) {
				this.imageSprite?.free();
				this.imageSprite = new ImageSprite(
					images.load(Paths.sprites + "/doors/" + value + ".png"),
					0, 0, 0, 0);
			}
		}
	}

	override draw(ctx: RenderingContext2D) {
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}
		let height = image.height / 2;
		let x = (this["x"] * 32) - ((image.width - 32) / 2);
		let y = (this["y"] * 32) - ((height - 32) / 2);

		let offsetY = height;
		if (this["open"] === "") {
			offsetY = 0;
		}
		ctx.drawImage(image, 0, offsetY, image.width, height, x, y, image.width, height);
	}

	override buildActions(list: MenuItem[]) {
		list.push({
			title: "Look",
			type: "look"
		});
		list.push({
			title: "Use",
			type: "use"
		});
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/portal.png) 1 3, auto";
	}

}
