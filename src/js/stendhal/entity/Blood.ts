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
import { Entity } from "./Entity";
import { Paths } from "../data/Paths";

import { stendhal } from "../stendhal";
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";

export class Blood extends Entity {

	override minimapShow = false;
	override zIndex = 2000;

	constructor() {
		super();
		this.imageSprite = new ImageSprite(
			images.load(Paths.sprites + "/combat/blood_red.png"),
			0, 0, 32, 32
		);
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "amount") {
			this.imageSprite!.offsetY = parseInt(value, 10) * 32;
		} else if (key === "class") {
			this.imageSprite?.free();
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/combat/blood_" + value + ".png"),
				0, parseInt(this["amount"], 10) * 32, 32, 32
			);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

	override drawSpriteAt(ctx: RenderingContext2D, x: number, y: number) {
		if (!stendhal.config.getBoolean("effect.blood")) {
			return;
		}

		super.drawSpriteAt(ctx, x, y);
	}
}
