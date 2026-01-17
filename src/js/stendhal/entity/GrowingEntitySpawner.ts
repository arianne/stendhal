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


export class GrowingEntitySpawner extends Entity {
	override zIndex = 3000;

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

	/**
	 * draw RPEntities
	 */
	override draw(ctx: RenderingContext2D) {
		var localX = this["x"] * 32;
		var localY = this["y"] * 32;

		// FIXME:
		//   temporary fix, problem lies higher up
		//   appears to only affect button_mushroom_grower
		//   could be issue in marauroa
		let class_name = this["class"];
		if (class_name.includes(" ")) {
			class_name = class_name.replace(" ", "_");
		}

		var image = singletons.getSpriteStore().get(Paths.sprites + "/" + class_name + ".png");
		if (image.height) { // image.complete is true on missing image files
			var count = parseInt(this["max_ripeness"], 10) + 1;
			var drawHeight = image.height / count;
			var yRow = this["ripeness"];
			ctx.drawImage(image, 0, yRow * drawHeight, image.width, drawHeight,
					localX, localY - drawHeight + 32, image.width, drawHeight);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/harvest.png) 1 3, auto";
	}

}
