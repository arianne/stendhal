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

export class Sign extends Entity {
	override zIndex = 5000;

	private indicator?: ActivityIndicatorSprite;


	constructor() {
		super();
		this["class"] = "default";
	}

	override set(key: string, value: object) {
		super.set(key, value);
		if (key === "activity-indicator" && stendhal.config.getBoolean("activity-indicator")) {
			this.indicator = new ActivityIndicatorSprite();
		}
	}

	override draw(ctx: RenderingContext2D) {
		if (!this.imagePath) {
			this.imagePath = Paths.sprites + "/signs/" + this["class"] + ".png";
		}
		var image = singletons.getSpriteStore().get(this.imagePath);
		if (image.height) {
			var localX = this["x"] * 32;
			var localY = this["y"] * 32;
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
