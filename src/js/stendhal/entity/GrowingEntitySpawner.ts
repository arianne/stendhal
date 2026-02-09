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
import { singletons } from "../SingletonRepo";

import { marauroa } from "marauroa"
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";


export class GrowingEntitySpawner extends Entity {
	override zIndex = 3000;


	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class") {
			this.imageSprite?.free();
			let className = value.replace(" ", "_");
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/" + className + ".png"),
				0, 0, 0, 0);
		}
	}


	/**
	 * is this entity visible to a specific action
	 *
	 * @param filter 0: short left click
	 * @return true, if the entity is visible, false otherwise
	 */
	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override buildActions(list: MenuItem[]) {
		if (!this["menu"]) {
			list.push({
				title: "Harvest",
				type: "use",
			});
		}
		super.buildActions(list);
	}

	override onclick(_x: number, _y: number) {
		var action = {
			"type": "use",
			"target": "#" + this["id"],
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

	override draw(ctx: RenderingContext2D) {
		let localX = this["x"] * 32;
		let localY = this["y"] * 32;
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}

		let count = parseInt(this["max_ripeness"], 10) + 1;
		let drawHeight = image.height / count;
		let yRow = this["ripeness"];
		ctx.drawImage(image, 0, yRow * drawHeight, image.width, drawHeight,
					localX, localY - drawHeight + 32, image.width, drawHeight);
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/harvest.png) 1 3, auto";
	}

}
