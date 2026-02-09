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

import { RenderingContext2D } from "util/Types";
import { MenuItem } from "../action/MenuItem";
import { Entity } from "./Entity";
import { Paths } from "../data/Paths";

import { marauroa } from "marauroa"
import { images } from "sprite/image/ImageManager";
import { ImageSprite } from "sprite/image/ImageSprite";

export class Gate extends Entity {

	override zIndex = 5000;
	private locked = false;

	override init() {
		this.imageSprite?.free();
		this.imageSprite = new ImageSprite(
			images.load(Paths.sprites + "/doors/" + this["image"] + "_" + this["orientation"] + ".png"),
			0, 0, 0, 0);
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "resistance") {
			this.locked = parseInt(value, 10) !== 0;
		} else if (key === "image" || key === "orientation") {
			if (this.imageSprite) {
				this.imageSprite?.free();
				this.imageSprite = new ImageSprite(
					images.load(Paths.sprites + "/doors/" + this["image"] + "_" + this["orientation"] + ".png"),
					0, 0, 0, 0);
			}
		}
	}

	override buildActions(list: MenuItem[]) {
		var id = this["id"];
		list.push({
			title: (this.locked) ? "Open" : "Close",
			action: function(_entity: Entity) {
				var action = {
					"type": "use",
					"target": "#" + id,
					"zone": marauroa.currentZoneName,
				};
				marauroa.clientFramework.sendAction(action);
			}
		});
	}

	override draw(ctx: RenderingContext2D) {
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}
		let xOffset = -32 * Math.floor(image.width / 32 / 2);
		let height = image.height / 2;
		let yOffset = -32 * Math.floor(height / 32 / 2);
		let localX = this["_x"] * 32 + xOffset;
		let localY = this["_y"] * 32 + yOffset;
		let yStart = (this.locked) ? height : 0;
		ctx.drawImage(image, 0, yStart, image.width, height, localX, localY, image.width, height);
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override onclick(_x: number, _y: number) {
		var action = {
			"type": "use",
			"target": "#" + this["id"],
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/activity.png) 1 3, auto";
	}

}
