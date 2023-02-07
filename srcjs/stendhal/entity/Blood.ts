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

declare var stendhal: any;

export class Blood extends Entity {

	override minimapShow = false;
	override zIndex = 2000;

	constructor() {
		super();
		this.sprite = {
			height: 32,
			width: 32,
			filename: stendhal.paths.sprites + "/combat/blood_red.png"
		};
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "amount") {
			this.sprite.offsetY = parseInt(value, 10) * 32;
		} else if (key === "class") {
			this.sprite.filename = stendhal.paths.sprites + "/combat/blood_" + value + ".png";
		}
	}

	override getCursor(x: number, y: number) {
		return "url(" + stendhal.paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

	override drawSpriteAt(ctx: CanvasRenderingContext2D, x: number, y: number) {
		if (!stendhal.config.getBoolean("gamescreen.blood")) {
			return;
		}

		super.drawSpriteAt(ctx, x, y);
	}
}
