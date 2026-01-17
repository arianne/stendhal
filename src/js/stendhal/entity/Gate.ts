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

export class Gate extends Entity {

	override zIndex = 5000;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "resistance") {
			this["locked"] = parseInt(value, 10) !== 0;
		} else if (key === "image" || key === "orientation") {
			// Force re-evaluation of the sprite
			delete this["_image"];
		}
	}

	override buildActions(list: MenuItem[]) {
		var id = this["id"];
		list.push({
			title: (this["locked"]) ? "Open" : "Close",
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
		if (this._image == undefined) {
			var filename = Paths.sprites + "/doors/" + this["image"] + "_" + this["orientation"] + ".png";
			this._image = singletons.getSpriteStore().get(filename);
		}
		if (this._image.height) {
			var xOffset = -32 * Math.floor(this._image.width / 32 / 2);
			var height = this._image.height / 2;
			var yOffset = -32 * Math.floor(height / 32 / 2);
			var localX = this["_x"] * 32 + xOffset;
			var localY = this["_y"] * 32 + yOffset;
			var yStart = (this["locked"]) ? height : 0;
			ctx.drawImage(this._image, 0, yStart, this._image.width, height, localX, localY, this._image.width, height);
		}
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
