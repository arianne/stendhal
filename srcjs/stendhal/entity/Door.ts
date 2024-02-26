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

import { MenuItem } from "../action/MenuItem";
import { Portal } from "./Portal";

declare var marauroa: any;
declare var stendhal: any;

export class Door extends Portal {

	override zIndex = 5000;

	override draw(ctx: CanvasRenderingContext2D) {
		let imagePath = stendhal.paths.sprites + "/doors/" + this["class"] + ".png";
		let image = stendhal.data.sprites.get(imagePath);
		if (image.height) {
			let height = image.height / 2;
			let x = (this["x"] * 32) - ((image.width - 32) / 2);
			let y = (this["y"] * 32) - ((height - 32) / 2);

			var offsetY = height;
			if (this["open"] === "") {
				offsetY = 0;
			}
			ctx.drawImage(image, 0, offsetY, image.width, height, x, y, image.width, height);
		}
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
		return "url(" + stendhal.paths.sprites + "/cursor/portal.png) 1 3, auto";
	}

}
