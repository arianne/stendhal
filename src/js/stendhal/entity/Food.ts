/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";

declare var marauroa: any;
declare var stendhal: any;


export class Food extends Entity {

	override zIndex = 5000;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "amount") {
			this._amount = parseInt(value, 10);
		}
		// TODO: play sound effect
	}

	override draw(ctx: CanvasRenderingContext2D) {
		var image = stendhal.data.sprites.get(stendhal.paths.sprites + "/food.png");
		if (image.height) {
			var localX = this["x"] * 32;
			var localY = this["y"] * 32;
			var offset = this._amount * 32;
			ctx.drawImage(image, 0, offset, 32, 32, localX, localY, 32, 32);
		}
	}

	override onclick(_x: number, _y: number) {
		var action = {
				"type": "look",
				"target": "#" + this["id"]
			};
		marauroa.clientFramework.sendAction(action);
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/look.png) 1 3, auto";
	}

}
