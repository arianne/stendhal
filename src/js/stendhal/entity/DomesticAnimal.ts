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

import { RPEntity } from "./RPEntity";
import { MenuItem} from "../action/MenuItem";

import { Color } from "../data/color/Color";
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";

import { marauroa } from "marauroa"

export class DomesticAnimal extends RPEntity {

	override minimapStyle = Color.DOMESTICANIMAL;

	override drawMain(ctx: RenderingContext2D) {
		if (!this.imagePath && this["_rpclass"]) {
			this["largeWeight"] = this["largeWeight"] | 20;
			if (this["_rpclass"] == "sheep") {
				this["largeWeight"] = 60;
			}
			this.imagePath = Paths.sprites + "/" + this["_rpclass"] + ".png";
		}

		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		var image = singletons.getSpriteStore().get(this.imagePath);
		if (image.height) {
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this["dir"] - 1;
			if (this["weight"] >= this["largeWeight"]) {
				yRow += 4;
			}
			this["drawHeight"] = image.height / nDirections / 2;
			this["drawWidth"] = image.width / nFrames;
			var drawX = ((this["width"] * 32) - this["drawWidth"]) / 2;
			var frame = 0;
			if (this["speed"] > 0) {
				// % Works normally with *floats* (just whose bright idea was
				// that?), so use floor() as a workaround
				frame = Math.floor(Date.now() / 100) % nFrames;
			}
			var drawY = (this["height"] * 32) - this["drawHeight"];
			ctx.drawImage(image, frame * this["drawWidth"], yRow * this["drawHeight"], this["drawWidth"], this["drawHeight"], localX + drawX, localY + drawY, this["drawWidth"], this["drawHeight"]);
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
	}

	override buildActions(list: MenuItem[]) {
		let species = "pet";
		if (this["_rpclass"] === "sheep") {
			species = "sheep"
		}
		let playerOwned = marauroa.me[species];
		if (!playerOwned) {
			list.push({
				title: "Own",
				action: function(_entity: any) {
					let action = {
						"type": "own",
						"zone": marauroa.currentZoneName,
						"target": "#" + _entity["id"]
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}
		if (playerOwned === this["id"]) {
			list.push({
				title: "Leave",
				action: function(_entity: any) {
					let action = {
						"type": "forsake",
						"zone": marauroa.currentZoneName,
						"species": species,
						"target": "#" + _entity["id"]
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}
		super.buildActions(list);
	}

}
