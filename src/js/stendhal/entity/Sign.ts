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

import { RPObject, RPZone} from "marauroa";
import { Entity } from "./Entity";
import { ActivityIndicatorSprite } from "../sprite/ActivityIndicatorSprite";
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";

import { stendhal } from "../stendhal";
import { images } from "sprite/image/ImageManager";
import { ImageSprite } from "sprite/image/ImageSprite";

export class Sign extends Entity {
	override zIndex = 5000;

	private indicator?: ActivityIndicatorSprite;

	override init() {
		super.init();
		if (!this.imageSprite) {
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/signs/" + "default" + ".png"),
				0, 0, 32, 32
			);
		}
	}

	override set(key: string, value: object) {
		super.set(key, value);
		if (key === "activity-indicator" && stendhal.config.getBoolean("activity-indicator")) {
			this.indicator = new ActivityIndicatorSprite();
		} else if (key === "class") {
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/signs/" + this["class"] + ".png"),
				0, 0, 32, 32
			);
		}
	}

	override draw(ctx: RenderingContext2D) {
		let image = this.imageSprite?.imageRef?.image;
		let localX = this["x"] * 32;
		let localY = this["y"] * 32;
		if (image) {
			ctx.drawImage(image, localX, localY);
			if (this.indicator) {
				this.indicator.draw(ctx, localX, localY, image.width);
			}
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

	override destroy(parent: RPObject|RPZone): void {
		this.indicator?.free();
		super.destroy(parent);
	}

}
